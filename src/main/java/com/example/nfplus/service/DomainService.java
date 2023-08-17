/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:36:37
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:42:48
 */
package com.example.nfplus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.nfplus.entity.Domain;
import com.example.nfplus.entity.Indicator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface DomainService extends IService<Domain>{
    /**
     * @description: 根据指标域名查询指标
     * @param name 指标域名
     * @return {Domain} 指标域
     * @author: wch
     */
    public Domain findDomainByName(String name);

    /**
     * @description: 获取所有指标域
     * @param needAll 是否需要"全部"这个指标域结点
     * @param allowParent 父指标域是否可选 可选则disable属性为false,否则为true
     * @return {List<Map<String, Object>>} 指标域树形列表
     * @author: wch
     */    
    public List<Map<String, Object>> getAllDomain(Boolean needAll, Boolean allowParent);

    /**
     * @description: 获取指定指标域的所有子指标域
     * @param domainId 指标域id
     * @return {List<Domain>} 子指标域列表,包含父指标域本身
     * @author: wch
     */    
    public List<Domain> getDomainChildById(int domainId);

    /**
     * @description: 查找指标域中的所有指标
     * @param domainId 指标域id
     * @return {List<Indicator>} 指标列表
     * @author: wch
     */    
    public List<Indicator> findQuoteIndicators(int domainId);

    /**
     * @description: 删除指标域
     * @param domainId 指标域id
     * @return {*}
     * @author: wch
     */    
    public void deleteDomain(int domainId);

    /**
     * @description: 验证指标域中的信息是否合法
     * @param domain 待验证的指标域
     * @return {boolean} 正确返回true,否则抛出异常
     * @author: wch
     */    
    public boolean verifyDomain(Domain domain);
}
