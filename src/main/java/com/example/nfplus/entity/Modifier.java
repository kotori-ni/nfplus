/*
 * @Description: 修饰词类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:34:41
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 16:56:03
 */


package com.example.nfplus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@TableName("modifiers")
public class Modifier{
    @TableField(exist = false)
    private int index;                      //表格中的序号

    @TableField(exist = false)
    private Boolean isCollect;              //false没有被收藏，true被收藏

    @TableId(type = IdType.AUTO)
    private Integer modifierId;             //修饰词id
    @TableField(value = "modifier_key_id")
    private Integer parentModifierId;       //修饰词键id

    private Integer creatorId;              //创建者id
    private String ModifierName;            //修饰词名称
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date createTime;                //创建时间
    private String description;             //修饰词描述

    @TableField(exist = false)
    private List<Modifier> modifierValues;  //修饰词值列表

    @TableField(exist = false)
    private String creatorName;             //创建者名称

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Modifier modifier = (Modifier) o;
        return Objects.equals(getModifierId(), modifier.getModifierId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getModifierId());
    }
}
