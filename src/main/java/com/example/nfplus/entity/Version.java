/*
 * @Description: 指标版本类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:34:47
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 16:59:09
 */


package com.example.nfplus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;
import java.util.Objects;

@Data
@TableName("versions")
public class Version {
    @TableId(type = IdType.AUTO)
    private Integer versionId;  //版本id

    private String indicatorId; //指标id
    private Integer operatorId; //操作者id
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date operateTime;   //操作时间
    private String versionNum;  //版本号
    private String operation;   //操作

    @TableField(exist = false)
    private String operatorName;

    public Version(){
        versionNum = "1.0.0";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return Objects.equals(getVersionId(), version.getVersionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVersionId());
    }

    /**
     * @description: 更新版本号
     * @return {*}
     * @author: wch
     */    
    public void updateVersion(){
        String[] str_nums = versionNum.split("\\.");
        Integer[] int_nums = new Integer[3];
        for (int i = 0; i < 3; i++)
            int_nums[i] = Integer.valueOf(str_nums[i]);
        int_nums[2] += 1;
        
        for (int i = 2; i >= 0; i--) {
            if (int_nums[i] > 9) {
                int_nums[i] = 0;
                if (i != 0)
                    int_nums[i - 1] += 1;
            }
        }

        for (int i = 0; i <= 2; i++)
            str_nums[i] = int_nums[i].toString();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str_nums.length; i++) {
            sb.append(str_nums[i]);
            if (i != str_nums.length - 1)
                sb.append(".");
        }
        versionNum = sb.toString();
    }
}
