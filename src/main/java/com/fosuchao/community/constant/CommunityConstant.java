package com.fosuchao.community.constant;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 20:13
 */
public interface CommunityConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 激活失败
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 重复
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态的登录凭证的超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住状态的登录凭证超时时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 30;

    /**
     * 实体类型：帖子
     */
    int COMMENT_ENTITY = 1;

    /**
     * 实体类型：回复
     */
    int REPLY_ENTITY = 2;

    /**
     * 实体类型：用户
     */
    int USER_ENTITY = 3;

    /**
     * 主题：点赞
     */
    String LIKE_TOPIC = "like";

    /**
     * 主题：点赞
     */
    String COMMENT_TOPIC = "comment";

    /**
     * 主题：点赞
     */
    String FOLLOW_TOPIC = "follow";

    /**
     * 主题：邮件
     */
    String EMIAL_TOPIC = "email";

    /**
     * 系统用户
     */
    int SYSTEM_USER = 1;
}
