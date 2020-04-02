package com.fosuchao.community.controller;

import com.fosuchao.community.entity.User;
import com.fosuchao.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 18:59
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;




}
