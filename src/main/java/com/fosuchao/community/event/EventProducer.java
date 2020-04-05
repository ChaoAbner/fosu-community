package com.fosuchao.community.event;

import com.alibaba.fastjson.JSONObject;
import com.fosuchao.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/5 11:18
 */
@Component
public class EventProducer {

    @Autowired
    KafkaTemplate kafkaTemplate;

    public void fireEvent(Event event) {
        // 将事件发布到指定的主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }

}
