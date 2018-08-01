package com.yy.elasticsearch.service;

import com.yy.elasticsearch.model.Company;

import java.util.List;

/**
 * @description: 公司实体数据操作接口
 * @author: Guimu
 * @create: 2018/07/25 11:12:29
 **/
public interface CompanyElasticService {
    /**
     * @Description: 添加Company数据, 内部会进行检查数据是否已经存在，返回boolean值代表是否添加成功
     * @Param: [newCompany]
     * @Return: boolean
     * @Author: Guimu
     * @Date: 2018/8/1  上午9:25
     */
    boolean addCompany(Company newCompany);

    /**
    *@Description: 批量添加Company数据
    *@Param: [companies]
    *@Return: boolean
    *@Author: Guimu
    *@Date: 2018/8/1  上午10:52
    */
    boolean batchAddCompany(List<Company> companies);

    /**
     * @Description: 删除某Company数据, 返回boolean代表是否成功
     * @Param: [deleteCompany]
     * @Return: boolean
     * @Author: Guimu
     * @Date: 2018/8/1  上午9:26
     */
    boolean deleteCompany(Company deleteCompany);

    /**
     * @Description: 更新Company数据的name，传入的是就得Company和新的名字
     * @Param: [oldCompany, newName]
     * @Return: boolean
     * @Author: Guimu
     * @Date: 2018/8/1  上午9:27
     */
    boolean updateCompanyName(Company oldTargetCompany, String newName);

    /**
     * @Description: 获取所有的Company数据
     * @Param: []
     * @Return: java.util.List<com.yy.elasticsearch.model.Company>
     * @Author: Guimu
     * @Date: 2018/8/1  上午9:28
     */
    List<Company> getAllCompany();

    /**
     * @Description: 通过corpId获取Company数据
     * @Param: [corpId]
     * @Return: java.util.List<com.yy.elasticsearch.model.Company>
     * @Author: Guimu
     * @Date: 2018/8/1  上午9:29
     */
    List<Company> getSomeCompanyByCorpId(Long corpId);

    /**
     * @Description: 进行corpId精确匹配和company数据的name模糊匹配的多条件查询
     * @Param: [keyWord, corpId]
     * @Return: java.util.List<com.yy.elasticsearch.model.Company>
     * @Author: Guimu
     * @Date: 2018/8/1  上午9:30
     */
    List<Company> queryCompanyLikeNameAndCorpId(String keyWord, Long corpId);

    /**
     * @Description: 精确查询Company数据
     * @Param: [targetName, corpId]
     * @Return: com.yy.elasticsearch.model.Company
     * @Author: Guimu
     * @Date: 2018/8/1  上午9:31
     */
    Company getOneCompany(String targetName, Long corpId);


}
