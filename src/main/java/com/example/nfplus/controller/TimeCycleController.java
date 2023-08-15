/*
 * @Description: 时间周期控制类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-30 20:32:33
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 16:23:25
 */


package com.example.nfplus.controller;

import com.example.nfplus.entity.TimeCycle;
import com.example.nfplus.entity.User;
import com.example.nfplus.entity.WordsQuery;
import com.example.nfplus.mapper.TimeCycleMapper;
import com.example.nfplus.service.TimeCycleService;
import com.example.nfplus.service.UserService;
import com.example.nfplus.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/timecycle")
public class TimeCycleController {
    @Autowired
    private TimeCycleService timeCycleService;
    @Autowired
    private TimeCycleMapper timeCycleMapper;
    @Autowired
    private UserService userService;

    /**
     * @description: 获取所有时间周期
     * @return {ResultUtils}
     * @author: wch
     */    
    @GetMapping("/all")
    public ResultUtils getAllTimeCycle(){
        try{
            return ResultUtils.ok().data("timeCycles", timeCycleService.list());
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("获取时间周期失败");
        }
    }

    /**
     * @description: 按条件获取所有时间周期
     * @param {String} token 用户token
     * @param {WordQuery} wordsQuery 查询条件
     * @return {ResultUtils}
     * @author: wch
     */    
    @PostMapping("/find")
    public ResultUtils getTimeCycles(@RequestHeader("Authorization") String token, @RequestBody WordsQuery wordsQuery){
        if (wordsQuery.getKeyword() != null && wordsQuery.getKeyword().length() == 0)
            wordsQuery.setKeyword(null);
        if (wordsQuery.getNeedPage() == null ||(wordsQuery.getNeedPage() == true && (wordsQuery.getPage() == null || wordsQuery.getPageSize() == null)))
            return ResultUtils.error().message("缺少分页参数");

        try {
            User user = userService.findUserByToken(token);
            if (wordsQuery.getNeedPage() == false)
                return ResultUtils.ok().data("timeCycles", timeCycleService.getTimeCycles(user, wordsQuery));
            else
                return ResultUtils.ok().data("timeCycles", timeCycleService.getTimeCyclesWithPage(user, wordsQuery));
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("获取时间周期失败");
        }
    }

    /**
     * @description: 获取引用该时间周期的指标列表
     * @param {String} token 用户token
     * @param {int} timeCycleId 时间周期id
     * @return {ResultUtils}
     * @author: wch
     */    
    @GetMapping("/indicators")
    public ResultUtils getQuoteIndicators(@RequestHeader("Authorization") String token, @RequestParam int timeCycleId){
        if (timeCycleService.getById(timeCycleId) == null)
            return ResultUtils.error().message("时间周期 " + timeCycleId + " 不存在");
        try{
            return ResultUtils.ok().data("indicators", timeCycleService.findQuoteIndicators(timeCycleId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error().message("查询引用时间周期 " + timeCycleId + " 的指标失败");
        }
    }

    /**
     * @description: 新增时间周期
     * @param {String} token 用户token
     * @param {TimeCycle} timeCycle 时间周期
     * @return {ResultUtils}
     * @author: wch
     */    
    @PostMapping("/add")
    public ResultUtils addTimeCycle(@RequestHeader("Authorization") String token, @RequestBody TimeCycle timeCycle){
        User user = userService.findUserByToken(token);
        timeCycle.setTimeCycleId(null);
        timeCycle.setCreatorId(user.getUserId());
        if (timeCycle.getTimeCycleName() == null)
            return ResultUtils.error().message("缺少时间周期名称");
        TimeCycle existTimeCycle = timeCycleService.query().eq("time_cycle_name", timeCycle.getTimeCycleName()).one();
        if (existTimeCycle != null)
            return ResultUtils.error().message("时间周期名称重复");
        if (timeCycleService.save(timeCycle))
            return ResultUtils.ok().message("添加时间周期成功");
        return ResultUtils.error().message("添加时间周期失败");
    }

    /**
     * @description: 批量添加事件周期
     * @param {String} token 用户token
     * @param {List<TimeCycle>} timeCycles 时间周期列表
     * @return {ResultUtils}
     * @author: wch
     */    
    @PostMapping("/batch_add")
    public ResultUtils batchAddDerivation(@RequestHeader("Authorization") String token, @RequestBody List<TimeCycle> timeCycles){
        User user = userService.findUserByToken(token);
        List<String> newNames = new ArrayList<>();
        List<String> existNames = timeCycleMapper.getTimeCycleNames();

        for (int i = 0; i < timeCycles.size(); i++){
            if (timeCycles.get(i).getTimeCycleName() == null || timeCycles.get(i).getTimeCycleName().length() == 0)
                return ResultUtils.error().message("第" + (i + 1) + "行的时间周期缺少时间周期名称");
            if (timeCycles.get(i).getDescription() != null && timeCycles.get(i).getDescription().length() > 255)
                return ResultUtils.error().message("第" + (i + 1) + "行的时间周期描述过长");
            if (existNames.contains(timeCycles.get(i).getTimeCycleName()))
                return ResultUtils.error().message("时间周期" + timeCycles.get(i).getTimeCycleName() + "已存在");
            timeCycles.get(i).setTimeCycleId(null);
            timeCycles.get(i).setCreatorId(user.getUserId());
        }

        for (TimeCycle timeCycle : timeCycles) {
            newNames.add(timeCycle.getTimeCycleName());
        }
        Set<String> set = new HashSet<>(newNames);
        if (set.size() < newNames.size())
            return ResultUtils.error().message("存在名称重复的时间周期");

        try{
            timeCycleService.saveBatch(timeCycles);
            return ResultUtils.ok().message("上传时间周期成功");
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("上传时间周期失败");
        }
    }

    /**
     * @description: 更新时间周期信息
     * @param {String} token 用户token
     * @param {TimeCycle} timeCycle 时间周期
     * @return {ResultUtils}
     * @author: wch
     */    
    @PostMapping("/update")
    public ResultUtils updateDerivation(@RequestHeader("Authorization") String token, @RequestBody TimeCycle timeCycle){
        if (timeCycle.getTimeCycleId() == null || timeCycle.getTimeCycleName() == null)
            return ResultUtils.error().message("缺少时间周期参数");
        TimeCycle existTimeCycle = timeCycleService.getById(timeCycle.getTimeCycleId());
        if (existTimeCycle == null)
            return ResultUtils.error().message("时间周期" + timeCycle.getTimeCycleId() + "不存在");
        existTimeCycle.setTimeCycleName(timeCycle.getTimeCycleName());
        existTimeCycle.setDescription(timeCycle.getDescription());
        try{
            timeCycleService.updateById(existTimeCycle);
            return ResultUtils.ok().message("修改时间周期成功");
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("修改时间周期失败");
        }
    }

    /**
     * @description: 删除时间周期(该接口未使用)
     * @param {String} token 用户token
     * @param {int} timeCycleId 时间周期id
     * @return {ResultUtils}
     * @author: wch
     */    
    @PostMapping
    public ResultUtils deleteTimeCycle(@RequestHeader("Authorization") String token, @RequestParam int timeCycleId){
        User user = userService.findUserByToken(token);
        TimeCycle timeCycle = timeCycleService.getById(timeCycleId);
        if (timeCycle == null)
            return ResultUtils.error().message("时间周期不存在");
        if (timeCycle.getCreatorId().intValue() != user.getUserId().intValue())
            return ResultUtils.error().message("没有权限删除该时间周期");
        if (timeCycleService.removeById(timeCycleId))
            return ResultUtils.ok().message("删除时间周期成功");
        return ResultUtils.error().message("删除时间周期失败");
    }
}
