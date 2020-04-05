package com.fosuchao.community.dao;

import com.fosuchao.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 查询用户的会话列表，针对每个会话返回一条最新的私信
    List<Message> selectConversations(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    // 查询用户的会话数量
    int selectConversationsCount(@Param("userId") int userId);

    // 查询某个会话中的私信列表
    List<Message> selectLetters(@Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);

    // 查询某个会话中的私信数量
    int selectLettersCount(@Param("conversationId") String conversationId);

    // 查询某个会话的消息未读数量
    int selectConversationUnReadCount(@Param("conversationId") String conversationId, @Param("userId") int userId);

    // 新增消息
    void insertLetter(Message message);

    // 修改某个会话中消息的状态
    void updateConversationStatus(@Param("conversationId") String conversationId, @Param("status") int status, @Param("userId") int userId);

    // 查找某主题下最新的通知
    Message selectLastestNotice(@Param("userId") int userId, @Param("topic") String topic);

    // 查找某主题的通知数量
    int selectNoticeCount(@Param("userId") int userId, @Param("topic") String topic);

    // 查找某主题的未读通知数量
    int selectNoticeUnreadCount(@Param("userId") int userId, @Param("topic") String topic);

    // 查找某个主题的通知
    List<Message> selectNotices(@Param("userId") int userId, @Param("topic") String topic, @Param("offset") int offset, @Param("limit") int limit);
}
