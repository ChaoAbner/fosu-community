package com.fosuchao.community.service;

import com.fosuchao.community.dao.DiscussPostMapper;
import com.fosuchao.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 15:55
 */

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    // 查询用户的post
    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    // 查询post的总行数
    public int selectDiscussPostsRows(int userId) {
        return discussPostMapper.selectDiscussPostsRows(userId);
    }

    // 查询post
    public DiscussPost selectDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    // 更新评论数
    public int updateCommentCount( int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    // 插入文章
    public void insertDiscussPost(DiscussPost post) {
        discussPostMapper.insertDiscussPost(post);
    }
}
