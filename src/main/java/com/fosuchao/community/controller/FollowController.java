package com.fosuchao.community.controller;

import com.fosuchao.community.constant.CommunityConstant;
import com.fosuchao.community.entity.Event;
import com.fosuchao.community.entity.Page;
import com.fosuchao.community.entity.User;
import com.fosuchao.community.event.EventProducer;
import com.fosuchao.community.service.FollowService;
import com.fosuchao.community.service.UserService;
import com.fosuchao.community.utils.HostHolder;
import com.fosuchao.community.utils.JsonResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.net.ssl.HostnameVerifier;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/4 21:06
 */

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    /**
     * TODO: 权限控制
     * @Param [entityType, entityId]
     * @return java.lang.String
     */
    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {
        followService.follow(entityType, entityId, hostHolder.getUser().getId());

        // 触发关注事件
        Event event = new Event();
        event.setTopic(FOLLOW_TOPIC)
                .setEntityId(entityId)
                .setEntityType(entityType)
                .setUserId(hostHolder.getUser().getId())
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return JsonResponseUtil.getJsonResponse(0, "已关注！");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unfollow(entityType, entityId, user.getId());
        return JsonResponseUtil.getJsonResponse(0, "已取消关注");
    }

    @GetMapping("/followers/{userId}")
    public String getFollower(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.selectById(userId);
        if (user == null || user.getStatus() != 1) {
            throw new IllegalArgumentException("用户不存在");
        }

        page.setPath("/followers/" + userId);
        page.setLimit(5);
        page.setRows(followService.getFollowerCount(USER_ENTITY, userId).intValue());

        List<Map<String, Object>> followers = followService.getFollowers(userId, page.getOffset(), page.getLimit());

        if (followers != null) {
            for (Map<String, Object> follower : followers) {
                User u = (User) follower.get("user");
                follower.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", followers);
        model.addAttribute("user", hostHolder.getUser());

        return "/site/follower";
    }

    @GetMapping("/followees/{userId}")
    public String getFollowee(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.selectById(userId);
        if (user == null || user.getStatus() != 1) {
            throw new IllegalArgumentException("用户不存在");
        }

        page.setPath("/followees/" + userId);
        page.setLimit(5);
        page.setRows(followService.getFollowerCount(USER_ENTITY, userId).intValue());

        List<Map<String, Object>> followees = followService.getFollowees(userId, page.getOffset(), page.getLimit());

        if (followees != null) {
            for (Map<String, Object> followee : followees) {
                User u = (User) followee.get("user");
                followee.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", followees);
        model.addAttribute("user", hostHolder.getUser());

        return "/site/followee";
    }

    public boolean hasFollowed(int userId) {
        if (hostHolder.getUser() == null) {
            return false;
        }
        return followService.hasFollowed(USER_ENTITY, userId, hostHolder.getUser().getId());
    }
}
