package com.yy.elasticsearch.controller;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.yy.elasticsearch.config.ElasticsearchConfiguration;

import java.util.Collections;

/**
 * @description: 测试一下rhl的方法
 * @author: Guimu
 * @create: 2018/08/06 14:29:07
 **/
@RestController
public class EsHiClientTest {
    @Autowired
    private RestHighLevelClient client;
    private RestClient restClient = ElasticsearchConfiguration.getRestClient();
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "deletein")
    public void te() {
        try {
            String method = "POST";
            String endpoint = "/elastic/Company/_search";
            HttpEntity entity = new NStringEntity("{\n" +
                    "  \"query\": {\n" +
                    "    \"match_all\": {}\n" +
                    "  }\n" +
                    "}", ContentType.APPLICATION_JSON);

            Response response = restClient.performRequest(method, endpoint, Collections.<String, String>emptyMap(), entity);
            System.out.println(response.getEntity());
            restTemplate.delete("http://127.0.0.1:9200/elastic?pretty");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


