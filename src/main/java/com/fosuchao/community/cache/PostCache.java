package com.fosuchao.community.cache;

import com.fosuchao.community.dao.DiscussPostMapper;
import com.fosuchao.community.entity.DiscussPost;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.PostconditionAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @description: 帖子缓存
 * @author: Joker Ye
 * @create: 2020/4/7 11:42
 */

@Component
public class PostCache {

    private static final Logger logger = LoggerFactory.getLogger(PostCache.class);

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Value("${caffeine.post.max-size}")
    private int postMaxSize;

    @Value("${caffeine.post.expire-seconds}")
    private int expired;

    public LoadingCache<String, List<DiscussPost>> postListLoad() {
        return Caffeine.newBuilder()
                .maximumSize(postMaxSize)
                .expireAfterWrite(expired, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String s) throws Exception {
                        if (StringUtils.isBlank(s)) {
                            throw new IllegalArgumentException("参数错误");
                        }
                        String[] split = s.split(":");
                        if (split.length != 2) {
                            throw new IllegalArgumentException("参数错误");
                        }

                        // 取出offset和limit
                        int offset = Integer.valueOf(split[0]);
                        int limit = Integer.valueOf(split[1]);

                        // 二级缓存：redis TODO

                        // 走数据库
                        logger.info("load post list from DB.");
                        return discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
                    }
                });
    }

    public LoadingCache<Integer, Integer> postRowsLoad() {
        return Caffeine.newBuilder()
                .maximumSize(postMaxSize)
                .expireAfterWrite(expired, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        logger.debug("load post rows from DB.");

                        return discussPostMapper.selectDiscussPostsRows(key);
                    }
                });
    }
}
