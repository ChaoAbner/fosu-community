package com.fosuchao.community.controller;

import com.fosuchao.community.entity.User;
import com.fosuchao.community.service.LikeService;
import com.fosuchao.community.utils.HostHolder;
import com.fosuchao.community.utils.JsonResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/4 19:02
 */

@Controller
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId) {
        User user = hostHolder.getUser();

        // 点赞
        likeService.like(entityType, entityId, user.getId(), entityUserId);

        // 数量
        long likeCount = likeService.getEntityLikeCount(entityType, entityId);
        // 状态
        int likeStatus = likeService.getEntityLikeStatus(entityType, entityId, user.getId());

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return JsonResponseUtil.getJsonResponse(0, null, map);
    }
}
