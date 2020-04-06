package com.fosuchao.community.dao.es;

import com.fosuchao.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
