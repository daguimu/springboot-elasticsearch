package com.yy.elasticsearch.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: es实体测试类
 * @author: Guimu
 * @create: 2018/07/24 20:17:15
 **/
//@Document(indexName = "elasticsearch1", type = "article", indexStoreType ="niofs", shards = 5, replicas = 1, refreshInterval = "-1")
@Data
public class Company extends Base implements Serializable {
    private static final long serialVersionUID = -1849792707724667719L;
    private String name;
}


