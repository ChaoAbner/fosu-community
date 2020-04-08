package com.fosuchao.community.controller;

import com.fosuchao.community.constant.CommunityConstant;
import com.fosuchao.community.entity.Comment;
import com.fosuchao.community.entity.DiscussPost;
import com.fosuchao.community.entity.Page;
import com.fosuchao.community.entity.User;
import com.fosuchao.community.service.*;
import com.fosuchao.community.utils.CommunityUtil;
import com.fosuchao.community.utils.HostHolder;
import com.fosuchao.community.utils.JsonResponseUtil;
import com.fosuchao.community.utils.RedisKeyUtil;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private EventService eventService;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private HostHolder hostHolder;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header}")
    private String headerBucket;

    @Value("${qiniu.bucket.url}")
    private String headerUrl;

    // 个人设置
    @GetMapping(path = "/setting")
    public String getSettingPage(Model model) {
        // 生成文件名
        String fileName = CommunityUtil.uuid();
        // 设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody", JsonResponseUtil.getJsonResponse(0));
        // 为客户端生成上传凭证
        Auth auth = Auth.create(accessKey, secretKey);
        String token = auth.uploadToken(headerBucket, fileName, 3600, policy);

        model.addAttribute("uploadToken", token);
        model.addAttribute("fileName", fileName);

        return "/site/setting";
    }

    @PostMapping("/header/url")
    @ResponseBody
    public String updateHeaderUrl(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return JsonResponseUtil.getJsonResponse(400, "文件名不能为空");
        }

        String url = headerUrl + "/" + fileName;
        userService.updateHeaderUrl(hostHolder.getUser().getId(), url);

        // 清除用户缓存
        userService.clearUserCache(hostHolder.getUser().getId());

        return JsonResponseUtil.getJsonResponse(0);
    }

    @PostMapping("/password")
    @ResponseBody
    public String updatePassword(String sourcePass, String newPass) {
        if (StringUtils.isBlank(sourcePass) || StringUtils.isBlank(newPass)) {
            return JsonResponseUtil.getJsonResponse(400, "密码不能为空");
        }

        User user = hostHolder.getUser();
        if (user == null) {
            return JsonResponseUtil.getJsonResponse(400, "没有权限访问！");
        }
        String salt = user.getSalt();
        if (!user.getPassword().equals(CommunityUtil.md5(sourcePass + salt))) {
            return JsonResponseUtil.getJsonResponse(400, "原密码不正确");
        }
        userService.updatePassword(user.getId(), CommunityUtil.md5(newPass + salt));
        // 清除用户缓存
        userService.clearUserCache(hostHolder.getUser().getId());
        return JsonResponseUtil.getJsonResponse(0);
    }


    /**
     * 本地服务器上传头像接口
     * === 废弃 ===
     * @Param [headerImage, model]
     * @return java.lang.String
     */
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

        File fileDir = new File(uploadPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        // 图片存放位置
        File file = new File(uploadPath + "/" + fileName);

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

    /**
     * 获取本地服务器的用户头像
     *  === 废弃 ===
     * @Param [fileName, response]
     * @return void
     */
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
     * 发布的帖子
     * @Param [userId, model, page]
     * @return java.lang.String
     */
    @GetMapping("/posts/{userId}")
    public String getUserPosts(@PathVariable("userId") int userId, Model model, Page page) {
        page.setLimit(5);
        page.setPath("/user/posts/" + userId);
        int rows = discussPostService.selectDiscussPostsRows(userId);
        page.setRows(rows);
        model.addAttribute("postsCount", rows);

        List<DiscussPost> posts = discussPostService.selectDiscussPosts(userId, page.getOffset(), page.getLimit(), 0);
        List<Map<String, Object>> discussPosts = new ArrayList<>();

        // 用户
        model.addAttribute("user", userService.selectById(userId));

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

    /**
     * 获取用户的回复
     * @Param [userId, model, page]
     * @return java.lang.String
     */
    @GetMapping("/reply/{userId}")
    public String getUserReplys(@PathVariable("userId") int userId, Model model, Page page) {
        // 包括帖子回复和评论回复  TODO
        page.setLimit(5);
        page.setPath("/user/reply/" + userId);
        int rows = commentService.selectCountByUserId(userId);
        page.setRows(rows);
        // 回复总数
        model.addAttribute("replyCount", rows);

        // 用户
        model.addAttribute("user", userService.selectById(userId));

        List<Comment> comments = commentService.selectCommentsByUserId(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentsVO = new ArrayList<>();
        if (comments != null) {
            for (Comment comment : comments) {
                Map<String, Object> map = new HashMap<>();
                map.put("comment", comment);
                // 查询具体的帖子，（如果是回复，找到回复所在的帖子）
                if (comment.getEntityType() == POST_ENTITY) {
                    // 直接获取帖子
                    map.put("post", discussPostService.selectDiscussPostById(comment.getEntityId()));
                } else if (comment.getEntityType() == REPLY_ENTITY){
                    // 获取父评论
                    Comment superComment = commentService.selectById(comment.getEntityId());
                    // 获取帖子
                    map.put("post", discussPostService.selectDiscussPostById(superComment.getEntityId()));
                }
                commentsVO.add(map);
            }
        }
        model.addAttribute("comments", commentsVO);

        return "/site/my-reply";
    }

    @GetMapping("/forget")
    public String getForgetPage() {
        return "/site/forget";
    }

    /**
     * 忘记密码, 验证
     * @Param [email, code, newPassword]
     * @return java.lang.String
     */
    @PostMapping("/forget/check")
    @ResponseBody
    public String forgetPassword(String email, String code, String newPassword) {
        if (StringUtils.isBlank(email) || StringUtils.isBlank(code) || StringUtils.isBlank(newPassword)) {
            return JsonResponseUtil.getJsonResponse(400, "参数不能为空！");
        }
        email = email.toLowerCase();
        // 获取验证码
        String forgetCodeKey = RedisKeyUtil.getForgetCode(email);
        String sourceCode = (String) redisTemplate.opsForValue().get(forgetCodeKey);
        if (StringUtils.isBlank(sourceCode)) {
            return JsonResponseUtil.getJsonResponse(400, "验证码已过期，请重新获取");
        }
        // 验证
        if (!sourceCode.equals(code)) {
            return JsonResponseUtil.getJsonResponse(400, "验证码不正确！");
        }
        User user = userService.selectByEmail(email);
        if (user == null) {
            return JsonResponseUtil.getJsonResponse(400, "用户不存在，检查邮箱是否错误！");
        }
        // 设置新密码
        String pass = CommunityUtil.md5(newPassword + user.getSalt());
        userService.updatePassword(user.getId(), pass);

        return JsonResponseUtil.getJsonResponse(0, "重置密码成功, 快去登录吧！");
    }

    /**
     * 生成验证码
     * @Param [email]
     * @return java.lang.String
     */
    @GetMapping("/forget/code")
    @ResponseBody
    public String getForgetCode(String email) {
        // 生成验证码
        String code = CommunityUtil.uuid().substring(0, 5);

        String forgetCodeKey = RedisKeyUtil.getForgetCode(email.toLowerCase());
        // 保存五分钟
        redisTemplate.opsForValue().set(forgetCodeKey, code, 60 * 5, TimeUnit.SECONDS);

        Context context = new Context();

        context.setVariable("email", email);
        context.setVariable("code", code);

        String content = templateEngine.process("mail/forget", context);
        // 发送验证码
        eventService.email(email, "验证码", content);

        return JsonResponseUtil.getJsonResponse(0, "验证码已发送，若未收到，请重发");
    }
}
