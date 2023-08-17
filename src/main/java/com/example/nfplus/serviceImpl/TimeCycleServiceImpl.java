/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 20:59:25
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:50:07
 */
package com.example.nfplus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.nfplus.entity.Indicator;
import com.example.nfplus.entity.TimeCycle;
import com.example.nfplus.entity.User;
import com.example.nfplus.entity.WordsQuery;
import com.example.nfplus.mapper.TimeCycleMapper;
import com.example.nfplus.service.TimeCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeCycleServiceImpl extends ServiceImpl<TimeCycleMapper, TimeCycle> implements TimeCycleService {
    @Autowired
    private TimeCycleMapper timeCycleMapper;

    /**
     * @description: 查找引用该时间周期的指标列表
     * @param timeCycleId 时间周期id
     * @return {List<Indicator>} 指标列表
     * @author: wch
     */
    @Override
    public List<Indicator> findQuoteIndicators(int timeCycleId) {
        return timeCycleMapper.selectQuoteIndicators(timeCycleId);
    }

    /**
     * @description: 按搜索条件查找时间周期
     * @param user       请求查询的用户
     * @param wordsQuery 搜索条件
     * @return {List<TimeCycle>} 时间周期列表
     * @author: wch
     */
    @Override
    public List<TimeCycle> getTimeCycles(User user, WordsQuery wordsQuery) {
        QueryWrapper<TimeCycle> queryWrapper = getQueryWrapper(wordsQuery);
        return timeCycleMapper.selectTimeCycles(user, queryWrapper);
    }

    /**
     * @description: 按搜索条件分页查找时间周期
     * @param user       请求查询的用户
     * @param wordsQuery 搜索条件
     * @return {Page<TimeCycle>} 时间周期分页列表
     * @author: wch
     */
    @Override
    public Page<TimeCycle> getTimeCyclesWithPage(User user, WordsQuery wordsQuery) {
        QueryWrapper<TimeCycle> queryWrapper = getQueryWrapper(wordsQuery);
        Page<TimeCycle> page = new Page<>(wordsQuery.getPage(), wordsQuery.getPageSize());
        List<TimeCycle> timeCycles = timeCycleMapper.selectTimeCycles(page, user, queryWrapper);
        int index = (int) ((page.getCurrent() - 1) * page.getSize());
        for (TimeCycle timeCycle : timeCycles)
            timeCycle.setIndex(++index);
        page.setRecords(timeCycles);
        return page;
    }

    /**
     * @description: 根据搜索条件构造数据库查询条件
     * @param wordsQuery 搜索条件
     * @return {QueryWrapper<TimeCycle>} 数据库查询条件对象
     * @author: wch
     */
    public QueryWrapper<TimeCycle> getQueryWrapper(WordsQuery wordsQuery) {
        QueryWrapper<TimeCycle> queryWrapper = new QueryWrapper<>();
        if (wordsQuery.getSort() != null && wordsQuery.getKeyword() != null) {
            queryWrapper.like(wordsQuery.getSort().equals("all") || wordsQuery.getSort().equals("timeCycleName"),
                    "time_cycle_name", wordsQuery.getKeyword());
            queryWrapper.or();
            queryWrapper.like(wordsQuery.getSort().equals("all") || wordsQuery.getSort().equals("creator"), "username",
                    wordsQuery.getKeyword());
        }
        return queryWrapper;
    }
}
