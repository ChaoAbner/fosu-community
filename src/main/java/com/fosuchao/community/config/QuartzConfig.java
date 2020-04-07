package com.fosuchao.community.config;

import com.fosuchao.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/6 23:06
 */
@Configuration
public class QuartzConfig {

    // 配置JobDetail,帖子分数刷新的任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshDetail() {
        JobDetailFactoryBean jobBean = new JobDetailFactoryBean();
        jobBean.setJobClass(PostScoreRefreshJob.class);
        // 任务名
        jobBean.setName("postScoreRefreshJob");
        // 设置组名
        jobBean.setGroup("communityJobGroup");
        // 是否可恢复
        jobBean.setRequestsRecovery(true);
        // 是否持久
        jobBean.setDurability(true);
        return jobBean;
    }

    // 配置触发器
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshDetail) {
        SimpleTriggerFactoryBean triggerBean = new SimpleTriggerFactoryBean();
        triggerBean.setJobDetail(postScoreRefreshDetail);
        triggerBean.setGroup("communityTriggerGroup");
        triggerBean.setName("postScoreRefreshTrigger");
        triggerBean.setJobDataMap(new JobDataMap());
        triggerBean.setRepeatInterval(1000 * 60 * 5);
        return triggerBean;
    }
}
