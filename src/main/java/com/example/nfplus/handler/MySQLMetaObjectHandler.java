/*
 * @Description: MySQL自动数据填充配置类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 22:56:44
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 17:01:24
 */


package com.example.nfplus.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MySQLMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("operateTime", new Date(), metaObject);
        this.setFieldValByName("markTime", new Date(), metaObject);
        this.setFieldValByName("lastOperateTime", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("operateTime", new Date(), metaObject);
        this.setFieldValByName("markTime", new Date(), metaObject);
    }
}
