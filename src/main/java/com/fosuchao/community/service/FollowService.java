package com.fosuchao.community.service;

import com.fosuchao.community.constant.CommunityConstant;
import com.fosuchao.community.entity.User;
import com.fosuchao.community.utils.HostHolder;
import com.fosuchao.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/4 17:46
 * @noinspection ALL
 */

@Service
public class FollowService implements CommunityConstant {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserService userService;

    // 加关注
    public void follow(int entityType, int entityId, int userId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);

                operations.multi();

                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    // 取消关注
    public void unfollow(int entityType, int entityId, int userId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);

                operations.multi();

                operations.opsForZSet().remove(followerKey, userId);
                operations.opsForZSet().remove(followeeKey, entityId);

                return operations.exec();
            }
        });
    }

    // 获取实体的粉丝数量
    public Long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    // 查看用户关注的实体数量
    public Long getFolloweeCount(int entityType, int userId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    // 查看用户是否关注当前实体
    public boolean hasFollowed(int entityType, int entityId, int userId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    // 查询某个用户的关注的人
    public List<Map<String, Object>> getFollowees(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(USER_ENTITY, userId);

        Set<Integer> ids = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, limit + offset - 1);
        if (ids == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();

        for (Integer id : ids) {
            HashMap<String, Object> map = new HashMap<>();
            User user = userService.selectById(id);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, id);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }

        return list;
    }

    // 查询某个用户的粉丝
    public List<Map<String, Object>> getFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(USER_ENTITY, userId);

        Set<Integer> ids = redisTemplate.opsForZSet().reverseRange(followerKey, offset, limit + offset - 1);
        if (ids == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();

        for (Integer id : ids) {
            HashMap<String, Object> map = new HashMap<>();
            User user = userService.selectById(id);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey, id);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }

        return list;
    }
}
