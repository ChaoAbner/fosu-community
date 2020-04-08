package com.fosuchao.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @description: 生产者消费者模式
 * @author: Joker Ye
 * @create: 2020/4/5 17:46
 */

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
