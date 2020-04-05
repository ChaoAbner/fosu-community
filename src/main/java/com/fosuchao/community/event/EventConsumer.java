package com.fosuchao.community.event;

import com.alibaba.fastjson.JSONObject;
import com.fosuchao.community.constant.CommunityConstant;
import com.fosuchao.community.entity.Event;
import com.fosuchao.community.entity.Message;
import com.fosuchao.community.service.MessageService;
import com.fosuchao.community.utils.MailUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/5 11:18
 */
@Component
public class EventConsumer implements CommunityConstant{

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    MessageService messageService;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    MailUtil mailUtil;

    /**
     * 处理点赞，评论，关注消息
     * @Param [record]
     * @return void
     */
    @KafkaListener(topics = {LIKE_TOPIC, COMMENT_TOPIC, FOLLOW_TOPIC})
    public void handleSocialMessage(ConsumerRecord record) {
        if (!isValid(record))
            return ;
        // 将消息字符串转化成Event
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息的格式错误");
            return ;
        }
        // 发送站内通知,将相应的数据插入message表
        Message message = new Message();
        message.setConversationId(event.getTopic());
        message.setToId(event.getEntityUserId());
        message.setFromId(SYSTEM_USER);
        message.setCreateTime(new Date());

        HashMap<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.insertLetter(message);
    }

    @KafkaListener(topics = {EMIAL_TOPIC})
    public void handlerEmailMessage(ConsumerRecord record) {
        if (!isValid(record))
            return ;
        // 将消息字符串转化成Event
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息的格式错误");
            return ;
        }
        // 发送邮件
        String email = (String) event.getData().get("email");
        String subject = (String) event.getData().get("subject");
        String content = (String) event.getData().get("content");
        mailUtil.sendCompanyMail(email, subject, content);
    }

    public boolean isValid(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!！");
            return false;
        }
        return true;
    }
}
