package com.fosuchao.community.controller;

import com.fosuchao.community.entity.Comment;
import com.fosuchao.community.service.CommentService;
import com.fosuchao.community.service.DiscussPostService;
import com.fosuchao.community.utils.HostHolder;
import com.fosuchao.community.utils.SensitiveFilterUtil;
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
public class CommentController {

    @Autowired
    CommentService commentService;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    SensitiveFilterUtil sensitiveFilterUtil;

    @PostMapping("/add/{postId}")
    public String addComment(@PathVariable("postId") int postId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilterUtil.filter(comment.getContent()));
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.insertComment(comment);
        // 更新帖子评论数量
        discussPostService.updateCommentCount(
                postId, discussPostService.selectDiscussPostById(postId).getCommentCount() + 1);

        return "redirect:/discuss/detail/" + postId;
    }
}
