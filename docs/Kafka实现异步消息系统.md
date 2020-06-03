# Kafka实现异步消息系统

再学习Kafka之前，先来了解下阻塞队列，并且实现生产者消费者模式

Java中最常见的阻塞队列，BlockingQueue。

![](http://img.fosuchao.com/20200405174541.png)

**生产者消费者模式**

```java
@SpringBootTest
@RunWith(SpringRunner.class)
public class BlockingQueueTest {
    @Test
    public void modeTest() {
        ArrayBlockingQueue<Integer> integers = new ArrayBlockingQueue<>(10);

        new Thread(new MessageProducer(integers)).start();
        new Thread(new MessageConsumer(integers)).start();
        new Thread(new MessageConsumer(integers)).start();
        new Thread(new MessageConsumer(integers)).start();
    }
}

class MessageProducer implements Runnable{

    BlockingQueue queue;

    public MessageProducer(BlockingQueue queue) {
        this.queue = queue;
    }

    public void produce() {
        for (int i = 0; i < 100; i++) {
            System.out.println("生产消息：" + i);
            try {
                queue.put("message-" + i);
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        produce();
    }
}

class MessageConsumer implements Runnable{

    BlockingQueue queue;

    public MessageConsumer(BlockingQueue queue) {
        this.queue = queue;
    }

    public void consume() {
        while (true) {
            try {
                Thread.sleep(new Random().nextInt(1000));
                System.out.println("消费消息：" + queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        consume();
    }
}
```

## Kafka

### 入门

[文档链接](http://kafka.apache.org/)

![](http://img.fosuchao.com/20200405184153.png)

- Broker：就是Kafka服务器。因为Kafka是支持分布式部署的。
- Zookeeper：注册中心，Kafka依赖Zookeeper管理
- Topic：主题，对应一个主题来生产发布消息
- Partition：分段，一个主题可以有多个分段（队列），多线程消费消息。

![](http://img.fosuchao.com/20200405184627.png)

![](http://img.fosuchao.com/20200405184640.png)

### Spring整合Kafka

![](http://img.fosuchao.com/20200405184706.png)

生产者需要主动调用，将相应的消息发送到相应的主题。

消费者属于被动调用，每个消费者可能监听不同的主题，当对应主题的队列中有消息时。则进行消费。

## 系统通知

### 触发事件

- 评论后
- 点赞后
- 关注后
- 系统消息
- 等等

![](http://img.fosuchao.com/20200405185122.png)

### 处理事件

- 封装事件对象（Event）
- 开发事件的生产者
- 开发事件的消费者

注意: 对象的生产和消费可能需要涉及到序列化操作，可以依赖alibaba的fastjson。