package com.fosuchao.community.controller;

import com.fosuchao.community.constant.CommunityConstant;
import com.fosuchao.community.entity.DiscussPost;
import com.fosuchao.community.entity.Page;
import com.fosuchao.community.entity.User;
import com.fosuchao.community.service.DiscussPostService;
import com.fosuchao.community.service.LikeService;
import com.fosuchao.community.service.MessageService;
import com.fosuchao.community.service.UserService;
import com.fosuchao.community.utils.HostHolder;
import com.fosuchao.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
public class HomeController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private MessageService messageService;

    @Autowired
    HostHolder hostHolder;

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
                long likeCount = likeService.getEntityLikeCount(POST_ENTITY, post.getId());
                map.put("user", user);
                map.put("post", post);
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }

        model.addAttribute("discussPosts", discussPosts);
        return "/index";
    }

    @GetMapping(path = "error404")
    public String error404() {
        return "/error/404";
    }

    @GetMapping(path = "error")
    public String error500() {
        return "/error/500";
    }
}
