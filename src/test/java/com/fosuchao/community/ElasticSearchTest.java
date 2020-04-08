package com.fosuchao.community;

import com.fosuchao.community.dao.DiscussPostMapper;
import com.fosuchao.community.dao.es.DiscussPostRepository;
import com.fosuchao.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/6 10:25
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticSearchTest {

    @Autowired
    private DiscussPostMapper discussMapper;

    @Autowired
    DiscussPostRepository discussPostRepository;

    @Autowired
    ElasticsearchTemplate template;

    @Test
    public void esTestInsert() {
        discussPostRepository.save(discussMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussMapper.selectDiscussPostById(243));
        discussPostRepository.save(discussMapper.selectDiscussPostById(242));
    }


    @Test
    public void esTestInsertList() {
        discussPostRepository.saveAll(discussMapper.selectDiscussPosts(0, 0, 500, 0));
//        discussPostRepository.saveAll(discussMapper.selectDiscussPosts(102, 0, 100));
//        discussPostRepository.saveAll(discussMapper.selectDiscussPosts(103, 0, 100));
//        discussPostRepository.saveAll(discussMapper.selectDiscussPosts(111, 0, 100));
//        discussPostRepository.saveAll(discussMapper.selectDiscussPosts(112, 0, 100));
//        discussPostRepository.saveAll(discussMapper.selectDiscussPosts(131, 0, 100));
//        discussPostRepository.saveAll(discussMapper.selectDiscussPosts(132, 0, 100));
//        discussPostRepository.saveAll(discussMapper.selectDiscussPosts(133, 0, 100));
//        discussPostRepository.saveAll(discussMapper.selectDiscussPosts(134, 0, 100));
    }

    @Test
    public void esTestUpdate() {
        DiscussPost post = discussMapper.selectDiscussPostById(231);
        post.setContent("我的新人，欢迎怼我");
        discussPostRepository.save(post);
    }

    @Test
    public void esTestDelete() {
        discussPostRepository.delete(discussMapper.selectDiscussPostById(231));
//        discussPostRepository.deleteAll();
    }

    @Test
    public void esTestSearchByRepository() {
        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        Page<DiscussPost> search = discussPostRepository.search(query);

        for (DiscussPost post : search) {
            System.out.println(post);
        }
    }

    @Test
    public void esTestSearchByTemplate() {
        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        Page<DiscussPost> pages = template.queryForPage(query, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                SearchHits hits = response.getHits();
                if (hits.getTotalHits() <= 0) {
                    return null;
                }
                List<DiscussPost> list = new ArrayList<>();

                for (SearchHit hit : hits) {
                    DiscussPost post = new DiscussPost();

                    String id = hit.getSourceAsMap().get("id").toString();
                    post.setId(Integer.valueOf(id));

                    String userId = hit.getSourceAsMap().get("userId").toString();
                    post.setId(Integer.valueOf(userId));

                    String status = hit.getSourceAsMap().get("status").toString();
                    post.setStatus(Integer.valueOf(status));

                    String title = hit.getSourceAsMap().get("title").toString();
                    post.setTitle(title);

                    String content = hit.getSourceAsMap().get("content").toString();
                    post.setContent(content);

                    String type = hit.getSourceAsMap().get("type").toString();
                    post.setType(Integer.valueOf(type));

                    String score = hit.getSourceAsMap().get("score").toString();
                    post.setScore(Double.valueOf(score));

                    String createTime = hit.getSourceAsMap().get("createTime").toString();
                    post.setCreateTime(new Date(Long.valueOf(createTime)));

                    String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                    post.setCommentCount(Integer.valueOf(commentCount));

                    // 处理需要高亮的部分
                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if (titleField != null) {
                        post.setTitle(titleField.getFragments()[0].toString());
                    }

                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if (contentField != null) {
                        post.setContent(contentField.getFragments()[0].toString());
                    }

                    list.add(post);
                }
                return new AggregatedPageImpl(list, pageable, hits.getTotalHits(),
                        response.getAggregations(), hits.getMaxScore());
            }
        });

        System.out.println(pages.getTotalElements());
        System.out.println(pages.getTotalPages());
        System.out.println(pages.getNumber());
        System.out.println(pages.getSize());
        for (DiscussPost post : pages) {
            System.out.println(post);
        }
    }
}
