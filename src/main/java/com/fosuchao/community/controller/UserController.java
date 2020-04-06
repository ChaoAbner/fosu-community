package com.fosuchao.community.controller;

import com.fosuchao.community.annotation.LoginRequired;
import com.fosuchao.community.constant.CommunityConstant;
import com.fosuchao.community.entity.DiscussPost;
import com.fosuchao.community.entity.Page;
import com.fosuchao.community.entity.User;
import com.fosuchao.community.service.DiscussPostService;
import com.fosuchao.community.service.FollowService;
import com.fosuchao.community.service.LikeService;
import com.fosuchao.community.service.UserService;
import com.fosuchao.community.utils.CommunityUtil;
import com.fosuchao.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 18:59
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    // 个人设置
    @LoginRequired
    @GetMapping(path = "/setting")
    public String getSettingPage() {
        return "/site/setting";
    }

    // 上传头像接口
    @LoginRequired
    @PostMapping(path = "/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "请选择一张图片");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        // 后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确！");
            return "/site/setting";
        }
        // 生成随机文件名
        fileName = CommunityUtil.uuid() + suffix;

        // 图片存放位置
        File file = new File(uploadPath + "/" + fileName);
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            headerImage.transferTo(file);
        } catch (IOException e) {
            logger.error("头像存储失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }
        // 更新用户头像
        User user = hostHolder.getUser();
        String newHeaderUrl = domain + "/user/header/" + fileName;
        userService.updateHeaderUrl(user.getId(), newHeaderUrl);

        return "redirect:/index";
    }

    // 获取头像
    @GetMapping(path = "/header/{fileName}")
    public void updateHeaderUrl(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    // 个人主页
    @GetMapping("/profile/{userId}")
    public String profile(@PathVariable("userId") int userId, Model model) {
        User user = userService.selectById(userId);

        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 用户
        model.addAttribute("user", user);
        // 点赞数量
        int likeCount = likeService.getUserlikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        // 关注数量
        Long followeeCount = followService.getFolloweeCount(USER_ENTITY, userId);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        Long followerCount = followService.getFollowerCount(USER_ENTITY, userId);
        model.addAttribute("followerCount", followerCount);

        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(USER_ENTITY, userId, hostHolder.getUser().getId());
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }

    /**
     *
     * @Param [userId, model, page]
     * @return java.lang.String
     */
    @GetMapping("/posts/{userId}")
    public String getUserPosts(@PathVariable("userId") int userId, Model model, Page page) {
        page.setLimit(5);
        page.setPath("/user/posts/" + userId);
        page.setRows(discussPostService.selectDiscussPostsRows(userId));

        List<DiscussPost> posts = discussPostService.selectDiscussPosts(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();

        if (posts != null) {
            for (DiscussPost post : posts) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.selectById(post.getUserId());
                map.put("user", user);

                long likeCount = likeService.getEntityLikeCount(POST_ENTITY, post.getId());
                map.put("likeCount", likeCount);

                discussPosts.add(map);
            }
            model.addAttribute("discussPosts", discussPosts);
        }

        return "/site/my-post";
    }
}
