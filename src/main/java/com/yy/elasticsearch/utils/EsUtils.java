package com.yy.elasticsearch.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yy.elasticsearch.model.Base;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * @description: es工具类
 * @author: Guimu
 * @create: 2018/07/31 11:47:55
 **/
@Component
public class EsUtils {
    @Autowired
    private RestHighLevelClient client;
    private static final String CURRENT_MODEL_PACKAGE_NAME = "com.yy.elasticsearch.model.";
    private static final String DEFAULT_INDEX = "elasticsearch";

    /**
     * @Description: 根据Base 子类数据产生一个IndexRequest数据
     * @Param: [source]
     * @Return: org.elasticsearch.action.index.IndexRequest
     * @Author: Guimu
     * @Date: 2018/7/31  下午5:30
     */
    private IndexRequest indexRequestGenerater(Base source) {
        IndexRequest indexRequest = null;
        if (StringUtils.isEmpty(source.getIndex())) {
            source.setIndex(DEFAULT_INDEX);
        }
        try {
            String[] tempArr = source.getClass().getName().split("\\.");
            source.setType(tempArr[tempArr.length - 1]);
            indexRequest = new IndexRequest(source.getIndex(), source.getType());
            indexRequest.source(JacksonUtil.getString(source), XContentType.JSON);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return indexRequest;
    }

    /**
     * @Description: 批量存储接口, boolean  表示保存成功与否
     * @Param: [bases]
     * @Return: boolean
     * @Author: Guimu
     * @Date: 2018/7/31  下午5:32
     */
    public boolean batchSave(List<? extends Base> bases) {
        BulkRequest bulkRequest = new BulkRequest();
        bases.forEach(el -> bulkRequest.add(indexRequestGenerater(el)));
        boolean flag = false;
        try {
            BulkResponse bulkItemResponses = client.bulk(bulkRequest);
            flag = "created".equalsIgnoreCase(bulkItemResponses.getItems()[0].getResponse().getResult().name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * @Description: 保存单个索引文档数据
     * @Param: [source]
     * @Return: boolean
     * @Author: Guimu
     * @Date: 2018/7/31  下午2:05
     */
    public boolean singleSave(Base source) {
        IndexRequest singleRequest;
        boolean flag = false;
        try {
            singleRequest = indexRequestGenerater(source);
            IndexResponse indexResponse = client.index(singleRequest);
            flag = "created".equalsIgnoreCase(indexResponse.getResult().name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * @Description: 模糊匹配名字, 精确匹配corpId
     * @Param: [name, corpId]
     * @Return: java.util.List<com.yy.elasticsearch.model.Base>
     * @Author: Guimu
     * @Date: 2018/7/31  下午2:21
     */
    public List<Base> queryLikeNameAndCorpId(String name, String type, Long corpId) {
        MatchPhraseQueryBuilder mb1 = QueryBuilders.matchPhraseQuery("corpId", corpId);
        MatchPhraseQueryBuilder mb2 = QueryBuilders.matchPhraseQuery("name", "*" + name + "*");
        QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(mb1).must(mb2);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        SearchRequest searchRequest = new SearchRequest(DEFAULT_INDEX);
        searchRequest.types(type);
        searchRequest.source(searchSourceBuilder);
        return baseQuery(searchRequest);
    }

    /**
     * @Description: 查找指定corpId和type的所有数据
     * @Param: [name, corpId]
     * @Return: java.util.List<com.yy.elasticsearch.model.Base>
     * @Author: Guimu
     * @Date: 2018/7/31  下午2:21
     */
    public List<Base> queryByCorpId(String type, Long corpId) {
        MatchPhraseQueryBuilder mb = QueryBuilders.matchPhraseQuery("corpId", corpId);
        QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(mb);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        SearchRequest searchRequest = new SearchRequest(DEFAULT_INDEX);
        searchRequest.types(type);
        searchRequest.source(searchSourceBuilder);
        return baseQuery(searchRequest);
    }

    /**
     * @Description: 查询该索引中, 指定type的所有数据
     * @Param: [index, type]
     * @Return: java.util.List<com.yy.elasticsearch.model.Base>
     * @Author: Guimu
     * @Date: 2018/7/31  下午6:06
     */
    public List<Base> findAll(String index, String type) {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(this.getCount(searchRequest).intValue());
        searchRequest.source(searchSourceBuilder);
        //查询全部xxxxx
        searchRequest.types(type);
        return baseQuery(searchRequest);
    }

    /**
     * @Description: 获取该查询请求的总条数total
     * @Param: [searchRequest]
     * @Return: java.lang.Long
     * @Author: Guimu
     * @Date: 2018/7/31  下午4:58
     */
    private Long getCount(SearchRequest searchRequest) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(1);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse response = client.search(searchRequest);
            return response.getHits().getTotalHits();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1L;
    }

    /**
     * @Description: 私有的基础查询, 提高代码复用性
     * @Param: [searchRequest]
     * @Return: java.util.List<com.yy.elasticsearch.model.Base>
     * @Author: Guimu
     * @Date: 2018/7/31  下午6:15
     */
    private List<Base> baseQuery(SearchRequest searchRequest) {
        try {
            SearchResponse response = client.search(searchRequest);
            return Arrays.asList(response.getHits().getHits()).stream().map(el -> {
                Map<String, Object> map = el.getSource();
                Class clazz;
                Base base = null;
                try {
                    map.put("id", el.getId());
                    clazz = Class.forName(CURRENT_MODEL_PACKAGE_NAME + map.get("type").toString());
                    base = (Base) JacksonUtil.getObject(JacksonUtil.getString(map), clazz);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return base;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Description: 基础删除接口, 支持Base的所有子类
     * @Param: [base]
     * @Return: boolean
     * @Author: Guimu
     * @Date: 2018/7/31  下午6:32
     */
    public boolean deleteBase(Base base) {
        DeleteRequest deleteRequest = new DeleteRequest(base.getIndex(), base.getType(), base.getId());
        boolean flag = false;
        try {
            DeleteResponse deleteResponse = client.delete(deleteRequest);
            flag = "deleted".equalsIgnoreCase(deleteResponse.getResult().name());
            System.out.println(deleteResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * @Description: 此处传入的base的所有值都是全的，没有null值
     * @Param: [base]
     * @Return: boolean
     * @Author: Guimu
     * @Date: 2018/8/1  上午10:02
     */
    public boolean updateBase(Base base) {
        UpdateRequest updateRequest = new UpdateRequest(base.getIndex(), base.getType(), base.getId());
        boolean flag = false;
        try {
            updateRequest.doc(JacksonUtil.getString(base), XContentType.JSON);
            UpdateResponse updateResponse = client.update(updateRequest);
            flag = "updated".equalsIgnoreCase(updateResponse.getResult().name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * @Description: 根据名字, type, corpId 进行精确查询Base数据,没找到则返回 null
     * @Param: [name, type, corpId]
     * @Return: com.yy.elasticsearch.model.Base
     * @Author: Guimu
     * @Date: 2018/8/1  上午9:35
     */
    public <T extends Base> T queryOneBase(String name, String type, Long corpId) {
        MatchPhraseQueryBuilder mb = QueryBuilders.matchPhraseQuery("corpId", corpId);
        MatchPhraseQueryBuilder mb1 = QueryBuilders.matchPhraseQuery("name", name);
        QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(mb).must(mb1);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        SearchRequest searchRequest = new SearchRequest(DEFAULT_INDEX);
        searchRequest.types(type);
        searchRequest.source(searchSourceBuilder);
        Base rebase = null;
        try {
            rebase = baseQuery(searchRequest).get(0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        Class<T> aClass = null;
        try {
            aClass = (Class<T>) Class.forName(CURRENT_MODEL_PACKAGE_NAME + type);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null == rebase ? null : aClass.cast(rebase);
    }

    public static String getDefaultIndex() {
        return DEFAULT_INDEX;
    }
}


