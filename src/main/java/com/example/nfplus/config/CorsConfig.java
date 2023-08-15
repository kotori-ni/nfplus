/*
 * @Description: 跨域配置类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:14:25
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 15:48:12
 */

package com.example.nfplus.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
        corsRegistry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET","PUT","POST","DELETE","OPTIONS")
                .allowedHeaders("*")
                .maxAge(150000);
    }
}
