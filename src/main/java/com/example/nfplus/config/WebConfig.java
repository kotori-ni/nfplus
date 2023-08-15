/*
 * @Description: Web配置类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:14:53
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 15:49:19
 */


package com.example.nfplus.config;

import com.example.nfplus.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/register")
                .excludePathPatterns("/user/add");
    }
}
