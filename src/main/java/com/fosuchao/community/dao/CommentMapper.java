package com.fosuchao.community.dao;


import com.fosuchao.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(@Param("entityType") int entityType,
                                         @Param("entityId") int entityId,
                                         @Param("offset") int offset, @Param("limit") int limit);

    int selectCountByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    void insertComment(Comment comment);

    Comment selectById(@Param("id") int id);
}
