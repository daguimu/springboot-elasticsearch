package com.yy.elasticsearch.serviceimpl;

import com.yy.elasticsearch.dao.CompanySearchRepository;
import com.yy.elasticsearch.model.Base;
import com.yy.elasticsearch.model.Company;
import com.yy.elasticsearch.service.CompanyElasticService;
import com.yy.elasticsearch.utils.EsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 公司实体数据操作接口实现类
 * @author: Guimu
 * @create: 2018/07/25 11:20:45
 **/
@Service
public class CompanyElasticServiceImpl implements CompanyElasticService {
    @Autowired
    EsUtils esTool;
    private static final String COMPANY_TYPE = "Company";

    @Override
    public boolean addCompany(Company newCompany) {
        //检查数据是否已经存在
        String[] nameArr = newCompany.getClass().getName().split("\\.");
        if (null != esTool.queryOneBase(newCompany.getName(), nameArr[nameArr.length - 1], newCompany.getCorpId())) {
            System.out.println("该数据已存在,不可重复添加");
            return false;
        }
        return esTool.singleSave(newCompany);
    }

    @Override
    public boolean batchAddCompany(List<Company> companies) {
        return esTool.batchSave(companies);
    }

    @Override
    public boolean deleteCompany(Company deleteCompany) {
        Base deleBase = this.getOneCompany(deleteCompany.getName(), deleteCompany.getCorpId());
        return null != deleBase && esTool.deleteBase(deleBase);
    }

    @Override
    public boolean updateCompanyName(Company oldTargetCompany, String newName) {
        //此处传入的oldTargetCompany没有id值, 查找一次主要是为了根据目标信息获取该数据所有的信息
        Company oldCompany = this.getOneCompany(oldTargetCompany.getName(), oldTargetCompany.getCorpId());
        oldCompany.setName(newName);
        return esTool.updateBase(oldCompany);
    }

    @Override
    public List<Company> getAllCompany() {
        return this.baseToCompany(esTool.findAll(EsUtils.getDefaultIndex(), COMPANY_TYPE));
    }

    @Override
    public List<Company> getSomeCompanyByCorpId(Long corpId) {
        return this.baseToCompany(esTool.queryByCorpId(COMPANY_TYPE, corpId));
    }

    @Override
    public List<Company> queryCompanyLikeNameAndCorpId(String keyWord, Long corpId) {
        return this.baseToCompany(esTool.queryLikeNameAndCorpId(keyWord, COMPANY_TYPE, corpId));
    }

    @Override
    public Company getOneCompany(String targetName, Long corpId) {
        return esTool.queryOneBase(targetName, COMPANY_TYPE, corpId);
    }

    private List<Company> baseToCompany(List<Base> bases) {
        return bases.stream().map(el -> (Company) el).collect(Collectors.toList());
    }
}


