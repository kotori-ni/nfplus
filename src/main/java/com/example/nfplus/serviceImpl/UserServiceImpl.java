/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 16:58:54
 * @LastEditors: wch
 * @LastEditTime: 2023-08-16 10:15:05
 */
package com.example.nfplus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.nfplus.entity.Indicator;
import com.example.nfplus.entity.User;
import com.example.nfplus.mapper.*;
import com.example.nfplus.service.*;
import com.example.nfplus.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private IndicatorService indicatorService;
    @Autowired
    private DerivationService derivationService;
    @Autowired
    private ModifierService modifierService;
    @Autowired
    private TimeCycleService timeCycleService;

    /**
     * @description: 用户登录,生成用户token
     * @param user 待登录的用户
     * @return {User} 登录成功的用户信息
     * @author: wch
     */
    @Override
    public User userLogin(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        return getOne(queryWrapper);
    }

    /**
     * @description: 根据token查找用户
     * @param token 用户token
     * @return {User} 用户信息
     * @author: wch
     */
    @Override
    public User findUserByToken(String token) {
        String username = JwtUtils.getClaimsByToken(token).getSubject();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return getOne(queryWrapper);
    }

    /**
     * @description: 获取用户首页的信息(指标数量,衍生词数量,收藏指标数量...)
     * @return {List<Map<String, Object>>} 首页信息
     * @author: wch
     */
    @Override
    public List<Map<String, Object>> getIndexInfo() {
        List<Map<String, Object>> informations = new ArrayList<>();

        Map<String, Object> information = new HashMap<>();
        information.put("value", indicatorService.count());
        information.put("label", "指标总数");
        informations.add(information);

        information = new HashMap<>();
        information.put("value", indicatorService.query().eq("indicator_type", Indicator.TYPE_ATOMIC).count());
        information.put("label", "主原子指标");
        informations.add(information);

        information = new HashMap<>();
        information.put("value", indicatorService.query().eq("indicator_type", Indicator.TYPE_DERIVATION).count());
        information.put("label", "衍生原子指标");
        informations.add(information);

        information = new HashMap<>();
        information.put("value", indicatorService.query().eq("indicator_type", Indicator.TYPE_MODIFIER).count());
        information.put("label", "派生指标");
        informations.add(information);

        information = new HashMap<>();
        information.put("value", indicatorService.query().eq("indicator_type", Indicator.TYPE_COMPOSITE).count());
        information.put("label", "复合指标");
        informations.add(information);

        information = new HashMap<>();
        information.put("value", derivationService.count());
        information.put("label", "衍生词数量");
        informations.add(information);

        information = new HashMap<>();
        information.put("value", modifierService.query().isNull("modifier_key_id").count());
        information.put("label", "修饰词数量");
        informations.add(information);

        information = new HashMap<>();
        information.put("value", timeCycleService.count());
        information.put("label", "时间周期数量");
        informations.add(information);

        return informations;
    }

    /**
     * @description: 验证用户信息是否正确
     * @param user 待验证的用户对象
     * @return {Boolean} 验证结果,正确返回true,错误返回false
     * @author: wch
     */
    @Override
    public Boolean verifyUser(User user) {
        if (user.getUsername() == null || user.getUsername().length() == 0)
            throw new IllegalArgumentException("用户名不能为空");
        if (user.getPassword() == null || user.getPassword().length() < 6)
            throw new IllegalArgumentException("密码长度不能小于6");
        if (user.getEmail() != null && !isValidEmail(user.getEmail()))
            throw new IllegalArgumentException("邮箱格式非法");
        return true;
    }

    /**
     * @description: 验证邮箱是否合法
     * @param email 待验证的邮箱
     * @return {Boolean} 验证结果,正确返回true,错误返回false
     */
    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
