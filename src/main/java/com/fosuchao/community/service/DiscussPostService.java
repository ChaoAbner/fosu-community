package com.fosuchao.community.service;

import com.fosuchao.community.cache.PostCache;
import com.fosuchao.community.dao.DiscussPostMapper;
import com.fosuchao.community.entity.DiscussPost;
import com.fosuchao.community.utils.RedisKeyUtil;
import com.fosuchao.community.utils.SensitiveFilterUtil;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 15:55
 */

@Service
public class DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    SensitiveFilterUtil sensitiveFilterUtil;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    PostCache postCache;

    // Caffeine核心接口: Cache, LoadingCache, AsyncLoadingCache

    // 帖子列表
    private LoadingCache<String, List<DiscussPost>> postsCache;

    // 帖子行数
    private LoadingCache<Integer, Integer> postRowsCache;

    /**
     * 初始化热门帖子的缓存
     *
     * @return void
     * @Param []
     */
    @PostConstruct
    public void initPostsCache() {
        // 初始化帖子列表缓存的方式
        postsCache = postCache.postListLoad();

        // 初始化帖子行数的方式
        postRowsCache = postCache.postRowsLoad();
    }

    // 查询用户的post
    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode) {
        if (userId == 0 && orderMode == 1) {
            // 走缓存
            String key = offset + ":" + limit;
            return postsCache.get(key);
        }
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    // 查询post的总行数
    public int selectDiscussPostsRows(int userId) {
        if (userId == 0) {
            // 走缓存
            return postRowsCache.get(userId);
        }
        return discussPostMapper.selectDiscussPostsRows(userId);
    }

    // 查询post
    public DiscussPost selectDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    // 更新评论数
    public int updateCommentCount(int id, int commentCount) {
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
