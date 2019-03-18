package com.yy.elasticsearch.controller;


import com.yy.elasticsearch.model.Base;
import com.yy.elasticsearch.model.Company;
import com.yy.elasticsearch.service.CompanyElasticService;
import com.yy.elasticsearch.utils.CompanyUtils;
import com.yy.elasticsearch.utils.EsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @description: 真的是测试
 * @author: Guimu
 * @create: 2018/07/30 20:53:37
 **/
@RestController
public class Temo {
    @Autowired
    CompanyElasticService cService;

    @RequestMapping(value = "init")
    public String elasticSearchInit(@RequestParam(value = "count") Integer count) {
        Company company;
        Random random = new Random();
        List<Company> list = new LinkedList<Company>();
        for (int i = 0; i < count; i++) {
            company = new Company();
            company.setIndex("elastic");
            String tempName = CompanyUtils.generateCompanySource();
            company.setName(tempName);
            Integer tempCorpId = random.nextInt(20);
            company.setCorpId(tempCorpId.longValue() + 1);
            list.add(company);
        }
        long start = System.currentTimeMillis();
        cService.batchAddCompany(list);
        printMills(start, "初始化" + count + "条的效率为");
        return "init success";
    }

    @RequestMapping(value = "getall")
    public List<Company> findAll() {
        return cService.getAllCompany();
    }

    @RequestMapping(value = "query")
    public List<Company> queryCompanyLike(@RequestParam(value = "target") String target, @RequestParam(value = "corpId") long corpId) {

        long start = System.currentTimeMillis();
        List<Company> list = cService.queryCompanyLikeNameAndCorpId(target, corpId);
        printMills(start, "模糊匹配效率");
        return list;
    }

    @RequestMapping(value = "querybycorpid")
    public List<Company> queryCompanyByCorpId(@RequestParam(value = "corpId") long corpId) {
        return cService.getSomeCompanyByCorpId(corpId);
    }

    @RequestMapping(value = "delete")
    public boolean deleteBase(@RequestParam(value = "dename") String dename, @RequestParam(value = "corpId") long corpId) {
        Company deleteCom = new Company();
        deleteCom.setName(dename);
        deleteCom.setCorpId(corpId);
        boolean flag = cService.deleteCompany(deleteCom);
        System.out.println(flag);
        return flag;
    }


    @RequestMapping(value = "save")
    public void add(@RequestParam(value = "name") String name, long corpId) {
        Company company = new Company();
        company.setName(name);
        company.setCorpId(corpId);
        cService.addCompany(company);
    }

    @RequestMapping(value = "update")
    public boolean update(@RequestParam(value = "newname") String newname, long corpId, @RequestParam(value = "oldname") String oldname) {
        Company oldCompany = cService.getOneCompany(oldname, corpId);
        boolean flag = cService.updateCompanyName(oldCompany, newname);
        System.out.println("update result:" + flag);
        return flag;
    }

    private String printMills(Long start, String message) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(message + ":   ");
        Long end = System.currentTimeMillis();
        stringBuffer.append(end - start);
        stringBuffer.append("ms");
        System.out.println(stringBuffer.toString());
        return stringBuffer.toString();
    }
}


