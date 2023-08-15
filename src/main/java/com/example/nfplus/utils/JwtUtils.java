/*
 * @Description: token构造工具类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:16:19
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 11:24:10
 */


package com.example.nfplus.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtils {
    //过期时间: 2天
    private final static long expire_time = 3600 * 24 * 2;

    //32位密钥
    private final static String secret_key = "nfplusdataindicatorsusersystemer";

    /**
     * @description: 以用户名为标识生成用户token
     * @param {String} username 用户名
     * @return {String} 用户token
     * @author: wch
     */
    public static String generateToken(String username){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 1000 * expire_time);
        return Jwts.builder()
                .setHeaderParam("type","JWT")
                .setSubject(String.valueOf(username))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256,secret_key)
                .compact();
    }

    /**
     * @description: 解析token信息
     * @param {String} 用户token
     * @return {Claims}
     * @author: wch
     */    
    public static Claims getClaimsByToken(String token){
        return Jwts.parser()
                .setSigningKey(secret_key)
                .parseClaimsJws(token)
                .getBody();
    }
}
