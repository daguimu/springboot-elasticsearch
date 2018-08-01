package com.yy.elasticsearch.model;

import lombok.Data;

/**
 * @description: 基本类
 * @author: Guimu
 * @create: 2018/07/25 16:31:46
 **/
@Data
public class Base {
    private String index;//属于哪个索引 类似于库
    private String type;//属于索引中的哪个type 类似于表

    private String id;    //es 物理主键
    private Long corpId;
}


