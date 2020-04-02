package com.fosuchao.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 14:44
 */

@Data
public class Message {
    private int id;

    private int fromId;

    private int toId;

    private String conversationId;

    private String content;

    private int status;

    private Date createTime;
}
