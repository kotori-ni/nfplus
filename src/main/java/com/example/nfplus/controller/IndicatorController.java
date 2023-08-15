/*
 * @Description: 指标控制类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-30 23:21:48
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 16:12:13
 */


package com.example.nfplus.controller;

import com.example.nfplus.entity.*;
import com.example.nfplus.service.*;
import com.example.nfplus.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/indicator")
public class IndicatorController {
    @Autowired
    private IndicatorService indicatorService;
    @Autowired
    private UserService userService;

    /**
     * @description: 获取单个指标的详细信息
     * @param {String} token 用户token
     * @param {String} indicatorId 指标id
     * @return {ResultUtils}
     * @author: wch
     */    
    @GetMapping("/one")
    public ResultUtils getOneIndicator(@RequestHeader("Authorization") String token, @RequestParam String indicatorId){
        User user = userService.findUserByToken(token);
        try{
            Indicator indicator = indicatorService.getOneIndicator(user, indicatorId);
            if (indicator != null){
                indicator.setViewsNum(indicator.getViewsNum() + 1);
                indicatorService.updateById(indicator);
                return ResultUtils.ok().data("indicator", indicator);
            }
            return ResultUtils.error().message("指标不存在");
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("寻找指标" + indicatorId + "失败");
        }
    }

    /**
     * @description: 按浏览量从高到低获取所有指标
     * @param {String} token 用户token
     * @return {ResultUtils}
     * @author: wch
     */    
    @GetMapping("viewmax")
    public ResultUtils getViewMaxIndicators(@RequestHeader("Authorization") String token){
        try{
            return ResultUtils.ok().data("indicators", indicatorService.getViewMaxIndicators());
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("获取热门浏览指标失败");
        }
    }

    /**
     * @description: 获取用户收藏的指标
     * @param {String} token 用户token
     * @return {ResultUtils}
     * @author: wch
     */    
    @PostMapping("favour")
    public ResultUtils getFavourIndicators(@RequestHeader("Authorization") String token, @RequestBody IndicatorQuery indicatorQuery){
        if (indicatorQuery.getNeedPage() == null || (indicatorQuery.getNeedPage() == true && (indicatorQuery.getPage() == null || indicatorQuery.getPageSize() == null)))
            return ResultUtils.error().message("缺少分页参数");
        indicatorQuery.setCreatorId(null);
        indicatorQuery.setKeyword(null);
        indicatorQuery.setDomainId(null);
        indicatorQuery.setSortMethod(null);
        User user = userService.findUserByToken(token);

        try{
            if (indicatorQuery.getNeedPage() == true)
                return ResultUtils.ok().data("indicators", indicatorService.getUserFavourIndicatorsWithPage(user, indicatorQuery));
            else
                return ResultUtils.ok().data("indicators", indicatorService.getUserFavourIndicators(user));
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("获取用户收藏指标失败");
        }
    }

    /**
     * @description: 获取用户创建的指标
     * @param {String} token 用户token
     * @return {ResultUtils}
     * @author: wch
     */
    @PostMapping("/creator")
    public ResultUtils getIndicatorsByCreator(@RequestHeader("Authorization") String token, @RequestBody IndicatorQuery indicatorQuery){
        if (indicatorQuery.getNeedPage() == null || (indicatorQuery.getNeedPage() == true && (indicatorQuery.getPage() == null || indicatorQuery.getPageSize() == null)))
            return ResultUtils.error().message("缺少分页参数");
        indicatorQuery.setKeyword(null);
        indicatorQuery.setDomainId(null);
        indicatorQuery.setSortMethod(null);
        User user = userService.findUserByToken(token);
        indicatorQuery.setCreatorId(user.getUserId());

        try{
            if (indicatorQuery.getNeedPage() == true)
                return ResultUtils.ok().data("indicators", indicatorService.getIndicatorsWithPage(user, indicatorQuery));
            else
                return ResultUtils.ok().data("indicators", indicatorService.getIndicators(user, indicatorQuery));
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("获取用户创建的指标失败");
        }
    }

    /**
     * @description: 获取所有指标创建者
     * @return {ResultUtils}
     * @author: wch
     */    
    @GetMapping("/creators")
    public ResultUtils getAllIndicatorCreators(){
        try{
            return ResultUtils.ok().data("creators", indicatorService.getIndicatorCreators());
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("获取所有指标创建者失败");
        }
    }

    /**
     * @description: 按条件获取指标列表
     * @param {String} token 用户token
     * @param {IndicatorQuery} indicatorQuery 指标查询条件
     * @return {ResultUtils}
     * @author: wch
     */    
    @PostMapping("/find")
    public ResultUtils findIndicators(@RequestHeader("Authorization") String token, @RequestBody IndicatorQuery indicatorQuery){
        if (indicatorQuery.getNeedPage() == null || (indicatorQuery.getNeedPage() == true && (indicatorQuery.getPage() == null || indicatorQuery.getPageSize() == null)))
            return ResultUtils.error().message("缺少分页参数");
        if (indicatorQuery.getKeyword() != null && indicatorQuery.getKeyword().length() == 0)
            indicatorQuery.setKeyword(null);
        if (indicatorQuery.getDomainId() != null && indicatorQuery.getDomainId() == 0)
            indicatorQuery.setDomainId(null);
        if (indicatorQuery.getIndicatorState() != null && indicatorQuery.getIndicatorState() == 0)
            indicatorQuery.setIndicatorState(null);
        if (indicatorQuery.getIndicatorType() != null && indicatorQuery.getIndicatorType() == 0)
            indicatorQuery.setIndicatorType(null);
        if (indicatorQuery.getCreatorId() != null && indicatorQuery.getCreatorId() == 0)
            indicatorQuery.setCreatorId(null);
        if (indicatorQuery.getSortMethod() != null && indicatorQuery.getSortMethod().length() == 0)
            indicatorQuery.setSortMethod(null);

        try {
            User user = userService.findUserByToken(token);
            if (indicatorQuery.getNeedPage() == false)
                return ResultUtils.ok().data("indicators", indicatorService.getIndicators(user, indicatorQuery));
            else
                return ResultUtils.ok().data("indicators", indicatorService.getIndicatorsWithPage(user, indicatorQuery));
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("查找指标失败");
        }
    }

    /**
     * @description: 获取指标血缘树信息
     * @param {String} token 用户token
     * @param {String} indicatorId 指标id
     * @return {ResultUtils}
     * @author: wch
     */    
    @GetMapping("/indicator-tree")
    public ResultUtils getIndicatorTree(@RequestHeader("Authorization") String token, @RequestParam String indicatorId){
        try{
            return ResultUtils.ok().data("records", indicatorService.getIndicatorTreeById(indicatorId));
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            return ResultUtils.error().message(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("获取指标血缘树失败");
        }
    }

    /**
     * @description: 添加指标
     * @param {String} token 用户token
     * @param {Indicator} indicator 新指标
     * @return {ResultUtils}
     * @author: wch
     */    
    @PostMapping("/add")
    public ResultUtils addIndicator(@RequestHeader("Authorization") String token, @RequestBody Indicator indicator){
        User user = userService.findUserByToken(token);

        try{
            indicatorService.verifyIndicator(indicator);
            if (indicatorService.saveIndicator(user, indicator))
                return ResultUtils.ok().message("新增指标成功");
            return ResultUtils.error().message("新增指标失败");
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            return ResultUtils.error().message(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("新增指标失败");
        }
    }

    /**
     * @description: 更新指标信息
     * @param {String} token 用户token
     * @param {Indicator} indicator
     * @return {ResultUtils}
     * @author: wch
     */    
    @PostMapping("/update")
    public ResultUtils updateIndicator(@RequestHeader("Authorization") String token, @RequestBody Indicator indicator){
        User user = userService.findUserByToken(token);
        Indicator existIndicator = indicatorService.getById(indicator.getIndicatorId());
        if (existIndicator == null)
            return ResultUtils.error().message("指标" + indicator.getIndicatorId() + "不存在");
        if (existIndicator.getIndicatorType().intValue() != indicator.getIndicatorType().intValue())
            return ResultUtils.error().message("不允许更改指标类型");

        try{
            indicatorService.verifyIndicator(indicator);
            indicatorService.updateIndicator(user, indicator);
            if (existIndicator.getIndicatorState() != indicator.getIndicatorState())
                indicatorService.updateIndicatorState(user, indicator.getIndicatorState(), indicator);
            return ResultUtils.ok().message("更新指标成功");
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            return ResultUtils.error().message(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("更新指标失败");
        }
    }

    /**
     * @description: 更新指标状态
     * @param {String} token 用户token
     * @param {int} newState 新状态
     * @param {String} indicatorId 指标id
     * @return {ResultUtils}
     * @auther: wch
     */
    @PostMapping("/update_state")
    public ResultUtils updateIndicatorState(@RequestHeader("Authorization") String token, @RequestParam int newState, @RequestParam String indicatorId){
        User user = userService.findUserByToken(token);
        Indicator indicator = indicatorService.getById(indicatorId);

        if (indicator == null)
            return ResultUtils.error().message("指标" + indicatorId + "不存在");
        else if (indicator.getIndicatorState() == newState)
            return ResultUtils.error().message("指标状态重复");

        try{
            indicatorService.updateIndicatorState(user, newState, indicator);
            return ResultUtils.ok().message("更新指标状态成功");
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("更改指标状态失败");
        }
    }

    /**
     * @description: 删除指标
     * @param {String} token 用户token
     * @param {String} indicatorId 指标id
     * @return {ResultUtils}
     * @auther: wch
     */
    @PostMapping("/delete")
    public ResultUtils deleteIndicator(@RequestHeader("Authorization") String token, @RequestParam String indicatorId){
        return ResultUtils.ok();
    }
}
