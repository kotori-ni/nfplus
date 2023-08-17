/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 21:01:02
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:51:01
 */
package com.example.nfplus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.nfplus.entity.Indicator;
import com.example.nfplus.entity.User;
import com.example.nfplus.entity.Version;
import com.example.nfplus.mapper.DomainMapper;
import com.example.nfplus.mapper.IndicatorMapper;
import com.example.nfplus.mapper.VersionMapper;
import com.example.nfplus.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VersionServiceImpl extends ServiceImpl<VersionMapper, Version> implements VersionService {
    @Autowired
    private VersionMapper versionMapper;
    @Autowired
    private DerivationService derivationService;
    @Autowired
    private ModifierService modifierService;
    @Autowired
    private TimeCycleService timeCycleService;
    @Autowired
    private IndicatorMapper indicatorMapper;
    @Autowired
    private DomainMapper domainMapper;

    /**
     * @description: 查找指标的所有版本
     * @param user        请求查询的用户
     * @param indicatorId 指标id
     * @return {List<Version>} 指标版本列表,按时间倒序排列
     * @author: wch
     */
    @Override
    public List<Version> getIndicatorVersions(User user, String indicatorId) {
        QueryWrapper<Version> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("indicator_id", indicatorId);
        queryWrapper.orderByDesc("operate_time");
        return versionMapper.selectIndicatorVersions(queryWrapper);
    }

    /**
     * @description: 根据指标修改前后的信息,生成新指标版本
     * @param oldIndicator 修改前的指标
     * @param newIndicator 修改后的指标
     * @return {Version} 新指标版本
     * @author: wch
     */
    public Version getVersionInfo(Indicator oldIndicator, Indicator newIndicator) {
        Version version = versionMapper.findLastVersionByIndicatorId(newIndicator.getIndicatorId());
        version.setOperatorId(newIndicator.getLastOperatorId());

        String operation = new String();
        if (!newIndicator.getIndicatorName().equals(oldIndicator.getIndicatorName()))
            operation += "指标名称由 " + oldIndicator.getIndicatorName() + " 改为 " + newIndicator.getIndicatorName() + " \n";

        if (!newIndicator.getDomainId().equals(oldIndicator.getDomainId()))
            operation += "指标域由 " + domainMapper.selectById(oldIndicator.getDomainId()).getDomainName() + " 改为 "
                    + domainMapper.selectById(newIndicator.getDomainId()).getDomainName() + " \n";

        if (newIndicator.getSecurityLevel().intValue() != oldIndicator.getSecurityLevel().intValue())
            operation += "安全级别由 " + oldIndicator.getSecurityLevel() + " 改为 " + newIndicator.getSecurityLevel() + " \n";

        if (!newIndicator.getBusinessCaliber().equals(oldIndicator.getBusinessCaliber()))
            operation += "业务口径由 " + oldIndicator.getBusinessCaliber() + " 改为 " + newIndicator.getBusinessCaliber()
                    + " \n";

        if (!newIndicator.getBusinessCaliberLeader().equals(oldIndicator.getBusinessCaliberLeader()))
            operation += "业务口径负责人由 " + oldIndicator.getBusinessCaliberLeader() + " 改为 "
                    + newIndicator.getBusinessCaliberLeader() + " \n";

        if ((newIndicator.getAffiliatedReportLinks() != null && oldIndicator.getAffiliatedReportLinks() != null)
                && !newIndicator.getAffiliatedReportLinks().equals(oldIndicator.getAffiliatedReportLinks()))
            operation += "关联报表链接由 " + oldIndicator.getAffiliatedReportLinks() + " 改为 "
                    + newIndicator.getAffiliatedReportLinks() + " \n";
        else if (oldIndicator.getAffiliatedReportLinks() == null && newIndicator.getAffiliatedReportLinks() != null)
            operation += "关联报表链接新增为 " + newIndicator.getAffiliatedReportLinks() + " \n";
        else if (oldIndicator.getAffiliatedReportLinks() != null && newIndicator.getAffiliatedReportLinks() == null)
            operation += "关联报表链接由 " + oldIndicator.getAffiliatedReportLinks() + " 改为 null \n";

        if ((newIndicator.getAnalyzableDimensions() != null && oldIndicator.getAnalyzableDimensions() != null)
                && !newIndicator.getAnalyzableDimensions().equals(oldIndicator.getAnalyzableDimensions()))
            operation += "可分析维度由 " + oldIndicator.getAnalyzableDimensions() + " 改为 "
                    + newIndicator.getAnalyzableDimensions() + " \n";
        else if (oldIndicator.getAnalyzableDimensions() == null && newIndicator.getAnalyzableDimensions() != null)
            operation += "可分析维度新增为 " + newIndicator.getAnalyzableDimensions() + " \n";
        else if (oldIndicator.getAnalyzableDimensions() != null && newIndicator.getAnalyzableDimensions() == null)
            operation += "可分析维度由 " + oldIndicator.getAnalyzableDimensions() + " 改为 null \n";

        if ((newIndicator.getTechnicalCaliber() != null && oldIndicator.getTechnicalCaliber() != null)
                && !newIndicator.getTechnicalCaliber().equals(oldIndicator.getTechnicalCaliber()))
            operation += "技术口径由 " + oldIndicator.getTechnicalCaliber() + " 改为 " + newIndicator.getTechnicalCaliber()
                    + " \n";
        else if (oldIndicator.getTechnicalCaliber() == null && newIndicator.getTechnicalCaliber() != null)
            operation += "技术口径新增为 " + newIndicator.getTechnicalCaliber() + " \n";
        else if (oldIndicator.getTechnicalCaliber() != null && newIndicator.getTechnicalCaliber() == null)
            operation += "技术口径由 " + oldIndicator.getTechnicalCaliber() + " 改为 null \n";

        if ((newIndicator.getTechnicalCaliberLeader() != null && oldIndicator.getTechnicalCaliberLeader() != null)
                && !newIndicator.getTechnicalCaliberLeader().equals(oldIndicator.getTechnicalCaliberLeader()))
            operation += "技术口径负责人由 " + oldIndicator.getTechnicalCaliberLeader() + " 改为 "
                    + newIndicator.getTechnicalCaliberLeader() + " \n";
        else if (oldIndicator.getTechnicalCaliberLeader() == null && newIndicator.getTechnicalCaliberLeader() != null)
            operation += "技术口径负责人新增为 " + newIndicator.getTechnicalCaliberLeader() + " \n";
        else if (oldIndicator.getTechnicalCaliberLeader() != null && newIndicator.getTechnicalCaliberLeader() == null)
            operation += "技术口径负责人由 " + oldIndicator.getTechnicalCaliberLeader() + "改为 null \n";

        if ((newIndicator.getRealtimeTechnicalCaliber() != null && oldIndicator.getRealtimeTechnicalCaliber() != null)
                && !newIndicator.getRealtimeTechnicalCaliber().equals(oldIndicator.getRealtimeTechnicalCaliber()))
            operation += "实时技术口径由 " + oldIndicator.getRealtimeTechnicalCaliber() + " 改为 "
                    + newIndicator.getRealtimeTechnicalCaliber() + " \n";
        else if (oldIndicator.getRealtimeTechnicalCaliber() == null
                && newIndicator.getRealtimeTechnicalCaliber() != null)
            operation += "实时技术口径新增为 " + newIndicator.getRealtimeTechnicalCaliber() + " \n";
        else if (oldIndicator.getRealtimeTechnicalCaliber() != null
                && newIndicator.getRealtimeTechnicalCaliber() == null)
            operation += "实时技术口径由 " + oldIndicator.getRealtimeTechnicalCaliber() + " 改为 null \n";

        if ((newIndicator.getCompetentAuthoritie() != null && oldIndicator.getCompetentAuthoritie() != null)
                && !newIndicator.getCompetentAuthoritie().equals(oldIndicator.getCompetentAuthoritie()))
            operation += "主管部门由 " + oldIndicator.getCompetentAuthoritie() + " 改为 "
                    + newIndicator.getCompetentAuthoritie() + " \n";
        else if (oldIndicator.getCompetentAuthoritie() == null && newIndicator.getCompetentAuthoritie() != null)
            operation += "主管部门新增为 " + newIndicator.getCompetentAuthoritie() + " \n";
        else if (oldIndicator.getCompetentAuthoritie() != null && newIndicator.getCompetentAuthoritie() == null)
            operation += "主管部门由 " + oldIndicator.getCompetentAuthoritie() + " 改为 null \n";

        if (newIndicator.getIndicatorType() == Indicator.TYPE_DERIVATION
                && !oldIndicator.getDerivations().equals(newIndicator.getDerivations())) {
            operation += "衍生词由 ";
            for (int i = 0; i < oldIndicator.getDerivations().size(); i++) {
                operation += derivationService.getById(oldIndicator.getDerivations().get(i)).getDerivationName();
                if (i != oldIndicator.getDerivations().size() - 1)
                    operation += ", ";
            }
            operation += " 改为 ";
            for (int i = 0; i < newIndicator.getDerivations().size(); i++) {
                operation += derivationService.getById(newIndicator.getDerivations().get(i)).getDerivationName();
                if (i != newIndicator.getDerivations().size() - 1)
                    operation += ", ";
            }
            operation += " \n";
        }

        if (newIndicator.getIndicatorType() == Indicator.TYPE_MODIFIER
                && !oldIndicator.getModifiers().equals(newIndicator.getModifiers())) {
            operation += "修饰词由 ";
            for (int i = 0; i < oldIndicator.getModifiers().size(); i++) {
                operation += modifierService.getById(oldIndicator.getModifiers().get(i).get(1)).getModifierName();
                if (i != oldIndicator.getModifiers().size() - 1)
                    operation += ", ";
            }
            operation += " 改为 ";
            for (int i = 0; i < newIndicator.getModifiers().size(); i++) {
                operation += modifierService.getById(newIndicator.getModifiers().get(i).get(1)).getModifierName();
                if (i != newIndicator.getModifiers().size() - 1)
                    operation += ", ";
            }
            operation += " \n";
        }

        if (newIndicator.getIndicatorType() == Indicator.TYPE_MODIFIER && newIndicator.getTimeCycleId() != null
                && oldIndicator.getTimeCycleId() != null
                && !oldIndicator.getTimeCycleId().equals(newIndicator.getTimeCycleId()))
            operation += "时间周期由 " + timeCycleService.getById(oldIndicator.getTimeCycleId()).getTimeCycleName() + " 改为 "
                    + timeCycleService.getById(newIndicator.getTimeCycleId()).getTimeCycleName() + " \n";
        else if (newIndicator.getIndicatorType() == Indicator.TYPE_MODIFIER && newIndicator.getTimeCycleId() != null
                && oldIndicator.getTimeCycleId() == null)
            operation += "时间周期新增为 " + timeCycleService.getById(newIndicator.getTimeCycleId()).getTimeCycleName()
                    + " \n";
        else if (newIndicator.getIndicatorType() == Indicator.TYPE_MODIFIER && newIndicator.getTimeCycleId() == null
                && oldIndicator.getTimeCycleId() != null)
            operation += "时间周期由 " + timeCycleService.getById(oldIndicator.getTimeCycleId()).getTimeCycleName()
                    + " 改为 null \n";

        if (newIndicator.getIndicatorType() == Indicator.TYPE_COMPOSITE
                && !oldIndicator.getCompositeds().equals(newIndicator.getCompositeds())) {
            operation += "依赖的其他指标由 ";
            for (int i = 0; i < oldIndicator.getCompositeds().size(); i++) {
                operation += indicatorMapper.selectById(oldIndicator.getCompositeds().get(i)).getIndicatorName();
                if (i != oldIndicator.getCompositeds().size() - 1)
                    operation += ", ";
            }
            operation += " 改为 ";
            for (int i = 0; i < newIndicator.getCompositeds().size(); i++) {
                operation += indicatorMapper.selectById(newIndicator.getCompositeds().get(i)).getIndicatorName();
                if (i != newIndicator.getCompositeds().size() - 1)
                    operation += ", ";
            }
            operation += " \n";
        }

        if (oldIndicator.getIndicatorType() == Indicator.TYPE_COMPOSITE && newIndicator.getCalculateRule() != null
                && !oldIndicator.getCalculateRule().equals(newIndicator.getCalculateRule()))
            operation += "运算规则由 " + oldIndicator.getCalculateRule() + " 改为 " + newIndicator.getCalculateRule() + " \n";

        version.setOperation(operation);
        version.setOperateTime(null);
        version.setVersionId(null);
        version.updateVersion();
        return version;
    }
}
