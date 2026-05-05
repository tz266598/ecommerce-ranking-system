# 电商智能排序系统

基于 XGBoost 的机器学习商品搜索排序平台。

## 项目简介

系统通过 XGBoost 模型对 Elasticsearch 的召回结果进行智能重排序，综合考虑商品价格、点击率、购买转化率、用户行为特征及文本相关性（BM25）等多种因素，为用户提供个性化的搜索排序体验。

## 技术栈

- **后端**: Java 17, Spring Boot 3.3.0, Elasticsearch 7.17.9, Redis, XGBoost4J
- **数据处理**: Python 3.8+, pandas, numpy, XGBoost, scikit-learn
- **前端**: 原生 HTML + CSS + JavaScript

## 快速开始

详见 [技术说明书](技术说明书.md)
