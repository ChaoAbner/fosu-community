package com.fosuchao.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @description: 登录凭证
 * @author: Joker Ye
 * @create: 2020/4/2 18:37
 */
@Data
public class LoginTicket {
    private int id;

    private int userId;

    private String ticket;

    private int status;

    private Date expired;
}
