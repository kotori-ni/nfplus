/*
 * @Description: 衍生词,修饰词,时间周期搜索类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-30 11:54:29
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 17:00:19
 */


package com.example.nfplus.entity;

import lombok.Data;

@Data
public class WordsQuery {
    private Integer page;       //请求第几页
    private Integer pageSize;   //每页大小
    private Boolean needPage;   //是否需要分页
    private String sort;        //搜索范围
    private String keyword;     //模糊搜索关键词
    private String orderBy;     //排序方式(未使用)
}

