package com.fosuchao.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/5 10:32
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class KafkaTest {

    @Autowired
    Producer producer;

    @Test
    public void kafkaTest() {
        producer.send("test", "你好");
        producer.send("test", "在吗");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

@Component
class Producer {
    @Autowired
    KafkaTemplate kafkaTemplate;

    public void send(String topic, String content) {
        kafkaTemplate.send(topic, content);
    }
}

@Component
class Comsumer {

    @KafkaListener(topics = {"test"})
    public void handler(ConsumerRecord record) {
        System.out.println("处理消息：" + record.value().toString());
    }
}