package com.fosuchao.community.dao;


import com.fosuchao.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    // 查询post
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId,
                                         @Param("offset") int offset, @Param("limit") int limit);

    // 查询post的总行数
    int selectDiscussPostsRows(@Param("userId") int userId);

    // 插入post
    void insertDiscussPost(DiscussPost post);

    // 查询post
    DiscussPost selectDiscussPostById(@Param("id") int id);

    // 更新评论数
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    // 修改状态
    int updatePostStatus(@Param("id") int id, @Param("status") int status);

    // 修改文章type
    int updatePostType(@Param("id") int id, @Param("type") int type);
}
