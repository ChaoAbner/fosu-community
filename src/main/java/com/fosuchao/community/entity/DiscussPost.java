package com.fosuchao.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 14:39
 */
@Data
public class DiscussPost {

    private int id;

    private int userId;

    private String title;

    private String content;

    private int type;

    private int status;

    private Date createTime;

    private Double score;

    private int commentCount;
}
