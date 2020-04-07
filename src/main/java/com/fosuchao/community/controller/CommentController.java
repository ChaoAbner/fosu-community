package com.fosuchao.community.controller;

import com.fosuchao.community.constant.CommunityConstant;
import com.fosuchao.community.entity.Comment;
import com.fosuchao.community.entity.DiscussPost;
import com.fosuchao.community.entity.Event;
import com.fosuchao.community.event.EventProducer;
import com.fosuchao.community.service.CommentService;
import com.fosuchao.community.service.DiscussPostService;
import com.fosuchao.community.service.EventService;
import com.fosuchao.community.utils.HostHolder;
import com.fosuchao.community.utils.SensitiveFilterUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/3 19:16
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    CommentService commentService;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventService eventService;

    /**
     * TODO: 权限控制
     * @Param [postId, comment]
     * @return java.lang.String
     */
    @PostMapping("/add/{postId}")
    public String addComment(@PathVariable("postId") int postId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.insertComment(comment);
        // 更新帖子评论数量
        discussPostService.updateCommentCount(
                postId, discussPostService.selectDiscussPostById(postId).getCommentCount() + 1);

        int entityUserId = 0;
        if (comment.getEntityType() == POST_ENTITY) {
            // 帖子评论
            DiscussPost post = discussPostService.selectDiscussPostById(postId);
            entityUserId = post.getUserId();
        } else if (comment.getEntityType() == REPLY_ENTITY) {
            // 回复
            Comment reply = commentService.selectById(comment.getEntityId());
            entityUserId = reply.getUserId();
        }
        // 触发评论事件
        eventService.comment(comment, postId, entityUserId);

        if (comment.getEntityType() == POST_ENTITY) {
            // 触发发帖事件
            eventService.publishPost(postId);
            // 缓存变动帖子
            discussPostService.setChangePostSet(postId);
        }

        return "redirect:/discuss/detail/" + postId;
    }
}
