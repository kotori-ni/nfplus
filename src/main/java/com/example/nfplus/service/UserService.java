/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 16:54:35
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:45:26
 */
package com.example.nfplus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.nfplus.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface UserService extends IService<User> {
    /**
     * @description: 用户登录,生成用户token
     * @param user 登录用户
     * @return {User} 登录成功的用户信息
     * @author: wch
     */    
    public User userLogin(User user);

    /**
     * @description: 根据token查找用户
     * @param token
     * @return {User} 用户信息
     * @author: wch
     */    
    public User findUserByToken(String token);

    /**
     * @description: 获取用户首页的信息(指标数量,衍生词数量,收藏指标数量...)
     * @return {List<Map<String, Object>>} 首页信息
     * @author: wch
     */    
    public List<Map<String, Object>> getIndexInfo();

    /**
     * @description: 验证用户信息是否正确
     * @param user 待验证的用户对象
     * @return {Boolean} 验证结果,正确返回true,错误返回false
     * @author: wch
     */    
    public Boolean verifyUser(User user);
}
