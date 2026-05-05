package com.example.ranking.controller;

import com.example.ranking.model.RankedProduct;
import com.example.ranking.service.SearchFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RankingController {

    @Autowired
    private SearchFeatureService searchFeatureService;

    @GetMapping("/api/search")
    public List<RankedProduct> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "user_1") String userId) {
        return searchFeatureService.searchAndRank(query, userId);
    }
}
