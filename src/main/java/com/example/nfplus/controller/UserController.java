/*
 * @Description: 用户控制类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 16:59:43
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 16:31:11
 */


package com.example.nfplus.controller;

import com.example.nfplus.entity.User;
import com.example.nfplus.service.IndicatorService;
import com.example.nfplus.service.UserService;
import com.example.nfplus.utils.JwtUtils;
import com.example.nfplus.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private IndicatorService indicatorService;

    /**
     * @description: 用户登录
     * @param {User} webuser
     * @return {ResultUtils}
     * @author: wch
     */    
    @PostMapping("/login")
    public ResultUtils login(@RequestBody User webuser){
        if (webuser.getUsername() == null || webuser.getPassword() == null)
            return ResultUtils.error().message("缺少用户名或密码");
        try{
            User user = userService.userLogin(webuser);
            if (user != null)
                return ResultUtils.ok().data("token", JwtUtils.generateToken(user.getUsername()));
            return ResultUtils.error().message("登录失败,请检查用户名与密码是否正确");
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("登录失败,请检查用户名与密码是否正确");
        }
    }

    /**
     * @description: 用户注册
     * @param {User} user
     * @return {ResultUtils}
     * @author: wch
     */    
    @PostMapping("/register")
    public ResultUtils register(@RequestBody User user){
        try{
            userService.verifyUser(user);
            User existUser = userService.query().eq("username", user.getUsername()).one();
            if (existUser != null)
                return ResultUtils.error().message("用户 " + user.getUsername() + " 已存在");
            user.setUserId(null);
            userService.save(user);
            return ResultUtils.ok().data("token", JwtUtils.generateToken(user.getUsername()));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResultUtils.error().message(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error().message("注册失败");
        }
    }

    /**
     * @description: 根据token获取用户信息
     * @param {String} token 用户token
     * @return {ResultUtils}
     * @author: wch
     */
    @GetMapping("/user-info")
    public ResultUtils getUserInfo(@RequestParam String token){
        try {
            System.out.println(token);
            User user = userService.findUserByToken(token);
            return ResultUtils.ok().data("user", user);
        } catch (IllegalArgumentException e){
            return ResultUtils.error().message(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("获取用户信息错误");
        }
    }

    /**
     * @description: 修改用户信息
     * @param {String} token 用户token
     * @param {User} user 用户信息
     * @return {ResultUtils}
     * @author: wch
     */    
    @PostMapping("/update")
    public ResultUtils updateUser(@RequestHeader("Authorization") String token, @RequestBody User user){
        User existUser = userService.findUserByToken(token);
        if (user.getUserId() == null)
            return ResultUtils.error().message("缺少用户id");
        if (existUser.getUserId() != user.getUserId())
            return ResultUtils.error().message("没有权限修改此用户信息");
        existUser = userService.query().eq("username", user.getUsername()).one();
        if (!existUser.equals(user))
            return ResultUtils.error().message("用户名已存在,请输入其他用户名");

        try {
            userService.verifyUser(user);
            userService.updateById(user);
            return ResultUtils.ok().message("修改用户信息成功");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResultUtils.error().message(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error().message("修改用户信息失败");
        }
    }

    /**
     * @description: 获取用户首页信息，包括指标数量，衍生词数量等
     * @return {ResultUtils}
     * @author: wch
     */    
    @GetMapping("/index-info")
    public ResultUtils getIndexInfo(){
        try{
            return ResultUtils.ok().data("informations", userService.getIndexInfo());
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("获取首页信息失败");
        }
    }

    /**
     * @description: 用户登出
     * @param {String} token 用户token
     * @return {ResultUtils}
     * @author: wch
     */    
    @PostMapping("/logout")
    public ResultUtils logout(@RequestParam String token){
        try {
            User user = userService.findUserByToken(token);
            return ResultUtils.ok();
        } catch(IllegalArgumentException e){
            return ResultUtils.error().message(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error().message("获取用户信息错误");
        }
    }
}
