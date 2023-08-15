/*
 * @Description: 衍生词类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:34:38
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 16:47:37
 */

package com.example.nfplus.entity;

import java.util.Date;
import java.util.Objects;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("derivations")
public class Derivation{
    @TableField(exist = false)
    private int index;                  //表格中的序号

    @TableField(exist = false)
    private Boolean isCollect;          //false没有被收藏，true被收藏

    @TableId(type = IdType.AUTO)
    private Integer derivationId;       //衍生词id
    private Integer creatorId;          //创建者id
    private String derivationName;      //衍生词名称
    private String calculationCaliber;  //计算口径
    private String description;         //描述
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date createTime;            //创建时间

    @TableField(exist = false)
    private String CreatorName;         //创建者名称

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Derivation that = (Derivation) o;
        return getDerivationId().equals(that.getDerivationId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDerivationId());
    }
}
