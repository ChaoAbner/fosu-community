package com.fosuchao.community.service;

import com.fosuchao.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/6 21:01
 */

@Service
public class StatisticService {

    @Autowired
    RedisTemplate redisTemplate;

    private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    /**
     * 记录访问ip
     * @Param [ip]
     * @return void
     */
    public void setUV(String ip) {
        String uvKey = RedisKeyUtil.getUvkey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(uvKey, ip);
    }

    /**
     * 统计指定范围日期的UV
     * @Param [startDate, endDate]
     * @return int
     */
    public Long getUVByRange(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        // 指定范围的key的集合
        List<String> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        // 获取所有key
        while (!calendar.getTime().after(endDate)) {
            String uvKey = RedisKeyUtil.getUvkey(df.format(calendar.getTime()));
            // 天数加1
            calendar.add(Calendar.DATE, 1);
            list.add(uvKey);
        }
        // 组合所有的结果
        String unionKey = RedisKeyUtil.getUvRangekey(df.format(startDate), df.format(endDate));
        redisTemplate.opsForHyperLogLog().union(unionKey, list.toArray());

        // 返回统计结果
        return redisTemplate.opsForHyperLogLog().size(unionKey);
    }

    /**
     * 设置当天活跃用户
     * @Param [userId]
     * @return void
     */
    public void setDAU(int userId) {
        String uvKey = RedisKeyUtil.getDaukey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(uvKey, userId, true);
    }

    /**
     * 获取范围天数内的活跃用户
     * @Param [userId]
     * @return void
     */
    public long getDAUByRange(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        // 指定范围的key的集合
        List<byte[]> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        // 获取所有key
        while (!calendar.getTime().after(endDate)) {
            String dauKey = RedisKeyUtil.getDaukey(df.format(calendar.getTime()));
            list.add(dauKey.getBytes());
            // 天数加1
            calendar.add(Calendar.DATE, 1);
        }

        // 进行OR运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String dauKey = RedisKeyUtil.getDauRangekey(df.format(startDate), df.format(endDate));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        dauKey.getBytes(), list.toArray(new byte[0][0]));
                return connection.bitCount(dauKey.getBytes());
            }
        });

    }
}
