package com.fosuchao.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.fosuchao.community.annotation.LoginRequired;
import com.fosuchao.community.constant.CommunityConstant;
import com.fosuchao.community.entity.Message;
import com.fosuchao.community.entity.Page;
import com.fosuchao.community.entity.User;
import com.fosuchao.community.service.MessageService;
import com.fosuchao.community.service.UserService;
import com.fosuchao.community.utils.CookieUtil;
import com.fosuchao.community.utils.HostHolder;
import com.fosuchao.community.utils.JsonResponseUtil;
import com.fosuchao.community.utils.SensitiveFilterUtil;
import org.apache.kafka.common.internals.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/3 22:01
 * @noinspection ALL
 */

@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    SensitiveFilterUtil filterUtil;

    @Autowired
    UserService userService;


    // 私信列表
    @GetMapping("/letter/list")
    @LoginRequired
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.selectConversationsCount(user.getId()));

        // 会话列表
        List<Message> messages = messageService.selectConversations(
                user.getId(), page.getOffset(), page.getLimit());

        List<Map<String, Object>> msgList = new ArrayList<>();
        if (messages != null) {
            for (Message message : messages) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.selectLettersCount(message.getConversationId()));
                map.put("unreadCount", messageService.selectConversationUnReadCount(
                        message.getConversationId(), user.getId()));
                int targetId = message.getToId() == user.getId() ? message.getFromId() : message.getToId();
                map.put("target", userService.selectById(targetId));

                msgList.add(map);
            }
        }

        model.addAttribute("conversations", msgList);
        // 未读私信总数
        int letterUnreadCount = messageService.selectConversationUnReadCount(null, user.getId());
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        // 未读通知总数
        int noticeUnreadCount = messageService.selectNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/letter";
    }


    @GetMapping("/letter/detail/{conversationId}")
    @LoginRequired
    public String getConversationDetail(@PathVariable("conversationId") String cid,  Model model, Page page) {
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + cid);
        page.setRows(messageService.selectLettersCount(cid));

        List<Message> messages = messageService.selectLetters(cid, page.getOffset(), page.getLimit());

        List<Map<String, Object>> msgList = new ArrayList<>();

        if (messages != null) {
            for (Message message : messages) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.selectById(message.getFromId()));
                msgList.add(map);
            }
        }

        model.addAttribute("letters", msgList);
        // 私信目标
        model.addAttribute("target", getLetterTarget(cid));

        // 设置为已读
        messageService.updateConversationStatus(cid, 1, hostHolder.getUser().getId());
        return "/site/letter-detail";
    }

    // 获取消息发送者
    private Object getLetterTarget(String cid) {
        String[] ids = cid.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0) {
            return userService.selectById(id1);
        } else {
            return userService.selectById(id0);
        }
    }

    @PostMapping("/letter/send")
    @LoginRequired
    public String sendMessage(String toName, String content) {
        User toUser = userService.selectByName(toName);
        if (toUser == null) {
            return JsonResponseUtil.getJsonResponse(400, "目标用户不存在");
        }
        User holder = hostHolder.getUser();
        Message message = new Message();
        message.setCreateTime(new Date());
        message.setContent(HtmlUtils.htmlEscape(content));
        message.setContent(filterUtil.filter(message.getContent()));
        message.setFromId(holder.getId());
        message.setToId(toUser.getId());

        if (toUser.getId() > holder.getId()) {
            message.setConversationId(holder.getId() + "_" + toUser.getId());
        } else {
            message.setConversationId(toUser.getId() + "_" + holder.getId());
        }
        messageService.insertLetter(message);

        return JsonResponseUtil.getJsonResponse(0, "发送成功！");
    }

    @GetMapping("/notice/list")
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();

        // 获取评论通知
        Message message = messageService.selectLastestNotice(user.getId(), COMMENT_TOPIC);
        Map<String, Object> messageVO = new HashMap<>();
        if (message != null) {
            messageVO.put("message", message);
            // 解析通知内容
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.selectById((int) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.selectNoticeCount(user.getId(), COMMENT_TOPIC);
            messageVO.put("count", count);
            int unreadCount = messageService.selectNoticeUnreadCount(user.getId(), COMMENT_TOPIC);
            messageVO.put("unread", unreadCount);
        } else {
            messageVO.put("message", null);
        }
        model.addAttribute("commentNotice", messageVO);

        // 获取点赞通知
        message = messageService.selectLastestNotice(user.getId(), LIKE_TOPIC);
        messageVO = new HashMap<String, Object>();
        if (message != null) {
            messageVO.put("message", message);
            // 解析通知内容
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.selectById((int) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.selectNoticeCount(user.getId(), LIKE_TOPIC);
            messageVO.put("count", count);
            int unreadCount = messageService.selectNoticeUnreadCount(user.getId(), LIKE_TOPIC);
            messageVO.put("unread", unreadCount);
        } else {
            messageVO.put("message", null);
        }
        model.addAttribute("likeNotice", messageVO);

        // 获取关注通知
        message = messageService.selectLastestNotice(user.getId(), FOLLOW_TOPIC);
        messageVO = new HashMap<String, Object>();
        if (message != null) {
            messageVO.put("message", message);
            // 解析通知内容
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.selectById((int) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));

            int count = messageService.selectNoticeCount(user.getId(), FOLLOW_TOPIC);
            messageVO.put("count", count);
            int unreadCount = messageService.selectNoticeUnreadCount(user.getId(), FOLLOW_TOPIC);
            messageVO.put("unread", unreadCount);
        } else {
            messageVO.put("message", null);
        }
        model.addAttribute("followNotice", messageVO);

        // 查询未读消息数量
        int noticeUnreadCount = messageService.selectNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        int letterUnreadCount = messageService.selectConversationUnReadCount(null, user.getId());
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        return "/site/notice";
    }

    @GetMapping("/notice/detail/{topic}")
    public String noticeDetail(@PathVariable("topic") String topic, Model model, Page page) {
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.selectNoticeCount(user.getId(), topic));

        List<Message> messages = messageService.selectNotices(
                user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVolist = new ArrayList<>();
        if (messages != null) {
            for (Message message : messages) {
                Map<String, Object> map = new HashMap<>();
                // 通知
                map.put("notice", message);
                // 内容
                String content = HtmlUtils.htmlUnescape(message.getContent());
                Map data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.selectById( (int) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                if (data.containsKey("postId"))
                    map.put("postId", data.get("postId"));
                // 通知的人
                map.put("fromUser", userService.selectById(message.getFromId()));

                noticeVolist.add(map);
            }
        }
        model.addAttribute("notices", noticeVolist);
        // 设置已读
        messageService.updateConversationStatus(topic, 1, user.getId());

        return "/site/notice-detail";
    }
}
