/*
 * @Description: 衍生词控制类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-30 00:36:12
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:32:31
 */

package com.example.nfplus.controller;

import com.example.nfplus.entity.Derivation;
import com.example.nfplus.entity.User;
import com.example.nfplus.entity.WordsQuery;
import com.example.nfplus.mapper.DerivationMapper;
import com.example.nfplus.service.DerivationService;
import com.example.nfplus.service.UserService;
import com.example.nfplus.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/derivation")
public class DerivationController {
    @Autowired
    private DerivationService derivationService;
    @Autowired
    private DerivationMapper derivationMapper;
    @Autowired
    private UserService userService;

    /**
     * @description: 获取所有衍生词
     * @return {ResultUtils}
     * @author: wch
     */
    @GetMapping("/all")
    public ResultUtils getAllDerivation() {
        try {
            return ResultUtils.ok().data("derivations", derivationService.list());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error().message("获取所有衍生词失败");
        }
    }

    /**
     * @description: 按条件衍生词
     * @param token      用户token
     * @param wordsQuery 搜索条件
     * @return {ResultUtils}
     * @author: wch
     */
    @PostMapping("/find")
    public ResultUtils getDerivations(@RequestHeader("Authorization") String token,
            @RequestBody WordsQuery wordsQuery) {
        if (wordsQuery.getKeyword() != null && wordsQuery.getKeyword().length() == 0)
            wordsQuery.setKeyword(null);
        if (wordsQuery.getNeedPage() == null || (wordsQuery.getNeedPage() == true
                && (wordsQuery.getPage() == null || wordsQuery.getPageSize() == null)))
            return ResultUtils.error().message("缺少分页参数");

        try {
            User user = userService.findUserByToken(token);
            if (wordsQuery.getNeedPage() == false)
                return ResultUtils.ok().data("derivations", derivationService.getDerivations(user, wordsQuery));
            else
                return ResultUtils.ok().data("derivations", derivationService.getDerivationsWithPage(user, wordsQuery));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error().message("获取衍生词失败");
        }
    }

    /**
     * @description: 获取引用该衍生词的指标列表
     * @param derivationId 衍生词id
     * @return {ResultUtils}
     * @author: wch
     */
    @GetMapping("/indicators")
    public ResultUtils getQuoteIndicators(@RequestParam int derivationId) {
        if (derivationService.getById(derivationId) == null)
            return ResultUtils.error().message("衍生词 " + derivationId + " 不存在");
        try {
            return ResultUtils.ok().data("indicators", derivationService.findQuoteIndicators(derivationId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error().message("获取引用衍生词 " + derivationId + "的指标失败");
        }
    }

    /**
     * @description: 添加衍生词
     * @param token      用户token
     * @param derivation 待添加的衍生词
     * @return {ResultUtils}
     * @author: wch
     */
    @PostMapping("/add")
    public ResultUtils addDerivation(@RequestHeader("Authorization") String token, @RequestBody Derivation derivation) {
        User user = userService.findUserByToken(token);
        derivation.setDerivationId(null);
        derivation.setCreatorId(user.getUserId());
        if (derivation.getDerivationName() == null || derivation.getCalculationCaliber() == null)
            return ResultUtils.error().message("缺少衍生词名称或计算口径");
        else if (derivation.getDescription() != null && derivation.getDescription().length() > 255)
            return ResultUtils.error().message("衍生词描述长度不能大于255");
        if (derivationService.query().eq("derivation_name", derivation.getDerivationName()).count() > 0)
            return ResultUtils.error().message("衍生词名称重复");
        if (derivationService.save(derivation))
            return ResultUtils.ok().message("添加衍生词成功");
        return ResultUtils.error().message("添加衍生词失败");
    }

    /**
     * @description: 批量添加衍生词
     * @param token       用户token
     * @param derivations 衍生词列表
     * @return {ResultUtils}
     * @author: wch
     */
    @PostMapping("/batch_add")
    public ResultUtils batchAddDerivation(@RequestHeader("Authorization") String token,
            @RequestBody List<Derivation> derivations) {
        User user = userService.findUserByToken(token);
        List<String> newNames = new ArrayList<>();
        List<String> existNames = derivationMapper.getDerivationNames();

        for (int i = 0; i < derivations.size(); i++) {
            if (derivations.get(i).getDerivationName() == null || derivations.get(i).getDerivationName().length() == 0)
                return ResultUtils.error().message("第" + (i + 1) + "行的衍生词缺少衍生词名称");
            if (derivations.get(i).getCalculationCaliber() == null
                    || derivations.get(i).getCalculationCaliber().length() == 0)
                return ResultUtils.error().message("第" + (i + 1) + "行的衍生词缺少计算口径");
            if (derivations.get(i).getDescription() != null && derivations.get(i).getDescription().length() > 255)
                return ResultUtils.error().message("第" + (i + 1) + "行的衍生词描述过长");
            if (existNames.contains(derivations.get(i).getDerivationName()))
                return ResultUtils.error().message("衍生词" + derivations.get(i).getDerivationName() + "已存在");
            derivations.get(i).setDerivationId(null);
            derivations.get(i).setCreatorId(user.getUserId());
        }

        for (Derivation derivation : derivations) {
            newNames.add(derivation.getDerivationName());
        }
        Set<String> set = new HashSet<>(newNames);
        if (set.size() < newNames.size())
            return ResultUtils.error().message("存在名称重复的衍生词");

        try {
            derivationService.saveBatch(derivations);
            return ResultUtils.ok().message("上传衍生词成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error().message("上传衍生词失败");
        }
    }

    /**
     * @description: 更新衍生词信息
     * @param token      用户token
     * @param derivation 衍生词
     * @return {ResultUtils}
     */
    @PostMapping("/update")
    public ResultUtils updateDerivation(@RequestHeader("Authorization") String token,
            @RequestBody Derivation derivation) {
        if (derivation.getDerivationId() == null || derivation.getDerivationName() == null
                || derivation.getCalculationCaliber() == null)
            return ResultUtils.error().message("缺少衍生词参数");
        Derivation existDerivation = derivationService.getById(derivation.getDerivationId());
        if (existDerivation == null)
            return ResultUtils.error().message("衍生词" + derivation.getDerivationId() + "不存在");
        existDerivation.setDerivationName(derivation.getDerivationName());
        existDerivation.setCalculationCaliber(derivation.getCalculationCaliber());
        existDerivation.setDescription(derivation.getDescription());
        try {
            derivationService.updateById(existDerivation);
            return ResultUtils.ok().message("修改衍生词成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error().message("修改衍生词失败");
        }
    }

    /**
     * @description: 删除衍生词(该接口没有被使用)
     * @param token        用户token
     * @param derivationId 衍生词id
     * @return {ResultUtils}
     */
    @DeleteMapping("delete")
    public ResultUtils deleteDerivation(@RequestHeader("Authorization") String token, @RequestParam int derivationId) {
        Derivation derivation = derivationService.getById(derivationId);
        if (derivation == null)
            return ResultUtils.error().message("衍生词不存在");
        int quoteNum = derivationMapper.selectQuoteIndicatorNum(derivationId);
        if (quoteNum > 0)
            return ResultUtils.error().message("有 " + quoteNum + " 个指标引用了该衍生词,不可删除");
        try{
            derivationService.removeById(derivationId);
            return ResultUtils.ok().message("删除衍生词成功");
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("删除衍生词失败");
        }
    }
}
