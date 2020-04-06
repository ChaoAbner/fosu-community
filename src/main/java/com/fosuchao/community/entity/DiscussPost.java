package com.fosuchao.community.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 14:39
 */
@Data
@Document(indexName = "discusspost", type = "_doc", replicas = 3)
public class DiscussPost {

    @Id
    private int id;

    @Field(type = FieldType.Integer)
    private int userId;

    // analyzer 表示解析建立索引的时候，将句子拆到最细； searchAnalyzer则是搜索的时候，通过智能分词处理，不会太细，也不会太粗
    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String content;

    @Field(type = FieldType.Integer)
    private int type;

    @Field(type = FieldType.Integer)
    private int status;

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Double)
    private Double score;

    @Field(type = FieldType.Integer)
    private int commentCount;
}
