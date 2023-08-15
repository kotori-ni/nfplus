/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:38:15
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 10:58:44
 */
package com.example.nfplus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.nfplus.entity.Domain;
import com.example.nfplus.entity.Indicator;
import com.example.nfplus.mapper.DomainMapper;
import com.example.nfplus.service.DomainService;
import com.example.nfplus.service.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DomainServiceImpl extends ServiceImpl<DomainMapper, Domain> implements DomainService {
    @Autowired
    private IndicatorService indicatorService;
    @Autowired
    private DomainMapper domainMapper;

    /**
     * @description: 获取所有指标域
     * @param {Boolean} needAll 是否需要"全部"这个指标域结点
     * @param {Boolean} allowParent 父指标域是否可选 可选则disable属性为false,否则为true
     * @return {List<Map<String, Object>>} 指标域树形列表
     * @author: wch
     */    
    @Override
    public List<Map<String, Object>> getAllDomain(Boolean needAll, Boolean allowParent) {
        List<Domain> domains = domainMapper.getAllDomain();
        List<Map<String, Object>> domainHierarchy = new ArrayList<>();

        //构建父子域关系映射
        Map<Integer, List<Domain>> domainMap = new HashMap<>();
        for (Domain domain : domains) {
            int parentId = 0;
            if (domain.getParentDomainId() != null)
                parentId = domain.getParentDomainId();
            domainMap.putIfAbsent(parentId, new ArrayList<>());
            domainMap.get(parentId).add(domain);
        }

        // 从根节点开始递归构建类似结构的数据
        List<Domain> rootDomains = domainMap.get(0);
        if (needAll){
            Map<String, Object> allDomainNode = new HashMap<>();
            allDomainNode.put("value",0);
            allDomainNode.put("label","全部");
            domainHierarchy.add(allDomainNode);
        }

        if (rootDomains != null) {
            for (Domain rootDomain : rootDomains) {
                Map<String, Object> domainNode = createAllDomainNode(rootDomain, domainMap, allowParent);
                domainHierarchy.add(domainNode);
            }
        }

        return domainHierarchy;
    }

    /**
     * @description: 获取指定指标域的所有子指标域
     * @param {int} domainId 指标域id
     * @return {List<Domain>} 子指标域列表,包含父指标域本身
     * @author: wch
     */  
    @Override
    public List<Domain> getDomainChildById(int domainId) {
        List<Domain> list = new ArrayList<>();

        Domain domain = getById(domainId);
        //深度搜索找出domainId的所有子域
        Stack<Domain> stack = new Stack<>();
        stack.push(domain);
        while (!stack.isEmpty()) {
            Domain currentDomain = stack.pop();
            list.add(currentDomain);
            QueryWrapper<Domain> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("parent_domain_id", currentDomain.getDomainId());
            List<Domain> children = list(queryWrapper);
            if (children != null)
                stack.addAll(children);
        }

        return list;
    }

    /**
     * @description: 查找指标域中的所有指标
     * @param {int} domainId 指标域id
     * @return {List<Indicator>} 指标列表
     * @author: wch
     */    
    @Override
    public List<Indicator> findQuoteIndicators(int domainId) {
        return domainMapper.selectQuoteIndicators(domainId);
    }

    /**
     * @description: 删除指标域
     * @param {int} domainId 指标域id
     * @return {*}
     * @author: wch
     */
    @Override
    @Transactional
    public void deleteDomain(int domainId) throws IllegalArgumentException{
        List<Domain> domains = getDomainChildById(domainId);
        for (int i = domains.size() - 1; i >= 0; i--){
            Domain domain = domains.get(i);
            List<Indicator> indicators = indicatorService.query().eq("domain_id", domain.getDomainId()).list();
            if (indicators.size() > 0)
                throw new IllegalArgumentException("指标域 " + domain.getDomainName() + " 中存在 " + indicators.size() + "个指标,不能删除");
            removeById(domain.getDomainId());
        }
    }

    /**
     * @description: 验证指标域中的信息是否合法
     * @param {Domain} domain 待验证的指标域
     * @return {boolean} 正确返回true,否则抛出异常
     * @throws {IllegalArgumentException} 参数异常
     * @author: wch
     */  
    @Override
    public boolean verifyDomain(Domain domain) throws IllegalArgumentException{
        if (domain.getParentDomainId() != null && getById(domain.getParentDomainId()) == null)
            throw new IllegalArgumentException("父级指标域 " + domain.getParentDomainId() + " 不存在");
        if (domain.getDomainName() == null)
            throw new IllegalArgumentException("指标域名称不能为空");
        return true;
    }

    /**
     * @description: 递归构造指标域树形结构
     * @param {Domain} domain 根指标域
     * @param {Map<Integer, List<Domain>>} domainMap 指标域映射关系图
     * @param {Boolean} allowParent 父指标域是否可选 可选则disable属性为false,否则为true
     * @return {Map<String, Object>} 指标域树形结构结点信息
     * @author: wch
     */    
    private Map<String, Object> createAllDomainNode(Domain domain, Map<Integer, List<Domain>> domainMap, Boolean allowParent) {
        Map<String, Object> domainNode = new HashMap<>();
        domainNode.put("value", domain.getDomainId());
        domainNode.put("label", domain.getDomainName());
        domainNode.put("creatorId", domain.getCreatorId());
        domainNode.put("creatorName", domain.getCreatorName());
        domainNode.put("createTime", domain.getCreateTime());
        domainNode.put("parent", domain.getParentDomainId());
        domainNode.put("indicatorNum", domain.getIndicatorNum());
        domainNode.put("childNum", domain.getChildNum());

        List<Domain> children = domainMap.get(domain.getDomainId());
        if (children != null) {
            List<Map<String, Object>> childNodes = new ArrayList<>();
            for (Domain child : children) {
                Map<String, Object> childNode = createAllDomainNode(child, domainMap, allowParent);
                childNodes.add(childNode);
            }
            domainNode.put("children", childNodes);
            if (!allowParent)
                domainNode.put("disabled", true);
        }

        return domainNode;
    }
}
