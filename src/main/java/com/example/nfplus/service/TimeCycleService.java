/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 18:17:57
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:05:51
 */
package com.example.nfplus.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.nfplus.entity.Indicator;
import com.example.nfplus.entity.TimeCycle;
import com.example.nfplus.entity.User;
import com.example.nfplus.entity.WordsQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TimeCycleService extends IService<TimeCycle> {
    /**
     * @description: 查找引用该时间周期的指标列表
     * @return {List<Indicator>} 指标列表
     * @author: wch
     */    
    public List<Indicator> findQuoteIndicators(int timeCycleId);

    /**
     * @description: 按搜索条件查找时间周期
     * @return {List<TimeCycle>} 时间周期列表
     * @author: wch
     */    
    public List<TimeCycle> getTimeCycles(User user, WordsQuery wordsQuery);

    /**
     * @description: 按搜索条件分页查找时间周期
     * @return {Page<TimeCycle>} 时间周期分页列表
     * @author: wch
     */    
    public Page<TimeCycle> getTimeCyclesWithPage(User user, WordsQuery wordsQuery);
}
