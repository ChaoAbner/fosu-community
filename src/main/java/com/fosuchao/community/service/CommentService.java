package com.fosuchao.community.service;

import com.fosuchao.community.dao.CommentMapper;
import com.fosuchao.community.entity.Comment;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.CommandMap;
import java.util.List;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/3 18:30
 */

@Service
public class CommentService {

    @Autowired
    CommentMapper commentMapper;

    public List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int selectCountByEntity(int entityId, int entityType) {
        return commentMapper.selectCountByEntity(entityId, entityType);
    }

    public void insertComment(Comment comment) {
        commentMapper.insertComment(comment);
    }

    public Comment selectById(int id) {
        return commentMapper.selectById(id);
    }
}
