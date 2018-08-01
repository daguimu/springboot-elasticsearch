package com.yy.elasticsearch.dao;

import com.yy.elasticsearch.model.Company;

import java.util.List;


/**
 * @description: 搜索类
 * @author: Guimu
 * @create: 2018/07/24 20:21:06
 **/
public interface CompanySearchRepository {
    /**
     * @Description: 模糊匹配查询 == like %name%
     * @Param: [name, pageable]
     * @Return: org.springframework.data.domain.Page<com.yy.elasticsearch.model.Company>
     * @Author: Guimu
     * @Date: 2018/7/25  上午10:24
     */
    //Page<Company> findByNameContainingAndCorpId(String name, Long corpId, Pageable pageable);

    /**
     * @Description: 查询获取全部数据
     * @Param: []
     * @Return: java.util.List<com.yy.elasticsearch.model.Company>
     * @Author: Guimu
     * @Date: 2018/7/25  上午10:25
     */
    List<Company> findAll();

    /**
     * @Description: 删除指定名字的元素
     * @Param: [name]
     * @Return: void
     * @Author: Guimu
     * @Date: 2018/7/25  上午10:46
     */
    void deleteByNameAndCorpId(String name, Long corpId);

    /**
     * @Description: 精确查找
     * @Param: [name, corpId]
     * @Return: com.yy.elasticsearch.model.Company
     * @Author: Guimu
     * @Date: 2018/7/26  下午6:09
     */
    Company findByNameAndCorpId(String name, Long corpId);

    List<Company> findByCorpId(Long corpId);
}


