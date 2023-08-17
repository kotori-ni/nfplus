/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 20:57:46
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:46:37
 */
package com.example.nfplus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.nfplus.entity.Derivation;
import com.example.nfplus.entity.Indicator;
import com.example.nfplus.entity.User;
import com.example.nfplus.entity.WordsQuery;
import com.example.nfplus.mapper.DerivationMapper;
import com.example.nfplus.mapper.IndicatorMapper;
import com.example.nfplus.service.DerivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DerivationServiceImpl extends ServiceImpl<DerivationMapper, Derivation> implements DerivationService {
    @Autowired
    private DerivationMapper derivationMapper;
    @Autowired
    private IndicatorMapper indicatorMapper;

    /**
     * @description: 查询引用该衍生词的指标列表
     * @param derivationId 衍生词id
     * @return {List<Indicator>} 指标列表
     * @author: wch
     */
    @Override
    public List<Indicator> findQuoteIndicators(int derivationId) {
        List<String> indicatorIds = derivationMapper.selectQuoteIndicators(derivationId);
        if (indicatorIds == null || indicatorIds.size() == 0)
            return null;
        return indicatorMapper.selectBatchIds(indicatorIds);
    }

    /**
     * @description: 按搜索条件查询衍生词
     * @param user       请求查询的用户
     * @param wordsQuery 搜索条件
     * @return {List<Derivation>} 衍生词列表
     * @author: wch
     */
    @Override
    public List<Derivation> getDerivations(User user, WordsQuery wordsQuery) {
        QueryWrapper<Derivation> queryWrapper = getQueryWrapper(wordsQuery);
        return derivationMapper.selectDerivations(user, queryWrapper);
    }

    /**
     * @description: 按搜索条件分页查询衍生词
     * @param user       请求查询的用户
     * @param wordsQuery 搜索条件
     * @return {Page<Derivation>} 衍生词分页列表
     * @author: wch
     */
    @Override
    public Page<Derivation> getDerivationsWithPage(User user, WordsQuery wordsQuery) {
        QueryWrapper<Derivation> queryWrapper = getQueryWrapper(wordsQuery);
        Page<Derivation> page = new Page<>(wordsQuery.getPage(), wordsQuery.getPageSize());
        List<Derivation> derivations = derivationMapper.selectDerivations(page, user, queryWrapper);
        int index = (int) ((page.getCurrent() - 1) * page.getSize());
        for (Derivation derivation : derivations)
            derivation.setIndex(++index);
        page.setRecords(derivations);
        return page;
    }

    /**
     * @description: 根据搜索条件构造查询条件
     * @param wordsQuery 搜索条件
     * @return {QueryWrapper<Derivation>} 查询条件包装类对象
     * @author: wch
     */
    private QueryWrapper<Derivation> getQueryWrapper(WordsQuery wordsQuery) {
        QueryWrapper<Derivation> queryWrapper = new QueryWrapper<>();
        if (wordsQuery.getSort() != null && wordsQuery.getKeyword() != null) {
            queryWrapper.like(wordsQuery.getSort().equals("all") || wordsQuery.getSort().equals("derivationName"),
                    "derivation_name", wordsQuery.getKeyword());
            queryWrapper.or();
            queryWrapper.like(wordsQuery.getSort().equals("all") || wordsQuery.getSort().equals("calculationCaliber"),
                    "calculation_caliber", wordsQuery.getKeyword());
            queryWrapper.or();
            queryWrapper.like(wordsQuery.getSort().equals("all") || wordsQuery.getSort().equals("creator"), "username",
                    wordsQuery.getKeyword());
        }
        return queryWrapper;
    }
}
