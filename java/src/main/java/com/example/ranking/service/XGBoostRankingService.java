package com.example.ranking.service;

import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class XGBoostRankingService {

    private static final Logger log = LoggerFactory.getLogger(XGBoostRankingService.class);
    private Booster booster;

    private static final List<String> FEATURE_COLUMNS = Arrays.asList(
        "price", "ctr", "purchase_rate", "user_click_7d", "bm25_score"
    );

    public XGBoostRankingService() {
        log.info("========== XGBoostRankingService 构造函数被调用 ==========");
    }

    @PostConstruct
    public void init() {
        log.info("========== @PostConstruct init() 方法被调用 ==========");
        log.info("开始加载 XGBoost 原生模型...");

        try {
            InputStream is = new ClassPathResource("xgboost_model.json").getInputStream();
            log.info("XGBoost 模型文件读取成功");

            this.booster = XGBoost.loadModel(is);
            log.info("✅ XGBoost 模型加载成功！");
            log.info("   Booster 类型: {}", booster.getClass().getName());

        } catch (Exception e) {
            log.error("❌ XGBoost 模型加载失败", e);
            throw new RuntimeException("XGBoost 模型加载失败: " + e.getMessage(), e);
        }
    }

    public double predictScore(Map<String, Object> features) {
        try {
            float[] featureArray = new float[FEATURE_COLUMNS.size()];
            for (int i = 0; i < FEATURE_COLUMNS.size(); i++) {
                String featureName = FEATURE_COLUMNS.get(i);
                Object value = features.get(featureName);
                if (value == null) {
                    featureArray[i] = 0.0f;
                } else {
                    featureArray[i] = ((Number) value).floatValue();
                }
            }

            DMatrix dmatrix = new DMatrix(featureArray, 1, featureArray.length);

            float[][] predicts = booster.predict(dmatrix);

            double score;
            if (predicts[0].length == 1) {
                score = predicts[0][0];
                log.debug("XGBoost回归模式 - 预测分数: {}", score);
            } else {
                score = predicts[0][1];
                log.debug("XGBoost分类模式 - 预测概率: {}", score);
            }

            return score;

        } catch (Exception e) {
            log.error("❌ XGBoost 预测失败 - 异常类型: {}, 消息: {}",
                     e.getClass().getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("XGBoost 预测失败", e);
        }
    }
}
