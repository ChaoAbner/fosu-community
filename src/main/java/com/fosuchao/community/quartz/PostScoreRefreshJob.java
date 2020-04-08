package com.fosuchao.community.quartz;

import com.fosuchao.community.constant.CommunityConstant;
import com.fosuchao.community.entity.DiscussPost;
import com.fosuchao.community.service.DiscussPostService;
import com.fosuchao.community.service.ElasticsearchService;
import com.fosuchao.community.service.LikeService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description: 刷新一段变动的帖子的分数，用于热门排行
 * @author: Joker Ye
 * @create: 2020/4/6 23:04
 */

public class PostScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    LikeService likeService;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    ElasticsearchService elasticsearchService;

    // 项目纪元
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-4-8 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化项目纪元失败!", e);
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        BoundSetOperations operations = discussPostService.getChangePostSet();

        if (operations == null || operations.size() == 0) {
            logger.info("[任务结束]: 没有需要刷新分数的帖子");
            return ;
        }
        logger.info("[任务开始]: 准备刷新帖子分数");
        while (operations.size() > 0) {
            int postId = (int) operations.pop();
            this.refresh(postId);
        }
        logger.info("[任务结束]: 帖子分数刷新完成");

    }

    private void refresh(int postId) {
        DiscussPost post = discussPostService.selectDiscussPostById(postId);
        if (post == null || post.getStatus() == 2) {
            logger.error("该帖子不存在: id = " + postId);
            return ;
        }

        double refreshScore = getRefreshScore(post);

        // 更新帖子
        post.setScore(refreshScore);
        discussPostService.updatePostScore(postId, refreshScore);
        // 同步搜索数据
        elasticsearchService.saveDicussPost(post);
    }

    public double getRefreshScore(DiscussPost post) {
        // 是否精华
        boolean wonderful = post.getStatus() == 1;
        // 评论数量
        int commentCount = post.getCommentCount();
        // 点赞数量
        long likeCount = likeService.getEntityLikeCount(POST_ENTITY, post.getId());

        // 计算权重
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 3;

        // 分数 = 帖子权重 + 距离天数
        return Math.log10(Math.max(w, 1))
                + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);
    }
}
