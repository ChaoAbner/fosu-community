package com.fosuchao.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 14:38
 */
@Data
public class User {

    private int id;

    private String username;

    private String password;

    private String salt;

    private String email;

    private int type;

    private int status;

    private String activationCode;

    private String headerUrl;

    private Date createTime;
}
