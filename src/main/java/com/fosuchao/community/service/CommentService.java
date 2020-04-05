package com.fosuchao.community.service;

import com.fosuchao.community.dao.CommentMapper;
import com.fosuchao.community.entity.Comment;
import com.fosuchao.community.utils.SensitiveFilterUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

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

    @Autowired
    SensitiveFilterUtil sensitiveFilterUtil;

    public List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int selectCountByEntity(int entityId, int entityType) {
        return commentMapper.selectCountByEntity(entityId, entityType);
    }

    public void insertComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        // 转义html语义
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        // 过滤敏感词
        comment.setContent(sensitiveFilterUtil.filter(comment.getContent()));

        commentMapper.insertComment(comment);
    }

    public Comment selectById(int id) {
        return commentMapper.selectById(id);
    }
}
