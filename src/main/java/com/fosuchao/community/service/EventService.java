package com.fosuchao.community.service;

import com.fosuchao.community.constant.CommunityConstant;
import com.fosuchao.community.entity.Comment;
import com.fosuchao.community.entity.DiscussPost;
import com.fosuchao.community.entity.Event;
import com.fosuchao.community.event.EventProducer;
import com.fosuchao.community.utils.HostHolder;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @description: 事件触发服务
 * @author: Joker Ye
 * @create: 2020/4/6 16:40
 */

@Service
public class EventService implements CommunityConstant{

    @Autowired
    EventProducer eventProducer;

    @Autowired
    HostHolder hostHolder;

    public void publishPost(int postId) {
        Event event = new Event()
                .setTopic(PUBLISH_TOPIC)
                .setEntityType(POST_ENTITY)
                .setEntityId(postId);
        eventProducer.fireEvent(event);
    }

    public void publishPost(DiscussPost post) {
        Event event = new Event()
                .setTopic(PUBLISH_TOPIC)
                .setEntityType(POST_ENTITY)
                .setEntityId(post.getId())
                .setUserId(post.getUserId());
        eventProducer.fireEvent(event);
    }

    public void deletePost(DiscussPost post) {
        Event event = new Event()
                .setTopic(DELETE_TOPIC)
                .setEntityType(POST_ENTITY)
                .setEntityId(post.getId())
                .setUserId(post.getUserId());
        eventProducer.fireEvent(event);
    }

    public void like(int entityId, int entityType, int entityUserId, int postId) {
        Event event = new Event();
        event.setTopic(LIKE_TOPIC)
                .setEntityId(entityId)
                .setEntityType(entityType)
                .setUserId(hostHolder.getUser().getId())
                .setEntityUserId(entityUserId)
                .setData("postId", postId);
        eventProducer.fireEvent(event);
    }

    public void follow(int entityId, int entityType) {
        Event event = new Event();
        event.setTopic(FOLLOW_TOPIC)
                .setEntityId(entityId)
                .setEntityType(entityType)
                .setUserId(hostHolder.getUser().getId())
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);
    }

    public void comment(Comment comment, int postId, int entityUserId) {
        Event event = new Event();
        event.setTopic(COMMENT_TOPIC)
                .setEntityId(comment.getId())
                .setEntityType(comment.getEntityType())
                .setUserId(hostHolder.getUser().getId())
                .setEntityUserId(entityUserId)
                .setData("postId", postId);
        eventProducer.fireEvent(event);
    }

    public void email(String to, String Subject, String content) {
        Event event = new Event();
        event.setTopic(EMAIL_TOPIC);
        event.setData("email", to);
        event.setData("subject", Subject);
        event.setData("content", content);
        eventProducer.fireEvent(event);
    }
}
