/*
 * @Description: 时间周期类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:34:43
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 16:56:36
 */

package com.example.nfplus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;
import java.util.Objects;

@Data
@TableName("time_cycles")
public class TimeCycle {
    @TableField(exist = false)
    private int index;              //表格中的序号

    @TableField(exist = false)
    private Boolean isCollect;      //false没有被收藏，true被收藏

    @TableId(type = IdType.AUTO)
    private Integer timeCycleId;    //时间周期id
    private Integer creatorId;      //创建者id
    private String timeCycleName;   //时间周期名称
    private String description;     //描述

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date createTime;        //创建时间

    @TableField(exist = false)
    private String creatorName;     //创建者名称

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeCycle timeCycle = (TimeCycle) o;
        return Objects.equals(getTimeCycleId(), timeCycle.getTimeCycleId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTimeCycleId());
    }
}
