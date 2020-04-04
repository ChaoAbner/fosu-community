package com.fosuchao.community.utils;

import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/4 17:46
 */

@Component
public class RedisKeyUtil {

    private static final String SPLIT = ":";

    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    private static final String PREFIX_USER_LIKE = "like:user";

    private static final String PREFIX_FOLLOWER = "follower";

    private static final String PREFIX_FOLLOWEE = "followee";

    private static final String PREFIX_KAPTCHA = "kaptcha";

    private static final String PREFIX_TICKET = "ticket";

    private static final String PREFIX_USER = "user";


    /**
     * 某个实体的赞
     * @Param [entityType, entityId]
     * @return like:entity:entityType:entityId
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 某个用户的赞
     * @Param [userId]
     * @return like:user:userId
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 某个实体的粉丝
     * @Param [entityId, entityType]
     * @return java.lang.String
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 某个用户关注的实体
     * @Param [userId, entityType]
     * @return java.lang.String
     */
    public static String getFolloweeKey(int entityType, int userId) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 用户的验证码
     * @Param [userId]
     * @return java.lang.String
     */
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * ticket
     * @Param [ticket]
     * @return java.lang.String
     */
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 获取用户
     * @Param [ticket]
     * @return java.lang.String
     */
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }
}
