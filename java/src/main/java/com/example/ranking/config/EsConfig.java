package com.example.ranking.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfig {

    @Value("${es.host}")
    private String host;
    @Value("${es.port}")
    private int port;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(
                org.elasticsearch.client.RestClient.builder(
                        new HttpHost(host, port, "http")
                )
        );
    }
}
