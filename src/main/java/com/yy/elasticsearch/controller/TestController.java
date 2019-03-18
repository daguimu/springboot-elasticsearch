package com.yy.elasticsearch.controller;


/**
 * @description: 测试的Rest
 * @author: Guimu
 * @create: 2018/07/24 20:33:12
 **/
public class TestController {


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


