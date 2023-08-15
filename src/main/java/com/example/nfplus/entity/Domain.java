/*
 * @Description: 指标域类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:34:39
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 16:47:46
 */


package com.example.nfplus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;
import java.util.Objects;

@Data
@TableName("domains")
public class Domain {
    @TableId(type = IdType.AUTO)
    private Integer domainId;       //指标域id

    private Integer parentDomainId; //父指标域id
    private Integer creatorId;      //创建者id
    @TableField(exist = false)
    private String creatorName;     //创建者名称
    private String domainName;      //指标域名称
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date createTime;        //创建时间
    @TableField(exist = false)
    private Integer indicatorNum;   //指标数量
    @TableField(exist = false)
    private Integer childNum;       //子指标域数量

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Domain domain = (Domain) o;
        return getDomainId().equals(domain.getDomainId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDomainId());
    }
}
