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
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 7;

    /**
     * 实体类型：帖子
     */
    int POST_ENTITY = 1;

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
    String EMAIL_TOPIC = "email";

    /**
     * 主题：帖子发布，修改
     */
    String PUBLISH_TOPIC = "publish";

    /**
     * 主题：帖子删除
     */
    String DELETE_TOPIC = "delete";

    /**
     * 系统用户
     */
    int SYSTEM_USER_ID = 1;

    /**
     * 权限：普通用户
     */
    String AUTHORITY_USER = "user";

    /**
     * 权限：版主
     */
    String AUTHORITY_MODERATOR = "moderator";

    /**
     * 权限：管理员
     */
    String AUTHORITY_ADMIN = "admin";


}
