package com.fosuchao.community.controller;

import com.fosuchao.community.entity.DiscussPost;
import com.fosuchao.community.entity.Page;
import com.fosuchao.community.entity.User;
import com.fosuchao.community.service.DiscussPostService;
import com.fosuchao.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 14:35
 */

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @GetMapping(path = "/")
    public String root() {
        return "forward:/index";
    }

    @GetMapping(path = "/index")
    public String getIndexPage(Model model, Page page) {

        page.setRows(discussPostService.selectDiscussPostsRows(0));
        page.setPath("/index");

        List<DiscussPost> list =
                discussPostService.selectDiscussPosts(0, page.getOffset(), page.getLimit());

        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                User user = userService.selectById(post.getUserId());
                map.put("user", user);
                map.put("post", post);
                discussPosts.add(map);
            }
        }

        model.addAttribute("discussPosts", discussPosts);
        return "/index";
    }

    @GetMapping(path = "error404")
    public String error404() {
        return "/site/error/404";
    }

    @GetMapping(path = "error500")
    public String error500() {
        return "/site/error/500";
    }
}