package com.example.ranking.service;

import com.example.ranking.model.RankedProduct;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchFeatureService {

    private static final Logger log = LoggerFactory.getLogger(SearchFeatureService.class);

    @Autowired
    private RestHighLevelClient esClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private XGBoostRankingService rankingService;

    private static final ObjectMapper mapper = new ObjectMapper();

    public List<RankedProduct> searchAndRank(String query, String userId) {
        try {
            SearchRequest request = new SearchRequest("products");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchQuery("title", query));
            sourceBuilder.size(50);
            request.source(sourceBuilder);

            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();

            String userJson = redisTemplate.opsForValue().get("user:" + userId);
            Map<String, Object> userFeature = userJson != null ?
                    mapper.readValue(userJson, Map.class) : Collections.singletonMap("user_click_7d", 10.0);

            List<RankedProduct> resultProducts = new ArrayList<>();

            for (SearchHit hit : hits) {
                String productId = hit.getId();
                float bm25Score = hit.getScore();

                String prodJson = redisTemplate.opsForValue().get("product:" + productId);
                Map<String, Object> prodFeature = prodJson != null ?
                        mapper.readValue(prodJson, Map.class) : new HashMap<>();

                Map<String, Object> modelInput = new HashMap<>();
                modelInput.put("price", prodFeature.getOrDefault("price", 0.0));
                modelInput.put("ctr", prodFeature.getOrDefault("ctr", 0.0));
                modelInput.put("purchase_rate", prodFeature.getOrDefault("purchase_rate", 0.0));
                modelInput.put("user_click_7d", userFeature.getOrDefault("user_click_7d", 0.0));
                modelInput.put("bm25_score", (double) bm25Score);

                double score = rankingService.predictScore(modelInput);

                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                RankedProduct product = new RankedProduct();
                product.setProduct_id(productId);
                product.setTitle((String) sourceAsMap.get("title"));
                product.setCategory((String) sourceAsMap.get("category"));
                product.setPrice((Double) sourceAsMap.get("price"));
                product.setRanking_score(score);
                resultProducts.add(product);
            }

            resultProducts.sort((a, b) -> Double.compare(b.getRanking_score(), a.getRanking_score()));
            return resultProducts;

        } catch (Exception e) {
            log.error("搜索服务异常: {}", e.getMessage(), e);
            throw new RuntimeException("搜索服务异常", e);
        }
    }
}
