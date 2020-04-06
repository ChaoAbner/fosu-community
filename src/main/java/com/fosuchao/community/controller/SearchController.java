package com.fosuchao.community.controller;

import com.fosuchao.community.constant.CommunityConstant;
import com.fosuchao.community.entity.DiscussPost;
import com.fosuchao.community.entity.Page;
import com.fosuchao.community.service.ElasticsearchService;
import com.fosuchao.community.service.LikeService;
import com.fosuchao.community.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @create: 2020/4/6 11:11
 */

@Controller
public class SearchController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    ElasticsearchService esService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @GetMapping("/search")
    public String search(String keyword, Page page, Model model) {
        logger.info(String.format("关键字[%s]被搜索了", keyword));
        // 所有帖子
        org.springframework.data.domain.Page<DiscussPost> posts =
                esService.searchDiscussPosts(keyword, page.getCurrent() - 1, page.getLimit());

        // 聚合数据
        List<Map<String, Object>> discussVo = new ArrayList<>();
        if (posts != null) {
            for (DiscussPost post : posts) {
                Map<String, Object> map = new HashMap<>();
                // 帖子
                map.put("post", post);
                // 作者
                map.put("user", userService.selectById(post.getUserId()));
                // 点赞数
                map.put("likeCount", likeService.getEntityLikeCount(POST_ENTITY, post.getId()));
                discussVo.add(map);
            }
        }
        model.addAttribute("discussPosts", discussVo);
        model.addAttribute("keyword", keyword);

        // 分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(posts == null ? 0 : (int) posts.getTotalElements());

        return "/site/search";
    }
}
