package com.fosuchao.community.controller;

import com.fosuchao.community.constant.CommunityConstant;
import com.fosuchao.community.entity.*;
import com.fosuchao.community.event.EventProducer;
import com.fosuchao.community.service.*;
import com.fosuchao.community.utils.HostHolder;
import com.fosuchao.community.utils.JsonResponseUtil;
import com.fosuchao.community.utils.SensitiveFilterUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/3 15:57
 */

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventService eventService;

    /**
     * 添加文章
     * @return java.lang.String
     * @Param [title, content]
     */
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        if (StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            return JsonResponseUtil.getJsonResponse(400, "标题或内容不能为空");
        }
        // 检查登录态
        User user = hostHolder.getUser();
        if (user == null) {
            return JsonResponseUtil.getJsonResponse(403, "您还没有登录哦");
        }
        // 添加文章
        DiscussPost post = new DiscussPost();
        post.setCreateTime(new Date());
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setScore(0.0);
        // 插入文章
        discussPostService.insertDiscussPost(post);

        // 触发发帖事件
        eventService.publishPost(post);

        return JsonResponseUtil.getJsonResponse(0, "发布成功!");
    }

    /**
     * 帖子详情
     * @Param [id, model, page]
     * @return java.lang.String
     */
    @GetMapping("/detail/{id}")
    public String selectDiscussDetail(@PathVariable("id") int id, Model model, Page page) {
        DiscussPost post = discussPostService.selectDiscussPostById(id);
        if (post == null) {
            return JsonResponseUtil.getJsonResponse(400, "该帖子不存在");
        }
        model.addAttribute("post", post);
        // 查找用户
        User user = userService.selectById(post.getUserId());
        model.addAttribute("user", user);

        // 帖子点赞数
        long entityLikeCount = likeService.getEntityLikeCount(POST_ENTITY, post.getId());
        model.addAttribute("likeCount", entityLikeCount);

        // 点赞状态, 没有登录的人是无法查看状态的！
        int entityLikeStatus = hostHolder.getUser() == null ? 0 : likeService.getEntityLikeStatus(
                POST_ENTITY, post.getId(), hostHolder.getUser().getId());
        model.addAttribute("likeStatus", entityLikeStatus);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + id);
        page.setRows(post.getCommentCount());

        // 查找评论，帖子评论，回复
        List<Comment> comments = commentService.selectCommentsByEntity(
                POST_ENTITY, post.getId(), page.getOffset(), page.getLimit());
        // 评论VO列表
        ArrayList<Map<String, Object>> commentVoList = new ArrayList<>();
        if (comments != null) {
            User curUser = hostHolder.getUser();
            for (Comment comment : comments) {
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                // 查找评论中的用户
                commentVo.put("user", userService.selectById(comment.getUserId()));
                // 评论点赞数
                long commentLikeCount = likeService.getEntityLikeCount(REPLY_ENTITY, comment.getId());
                commentVo.put("likeCount", commentLikeCount);
                // 点赞状态, 没有登录的人是无法查看状态的！
                int commentLikeStatus = curUser == null ? 0 : likeService.getEntityLikeStatus(
                        REPLY_ENTITY, comment.getId(), hostHolder.getUser().getId());
                commentVo.put("likeStatus", commentLikeStatus);
                // 回复列表
                List<Comment> replyList = commentService.selectCommentsByEntity(
                        REPLY_ENTITY, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 作者
                        replyVo.put("user", userService.selectById(reply.getUserId()));
                        // 回复点赞数
                        long replyLikeCount = likeService.getEntityLikeCount(REPLY_ENTITY, reply.getId());
                        replyVo.put("likeCount", replyLikeCount);
                        // 点赞状态
                        int replyLikeStatus = curUser == null ? 0 : likeService.getEntityLikeStatus(
                                REPLY_ENTITY, reply.getId(), hostHolder.getUser().getId());
                        replyVo.put("likeStatus", replyLikeStatus);
                        // 回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.selectById(reply.getTargetId());
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);
                // 回复数量
                int replyCount = commentService.selectCountByEntity(comment.getId(), REPLY_ENTITY);
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }

    /**
     * 置顶，type = 1, 取消置顶
     * @Param [id]
     * @return java.lang.String
     */
    @PostMapping("/top")
    @ResponseBody
    public String setTop(int id, int type) {
        discussPostService.updatePostType(id, type);

        // 触发发帖事件
        return JsonResponseUtil.getJsonResponse(0);
    }

    /**
     * 加精，status = 1， 取消加精
     * @Param [id]
     * @return java.lang.String
     */
    @PostMapping("/wonderful")
    @ResponseBody
    public String setWonderful(int id, int status) {
        discussPostService.updatePostStatus(id, status);

        // 触发发帖事件
        eventService.publishPost(discussPostService.selectDiscussPostById(id));
        return JsonResponseUtil.getJsonResponse(0);
    }

    /**
     * 删除/拉黑，status = 2
     * @Param [id]
     * @return java.lang.String
     */
    @PostMapping("/delete")
    @ResponseBody
    public String setDelete(int id) {
        discussPostService.updatePostStatus(id, 2);
        // 删帖事件
        eventService.deletePost(discussPostService.selectDiscussPostById(id));
        return JsonResponseUtil.getJsonResponse(0);
    }
}
