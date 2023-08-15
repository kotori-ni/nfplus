package com.example.nfplus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.nfplus.entity.User;
import com.example.nfplus.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserService userService;

    @Test
    public void getUserTest(){
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", "wc");

        User user = userService.getOne(queryWrapper);
        System.out.println(user);
    }
}