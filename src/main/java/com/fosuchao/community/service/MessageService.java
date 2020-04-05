package com.fosuchao.community.service;

import com.fosuchao.community.dao.MessageMapper;
import com.fosuchao.community.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/3 21:58
 */

@Service
public class MessageService {

    @Autowired
    MessageMapper messageMapper;

    // 查询用户的会话列表，针对每个会话返回一条最新的私信
    public List<Message> selectConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    // 查询用户的会话数量
    public int selectConversationsCount(int userId) {
        return messageMapper.selectConversationsCount(userId);
    }

    // 查询某个会话中的私信列表
    public List<Message> selectLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    // 查询某个会话中的私信数量
    public int selectLettersCount(String conversationId) {
        return messageMapper.selectLettersCount(conversationId);
    }

    // 查询某个会话的消息未读数量
    public int selectConversationUnReadCount(String conversationId, int userId) {
        return messageMapper.selectConversationUnReadCount(conversationId, userId);
    }

    // 新增消息
    public void insertLetter(Message message) {
        messageMapper.insertLetter(message);
    }

    // 修改某个会话中消息的状态
    public void updateConversationStatus(String conversationId, int status, int userId) {
        messageMapper.updateConversationStatus(conversationId, status, userId);
    }

    // 查找某主题下最新的通知
    public Message selectLastestNotice(int userId, String topic) {
        return messageMapper.selectLastestNotice(userId, topic);
    }

    // 查找某主题的通知数量
    public int selectNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    // 查找某主题的未读通知数量
    public int selectNoticeUnreadCount(int userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    // 查找某个主题的通知
    public List<Message> selectNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }
}
