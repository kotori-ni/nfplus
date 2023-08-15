/*
 * @Description: token拦截器类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:15:39
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 17:01:58
 */


package com.example.nfplus.interceptor;

import com.example.nfplus.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenInterceptor implements HandlerInterceptor {
    /**
     * @description: token过期或token错误会被拦截
     * @return {*}
     * @author: wch
     */    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        String token = request.getHeader("Authorization");
        //System.out.println("usertoken: " + token);

        response.setContentType("application/json; charset=UTF-8");
        Map<String, Object> map = new HashMap<>();

        try{
            Claims claim = JwtUtils.getClaimsByToken(token);
            Date expiration = claim.getExpiration();
            if (expiration.before(new Date())) {
                map.put("success",false);
                map.put("code",50001);
                map.put("message","token已过期");
                String json = new ObjectMapper().writeValueAsString(map);
                response.getWriter().println(json);
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
            map.put("success",false);
            map.put("code",50001);
            map.put("message","token异常");
            String json = new ObjectMapper().writeValueAsString(map);
            response.getWriter().println(json);
        }

        return true;
    }
}
