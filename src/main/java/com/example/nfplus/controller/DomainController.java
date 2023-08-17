/*
 * @Description: 指标域控制类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 16:19:46
 * @LastEditors: wch
 * @LastEditTime: 2023-08-16 10:19:06
 */


package com.example.nfplus.controller;

import com.example.nfplus.entity.Domain;
import com.example.nfplus.entity.User;
import com.example.nfplus.service.DomainService;
import com.example.nfplus.service.UserService;
import com.example.nfplus.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/domain")
public class DomainController {
    @Autowired
    private DomainService domainService;
    @Autowired
    private UserService userService;

    /**
     * @description: 获取所有指标域(树形结构)
     * @param needAll 是否需要"全部"这个结点
     * @param allowParent 父指标域是否允许被选中
     * @return {ResultUtils}
     * @author: wch
     */    
    @GetMapping("/all")
    public ResultUtils getAllDomain(@RequestParam Boolean needAll, @RequestParam Boolean allowParent){
        try{
            return ResultUtils.ok().data("domains", domainService.getAllDomain(needAll, allowParent));
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("获取指标域失败");
        }
    }

    /**
     * @description: 获取指标域的所有子指标域(树形结构)
     * @param domainId 指标域id
     * @return {ResultUtils}
     */
    @GetMapping("/child")
    public ResultUtils getDomainChild(@RequestParam int domainId){
        if (domainService.getById(domainId) == null)
            return ResultUtils.error().message("指标域 " + domainId + " 不存在");
        try{
            return ResultUtils.ok().data("domains", domainService.getDomainChildById(domainId));
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("获取子指标域失败");
        }
    }

    /**
     * @description: 获取指标域中的所有指标
     * @param domainId 指标域id
     * @return {ResultUtils}
     * @author: wch
     */    
    @GetMapping("/indicators")
    public ResultUtils getQuoteIndicators(@RequestParam int domainId){
        if (domainService.getById(domainId) == null)
            return ResultUtils.error().message("指标域 " + domainId + " 不存在");
        try{
            return ResultUtils.ok().data("indicators", domainService.findQuoteIndicators(domainId));
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("查找指标域 " + + domainId + " 的指标失败");
        }
    }

    /**
     * @description: 新增指标域
     * @param token 用户token
     * @param domain 指标域信息
     * @return {ResultUtils}
     * @Author: wch
     */
    @PostMapping("/add")
    public ResultUtils addDomain(@RequestHeader("Authorization") String token, @RequestBody Domain domain){
        User creator = userService.findUserByToken(token);
        Domain existDomain = domainService.findDomainByName(domain.getDomainName());
        if (existDomain != null && domain.getParentDomainId() == existDomain.getParentDomainId())
            return ResultUtils.error().message("指标域名重复");
        if (domain.getParentDomainId() != null && domainService.findQuoteIndicators(domain.getParentDomainId()).size() != 0)
            return ResultUtils.error().message("该指标域中已有指标,不能添加新的子指标域");
        try{
            domain.setDomainId(null);
            domainService.verifyDomain(domain);
            domain.setCreatorId(creator.getUserId());
            domainService.save(domain);
            return ResultUtils.ok().message("新增指标域成功");
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            return ResultUtils.error().message(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("新增指标域失败");
        }
    }

    /**
     * @description: 修改指标域信息
     * @param token 用户token
     * @param domain 指标域信息
     * @return {ResultUtils}
     * @author: wch
     */    
    @PostMapping("/update")
    public ResultUtils updateDomain(@RequestHeader("Authorization") String token, @RequestBody Domain domain){
        try{
            domainService.verifyDomain(domain);
            Domain existDomain = domainService.getById(domain.getDomainId());
            existDomain.setDomainName(domain.getDomainName());
            existDomain.setParentDomainId(domain.getParentDomainId());
            domainService.updateById(existDomain);
            return ResultUtils.ok().message("修改指标域信息成功");
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            return ResultUtils.error().message(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("修改指标域信息失败");
        }
    }

    /**
     * @description: 删除指标域
     * @param token 用户token
     * @param domainId 指标域id
     * @return {ResultUtils}
     * @author: wch
     */    
    @DeleteMapping("/delete")
    public ResultUtils deleteDomain(@RequestHeader("Authorization") String token, @RequestParam int domainId){
        if (domainService.getById(domainId) == null)
            return ResultUtils.error().message("指标域 " + domainId + " 不存在");
        if (domainService.findQuoteIndicators(domainId).size() != 0)
            return ResultUtils.error().message("该指标域中存在" + domainService.findQuoteIndicators(domainId).size() + "个指标，不可删除");
        if (domainService.getDomainChildById(domainId).size() > 1)
            return ResultUtils.error().message("该指标域中存在其他子指标域,不可删除");
        try{
            domainService.deleteDomain(domainId);
            return ResultUtils.ok().message("删除指标域成功");
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            return ResultUtils.error().message(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("删除指标域失败");
        }
    }
}