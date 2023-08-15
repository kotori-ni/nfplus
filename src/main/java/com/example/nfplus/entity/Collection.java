/*
 * @Description: 用户收藏类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:34:36
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 16:45:41
 */


package com.example.nfplus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
@TableName("user_collections")
public class Collection {
    @TableId(type = IdType.AUTO)
    private Integer collectionId; //收藏id
    private Integer userId;       //用户id
    private String indicatorId;   //指标id
    private Integer derivationId; //衍生词id
    private Integer modifierId;   //修饰词id
    private Integer timeCycleId;  //时间周期id
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date markTime;        //收藏时间

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collection that = (Collection) o;
        return getCollectionId().equals(that.getCollectionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCollectionId());
    }
}
