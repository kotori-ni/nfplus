/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 18:16:58
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 10:47:53
 */

package com.example.nfplus.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.nfplus.entity.Derivation;
import com.example.nfplus.entity.Indicator;
import com.example.nfplus.entity.User;
import com.example.nfplus.entity.WordsQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DerivationService extends IService<Derivation> {
    /**
     * @description: 查询引用该衍生词的指标列表
     * @param {int} derivationId 衍生词id
     * @return {List<Indicator>} 指标列表
     * @author: wch
     */
    public List<Indicator> findQuoteIndicators(int derivationId);

    /**
     * @description: 按搜索条件查询衍生词
     * @param {User}       user 请求查询的用户
     * @param {WordsQuery} wordsQuery 搜索条件
     * @return {List<Derivation>} 衍生词列表
     * @author: wch
     */
    public List<Derivation> getDerivations(User user, WordsQuery wordsQuery);

    /**
     * @description: 按搜索条件分页查询衍生词
     * @param {User}       user 请求查询的用户
     * @param {WordsQuery} wordsQuery 搜索条件
     * @return {Page<Derivation>} 衍生词分页列表
     * @author: wch
     */
    public Page<Derivation> getDerivationsWithPage(User user, WordsQuery wordsQuery);
}
