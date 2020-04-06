package com.fosuchao.community.controller;

import com.fosuchao.community.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/6 20:57
 */

@Controller
@RequestMapping("/statistic")
public class StatisticController {

    @Autowired
    StatisticService statisticService;

    @RequestMapping(path = "", method = {RequestMethod.GET, RequestMethod.POST})
    public String getStatisticPage() {
        return "/site/admin/data";
    }

    /**
     * 统计网站UV
     * @Param [start, end, model]
     * @return java.lang.String
     */
    @PostMapping("/uv")
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        Long uv = statisticService.getUVByRange(start, end);
        model.addAttribute("uvResult", uv);
        model.addAttribute("uvStartDate", start);
        model.addAttribute("uvEndDate", end);

        return "forward:/statistic";
    }

    /**
     * 统计活跃用户
     * @Param [start, end, model]
     * @return java.lang.String
     */
    @PostMapping("/dau")
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        Long dau = statisticService.getDAUByRange(start, end);
        model.addAttribute("dauResult", dau);
        model.addAttribute("dauStartDate", start);
        model.addAttribute("dauEndDate", end);

        return "forward:/statistic";
    }
}
