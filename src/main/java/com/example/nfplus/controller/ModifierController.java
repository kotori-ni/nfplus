/*
 * @Description: 修饰词控制类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 23:13:33
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:34:45
 */

package com.example.nfplus.controller;

import com.example.nfplus.entity.Modifier;
import com.example.nfplus.entity.User;
import com.example.nfplus.entity.WordsQuery;
import com.example.nfplus.service.ModifierService;
import com.example.nfplus.service.UserService;
import com.example.nfplus.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/modifier")
public class ModifierController {
    @Autowired
    private ModifierService modifierService;
    @Autowired
    private UserService userService;

    /**
     * @description: 获取所有修饰词(树形结构)
     * @param token       用户token
     * @param needAll     是否需要"全部"这个结点
     * @param allowParent 父修饰词是否允许被选中
     * @return {ResultUtils}
     * @author: wch
     */
    @GetMapping("/all")
    public ResultUtils getAllModifier(@RequestHeader("Authorization") String token, @RequestParam Boolean needAll,
            @RequestParam Boolean allowParent) {
        try {
            User user = userService.findUserByToken(token);
            return ResultUtils.ok().data("modifiers", modifierService.getAllModifier(user, needAll, allowParent));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error().message("获取所有修饰词失败");
        }
    }

    /**
     * @description: 按条件获取所有修饰词
     * @param token      用户token
     * @param wordsQuery 查询条件
     * @return {ResultUtils}
     * @author: wch
     */
    @PostMapping("/find")
    public ResultUtils getModifiers(@RequestHeader("Authorization") String token, @RequestBody WordsQuery wordsQuery) {
        if (wordsQuery.getKeyword() != null && wordsQuery.getKeyword().length() == 0)
            wordsQuery.setKeyword(null);
        if (wordsQuery.getNeedPage() == null || (wordsQuery.getNeedPage() == true
                && (wordsQuery.getPage() == null || wordsQuery.getPageSize() == null)))
            return ResultUtils.error().message("缺少分页参数");

        try {
            User user = userService.findUserByToken(token);
            if (wordsQuery.getNeedPage() == false)
                return ResultUtils.ok().data("modifiers", modifierService.getModifiers(user, wordsQuery));
            else
                return ResultUtils.ok().data("modifiers", modifierService.getModifiersWithPage(user, wordsQuery));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error().message("获取修饰词失败");
        }
    }

    /**
     * @description: 获取引用该修饰词的指标列表
     * @param modifierId 修饰词id
     * @return {ResultUtils}
     * @author: wch
     */
    @GetMapping("/indicators")
    public ResultUtils getQuoteIndicators(@RequestParam int modifierId) {
        if (modifierService.getById(modifierId) == null
                || modifierService.getById(modifierId).getParentModifierId() != null)
            return ResultUtils.error().message("修饰词id错误");
        try {
            return ResultUtils.ok().data("indicators", modifierService.findQuoteIndicators(modifierId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error().message("获取引用修饰词 " + modifierId + " 的指标失败");
        }
    }

    /**
     * @description: 添加修饰词
     * @param token    用户token
     * @param modifier 修饰词
     * @return {ResultUtils}
     * @author: wch
     */
    @PostMapping("/add")
    public ResultUtils addModifier(@RequestHeader("Authorization") String token, @RequestBody Modifier modifier) {
        User user = userService.findUserByToken(token);
        try {
            modifierService.addModifier(user, modifier);
            return ResultUtils.ok().message("添加修饰词成功");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResultUtils.error().message(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error().message("添加修饰词失败");
        }
    }

    /**
     * @description: 更新修饰词信息
     * @param token    用户token
     * @param modifier 修饰词
     * @return {ResultUtils}
     * @author: wch
     */
    @PostMapping("/update")
    public ResultUtils updateModifier(@RequestHeader("Authorization") String token, @RequestBody Modifier modifier) {
        User user = userService.findUserByToken(token);
        if (modifier.getModifierId() == null || modifierService.getById(modifier.getModifierId()) == null)
            return ResultUtils.error().message("缺少修饰词id或修饰词id不存在");
        if (modifier.getModifierName() == null || modifier.getModifierName().length() == 0)
            return ResultUtils.error().message("修饰词名称不能为空");
        try {
            modifierService.updateModifier(user, modifier);
            return ResultUtils.ok().message("修改修饰词信息成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error().message("更新修饰词信息失败");
        }
    }

    /**
     * @description: 删除修饰词(该接口未使用)
     * @param token      用户token
     * @param modifierId 修饰词id
     * @return {ResultUtils}
     */
    @DeleteMapping("delete")
    public ResultUtils deleteModifier(@RequestHeader("Authorization") String token, @RequestParam int modifierId) {
        Modifier modifier = modifierService.getById(modifierId);
        if (modifier == null)
            return ResultUtils.error().message("修饰词不存在或修饰词ID错误");
        try{
            modifierService.removeModifer(modifier);
            return ResultUtils.ok().message("删除修饰词成功");
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            return ResultUtils.error().message(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("删除修饰词失败");
        }
    }
}
