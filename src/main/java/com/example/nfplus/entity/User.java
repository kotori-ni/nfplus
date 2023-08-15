/*
 * @Description: 用户类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:34:44
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 16:57:42
 */


package com.example.nfplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Objects;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer userId;     //用户id
    private String username;    //用户名
    private String password;    //密码
    private String email;       //邮箱

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getUserId(), user.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }
}
