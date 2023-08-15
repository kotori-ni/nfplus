/*
 * @Description: 指标搜索条件类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-30 23:35:18
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 16:53:32
 */


package com.example.nfplus.entity;

import lombok.Data;

@Data
public class IndicatorQuery {
    private Boolean needPage = true;    //是否需要分页
    private Integer page;               //请求第几页
    private Integer pageSize;           //每页大小
    private Integer indicatorType;      //指标类型
    private Integer indicatorState;     //指标状态
    private Integer creatorId;          //创建者id
    private Integer domainId;           //指标域id
    private String keyword;             //模糊搜索关键词
    private String sortMethod;          //搜索范围
}