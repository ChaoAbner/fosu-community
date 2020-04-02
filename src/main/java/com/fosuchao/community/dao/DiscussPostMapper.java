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
}
