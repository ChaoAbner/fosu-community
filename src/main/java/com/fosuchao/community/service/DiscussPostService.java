package com.fosuchao.community.service;

import com.fosuchao.community.dao.DiscussPostMapper;
import com.fosuchao.community.entity.DiscussPost;
import com.fosuchao.community.utils.RedisKeyUtil;
import com.fosuchao.community.utils.SensitiveFilterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

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

    @Autowired
    SensitiveFilterUtil sensitiveFilterUtil;

    @Autowired
    RedisTemplate redisTemplate;

    // 查询用户的post
    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
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

    // 更新帖子分数
    public int updatePostScore(int id, double score) {
        return discussPostMapper.updatePostScore(id, score);
    }

    // 修改状态,加精，置顶，热帖等
    public int updatePostStatus(int id, int status) {
        return discussPostMapper.updatePostStatus(id, status);
    }

    // 修改文章type，删除等
    public int updatePostType(int id, int type) {
        return discussPostMapper.updatePostType(id, type);
    }

    // 插入文章
    public void insertDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        // 转义html语义
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        // 过滤敏感词
        post.setContent(sensitiveFilterUtil.filter(post.getContent()));
        post.setTitle(sensitiveFilterUtil.filter(post.getTitle()));

        discussPostMapper.insertDiscussPost(post);
    }

    // 变动的帖子缓存集合，需要刷新分数
    public void setChangePostSet(int postId) {
        String postScoreKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(postScoreKey, postId);
    }

    // 获取缓存集合
    public BoundSetOperations getChangePostSet() {
        String postScoreKey = RedisKeyUtil.getPostScoreKey();
        return redisTemplate.boundSetOps(postScoreKey);
    }
}
