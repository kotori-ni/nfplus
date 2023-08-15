/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 18:17:27
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 11:42:57
 */
package com.example.nfplus.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.nfplus.entity.Indicator;
import com.example.nfplus.entity.Modifier;
import com.example.nfplus.entity.User;
import com.example.nfplus.entity.WordsQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ModifierService extends IService<Modifier> {
    /**
     * @description: 获取所有修饰词
     * @param {User} user 请求查询的用户
     * @param {Boolean} needAll 是否需要"全部"这个结点
     * @param {Boolean} allowParent 修饰词值是否可选 可选则disable属性为false,否则为true
     * @return {List<Map<String, Object>>} 修饰词列表
     * @author: wch
     */    
    public List<Map<String, Object>> getAllModifier(User user, Boolean needAll, Boolean allowParent);

    /**
     * @description: 按搜索条件获取修饰词
     * @param {User} user 请求查询的用户
     * @param {WordsQuery} wordsQuery 查询条件
     * @return {List<Modifier>} 修饰词列表
     * @author: wch
     */    
    public List<Modifier> getModifiers(User user, WordsQuery wordsQuery);

    /**
     * @description: 按搜索条件分页获取修饰词
     * @param {User} user 请求查询的用户
     * @param {WordsQuery} wordsQuery 查询条件
     * @return {Page<Modifier>} 修饰词分页列表
     * @author: wch
     */    
    public Page<Modifier> getModifiersWithPage(User user, WordsQuery wordsQuery);

    /**
     * @description: 获取引用该修饰词的指标列表
     * @param {int} modifierId 修饰词id
     * @return {List<Indicator>} 指标列表
     * @author: wch
     */    
    public List<Indicator> findQuoteIndicators(int modifierId);

    /**
     * @description: 新增修饰词
     * @param {User} user 请求新增的用户
     * @param {Modifier} modifier 新增的修饰词
     * @return {*}
     * @author: wch
     */    
    public void addModifier(User user, Modifier modifier);

    /**
     * @description: 根据修饰词键的名称获取修饰词键
     * @param {String} name 修饰词键名称
     * @return {Modifier} 修饰词键对象
     * @author: wch
     */    
    public Modifier findModifierKeyByName(String name);

    /**
     * @description: 更新修饰词信息
     * @param {User} user 请求更新的用户
     * @param {Modifier} modifier 更新的修饰词
     * @return {*}
     * @author: wch
     */    
    public void updateModifier(User user, Modifier modifier);

    /**
     * @description: 验证修饰词信息是否正确
     * @param {Modifier} modifier
     * @return {Boolean} true:正确 错误则抛出异常
     * @author: wch
     */    
    public Boolean verifyModifier(Modifier modifier);
}
