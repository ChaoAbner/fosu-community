package com.fosuchao.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 14:44
 */

@Data
public class Comment {

    private int id;

    private int userId;

    private int entityType;

    private int entityId;

    private int targetId;

    private String content;

    private int status;

    private Date createTime;
}
