package com.fosuchao.community.controller;

import com.fosuchao.community.constant.CommunityConstant;
import com.fosuchao.community.entity.Event;
import com.fosuchao.community.entity.User;
import com.fosuchao.community.event.EventProducer;
import com.fosuchao.community.service.EventService;
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
public class LikeController implements CommunityConstant {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventService eventService;

    /**
     * 点赞接口，TODO: 权限控制
     * @Param [entityType, entityId, entityUserId, postId]
     * @return java.lang.String
     */
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
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

        // 判断点赞状态来触发点赞事件
        if (likeStatus == 1) {
            eventService.like(entityId, entityType, entityUserId, postId);
        }

        return JsonResponseUtil.getJsonResponse(0, null, map);
    }
}
