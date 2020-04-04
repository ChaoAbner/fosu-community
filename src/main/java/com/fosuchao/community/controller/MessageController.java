package com.fosuchao.community.controller;

import com.fosuchao.community.annotation.LoginRequired;
import com.fosuchao.community.entity.Message;
import com.fosuchao.community.entity.Page;
import com.fosuchao.community.entity.User;
import com.fosuchao.community.service.MessageService;
import com.fosuchao.community.service.UserService;
import com.fosuchao.community.utils.HostHolder;
import com.fosuchao.community.utils.JsonResponseUtil;
import com.fosuchao.community.utils.SensitiveFilterUtil;
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
 */

@Controller
public class MessageController {

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
        // 查询未读消息总数
        int allUnreadCount = messageService.selectConversationUnReadCount(null, user.getId());
        model.addAttribute("letterUnreadCount", allUnreadCount);

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

}
