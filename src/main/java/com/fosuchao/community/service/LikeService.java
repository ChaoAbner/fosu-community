package com.fosuchao.community.service;

import com.fosuchao.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/4 17:45
 */

@Service
public class LikeService {

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 对实体赞操作
     * @Param [entityType, entityId, userId, entityUserId]
     * @return void
     */
    public void like(int entityType, int entityId, int userId, int entityUserId) {
        // 实体添加/取消赞
        // 被点赞用户增加赞的数量
        // 进行事务操作
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                // 判断用户点赞状态
                boolean status = operations.opsForSet().isMember(entityLikeKey, userId);
                // 开启事务
                operations.multi();
                if (status) {
                    // 取消赞，赞-1
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    // 点赞，赞+1
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }

                return operations.exec();
            }
        });
    }

    /**
     * 查询某个实体点赞的数量
     * @Param [entityType, entityId]
     * @return int
     */
    public long getEntityLikeCount(int entityType, int entityId) {
        String key = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 获取某个人对实体的点赞状态
     * @Param [entityType, entityId, userId]
     * @return boolean
     */
    public int getEntityLikeStatus(int entityType, int entityId, int userId) {
        String key = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(key, userId) ? 1 : 0;
    }

    /**
     * 查询用户获得的点赞数量
     * @Param [userId]
     * @return long
     */
    public int getUserlikeCount(int userId) {
        String key = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(key);
        return count == null ? 0 : count.intValue();
    }
}
