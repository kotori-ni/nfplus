/*
 * @Description: 指标版本控制类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-08-02 23:16:40
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:36:30
 */

package com.example.nfplus.controller;

import com.example.nfplus.entity.User;
import com.example.nfplus.service.UserService;
import com.example.nfplus.service.VersionService;
import com.example.nfplus.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/version")
public class VersionController {
    @Autowired
    private VersionService versionService;
    @Autowired
    private UserService userService;

    /**
     * @description: 获取指标的所有版本
     * @param token       用户token
     * @param indicatorId 指标id
     * @return {ResultUtils}
     * @author: wch
     */
    @GetMapping("/all")
    public ResultUtils getIndicatorVersion(@RequestHeader("Authorization") String token,
            @RequestParam String indicatorId) {
        User user = userService.findUserByToken(token);
        try {
            return ResultUtils.ok().data("versions", versionService.getIndicatorVersions(user, indicatorId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error().message("获取指标版本失败");
        }
    }

}
