/*
 * @Description: 指标类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:34:17
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 16:52:48
 */


package com.example.nfplus.entity;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.nfplus.utils.IndicatorUtils;
import lombok.Data;

@Data
@TableName("indicators")
public class Indicator implements Comparable<Indicator>{
    @TableField(exist = false)
    private int index;                          //表格中的序号

    @TableField(exist = false)
    private Boolean isCollect;                  //false没有被收藏，true被收藏

    @TableId
    private String indicatorId;                 //指标id
    private String indicatorName;               //指标名称
    private Integer domainId;                   //指标域id
    private Integer creatorId;                  //创建者id
    private Integer lastOperatorId;             //最后操作者id
    private Integer timeCycleId;                //时间周期id
    private Integer indicatorType;              //指标类型
    private Integer securityLevel;              //安全等级
    private Integer indicatorState;             //指标状态
    private String dependentIndicatorId;        //依赖的主原子指标
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date createTime;                    //创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date lastOperateTime;               //最后更新时间
    private String analyzableDimensions;        //可分析维度
    private String affiliatedReportLinks;       //关联报表连接
    private String businessCaliberLeader;       //业务口径负责人
    private String businessCaliber;             //业务口径
    private String technicalCaliber;            //技术口径
    private String realtimeTechnicalCaliber;    //实时技术口径
    private String technicalCaliberLeader;      //技术口径负责人
    private String competentAuthoritie;         //主管部门
    private String calculateRule;               //运算规则
    private Integer viewsNum;                   //查看次数
    @TableField(exist = false)
    private Integer favoursNum;                 //收藏次数

    @TableField(exist = false)
    private Integer quoteNum;                   //被引用的次数

    @TableField(exist = false)
    private String indicatorTypeName;           //指标类型名称

    @TableField(exist = false)
    private String indicatorStateName;          //指标状态名称

    @TableField(exist = false)
    private String creatorName;                 //创建者名称

    @TableField(exist = false)
    private String lastOperatorName;            //最后操作者名称

    @TableField(exist = false)
    private String DomainName;                  //指标域名称

    @TableField(exist = false)
    private String dependentIndicatorName;      //依赖的主原子指标名称

    @TableField(exist = false)
    private String timeCycleName;               //时间周期名称

    @TableField(exist = false)
    private Version version;                    //版本

    @TableField(exist = false)
    private List<Integer> derivations;          //衍生词列表

    @TableField(exist = false)
    private List<String> derivationNames;       //衍生词名称列表

    @TableField(exist = false)
    private List<List<Integer>> modifiers;      //修饰词列表

    @TableField(exist = false)
    private List<String> modifierNames;         //修饰词名称列表

    @TableField(exist = false)
    private List<String> compositeds;           //复合指标运算指标列表

    @TableField(exist = false)
    private List<String> compositedNames;       //复合指标运算指标名称列表

    public final static int MIN_SECURITY_LEVEL = 1;  //最小安全等级
    public final static int MAX_SECURITY_LEVEL = 10; //最大安全等级
    public final static int IDSIZE = 128;
    public final static int TEXTSIZE = 255;
    public final static int LINKSIZE = 1024;

    public final static int TYPE_ATOMIC = 1;         //主原子指标
    public final static int TYPE_DERIVATION = 2;     //衍生原子指标
    public final static int TYPE_MODIFIER = 3;       //派生指标
    public final static int TYPE_COMPOSITE = 4;      //复合指标
    public final static int STATE_NEW = 1;           //新建状态
    public final static int STATE_DRAFT = 2;         //草稿状态
    public final static int STATE_ONLINE = 3;        //已发布状态
    public final static int STATE_OFFLINE = 4;       //已下线状态

    public final static int MAX_DERIVATIONS = 5;     //最大衍生词数
    public final static int MAX_MODIFIERS = 5;       //最大修饰词数

    /**
     * @description: 获取指标类型名称和指标状态名称
     * @return {*}
     * @author: wch
     */    
    public void getInfo(){
        indicatorTypeName = IndicatorUtils.getTypeName(indicatorType);
        indicatorStateName = IndicatorUtils.getStateName(indicatorState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Indicator indicator = (Indicator) o;
        return Objects.equals(getIndicatorId(), indicator.getIndicatorId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIndicatorId());
    }

    @Override
    public int compareTo(Indicator other){
        return this.getIndicatorId().compareTo(other.getIndicatorId());
    }
}
