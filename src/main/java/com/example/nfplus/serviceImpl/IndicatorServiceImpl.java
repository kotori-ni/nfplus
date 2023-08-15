/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 21:00:23
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:20:59
 */
package com.example.nfplus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.nfplus.entity.*;
import com.example.nfplus.mapper.IndicatorMapper;
import com.example.nfplus.mapper.VersionMapper;
import com.example.nfplus.service.*;
import com.example.nfplus.utils.IndicatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class IndicatorServiceImpl extends ServiceImpl<IndicatorMapper, Indicator> implements IndicatorService {
    @Autowired
    private IndicatorMapper indicatorMapper;
    @Autowired
    private VersionService versionService;
    @Autowired
    private VersionMapper versionMapper;
    @Autowired
    private DerivationService derivationService;
    @Autowired
    private ModifierService modifierService;
    @Autowired
    private TimeCycleService timeCycleService;

    /**
     * @description: 获取指标详细信息
     * @param {User} user 请求查询指标的用户
     * @param {String} indicatorId 指标id
     * @return {Indicator} 指标信息
     * @author: wch
     */   
    @Override
    public Indicator getOneIndicator(User user, String indicatorId){
        Indicator indicator = indicatorMapper.selectOneIndicator(user, indicatorId);
        getIndicatorInfo(indicator);
        return indicator;
    }

    /**
     * @description: 获取浏览量最高的五个指标
     * @return {List<Indicator>} 指标列表
     * @author: wch
     */    
    @Override
    public List<Indicator> getViewMaxIndicators() {
        QueryWrapper<Indicator> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("views_num");
        queryWrapper.last("limit 5");
        return list(queryWrapper);
    }

    /**
     * @description: 获取用户收藏的所有指标
     * @param {User} user 请求查询的用户
     * @return {List<Indicator>} 指标列表
     * @author: wch
     */  
    @Override
    public List<Indicator> getUserFavourIndicators(User user) {
        QueryWrapper<Indicator> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("collection_id");
        return indicatorMapper.selectIndicators(user, queryWrapper);
    }

    /**
     * @description: 分页按搜索条件获取用户收藏的指标
     * @param {User} user 请求查询的用户
     * @param {IndicatorQuery} indicatorQuery 分页查询条件
     * @return {Page<Indicator>} 指标分页列表
     * @author: wch
     */    
    @Override
    public Page<Indicator> getUserFavourIndicatorsWithPage(User user, IndicatorQuery indicatorQuery) {
        QueryWrapper<Indicator> queryWrapper = getQueryWrapper(indicatorQuery);
        queryWrapper.isNotNull("collection_id");
        Page<Indicator> page = new Page<>(indicatorQuery.getPage(), indicatorQuery.getPageSize());
        List<Indicator> indicators = indicatorMapper.selectIndicators(page, user, queryWrapper);
        int index = (int)((page.getCurrent() - 1) * page.getSize());
        for (Indicator indicator : indicators){
            indicator.setIndex(++index);
            getIndicatorInfo(indicator);
        }
        page.setRecords(indicators);
        return page;
    }

    /**
     * @description: 获取指标的所有创建者
     * @return {List<User>} 指标创建者列表
     * @author: wch
     */
    @Override
    public List<User> getIndicatorCreators() {
        return indicatorMapper.selectIndicatorCreators();
    }

    /**
     * @description: 获取指标的血缘树结点信息与边信息
     * @param {String} indicatorId 指标id
     * @return {Map<String, Object>} 指标血缘树结点信息与边信息 {nodes: [], edges: []} nodes: 结点信息, edges: 边信息
     * @author: wch
     */  
    @Override
    public Map<String, Object> getIndicatorTreeById(String indicatorId) throws IllegalArgumentException{
        Indicator sourceIndicator = getById(indicatorId);
        if (sourceIndicator == null)
            throw new IllegalArgumentException("指标" + indicatorId + "不存在");

        Map<String, Object> records = new HashMap<>();
        List<Indicator> nodes = new ArrayList<>();
        List<Map<String, Object>> edges = new ArrayList<>();
        QueryWrapper<Indicator> queryWrapper = new QueryWrapper<>();

        sourceIndicator.setIndicatorTypeName(IndicatorUtils.getTypeName(sourceIndicator.getIndicatorType()));
        nodes.add(sourceIndicator);

        //主原子指标
        if (sourceIndicator.getIndicatorType() == Indicator.TYPE_ATOMIC){
            queryWrapper.eq("dependent_indicator_id", indicatorId);
            List<Indicator> derivationModifierIndicators = list(queryWrapper);
            for (Indicator node : derivationModifierIndicators) {
                node.setIndicatorTypeName(IndicatorUtils.getTypeName(node.getIndicatorType()));
                nodes.add(node);
                Map<String, Object> edge = new HashMap<>();
                edge.put("source", indicatorId);
                edge.put("target", node.getIndicatorId());
                edges.add(edge);
            }

            List<String> composites = indicatorMapper.getCompositeIndicatorIdByOtherIndicator(indicatorId);
            for (String compositeId : composites){
                Indicator composite = getById(compositeId);
                composite.setIndicatorTypeName(IndicatorUtils.getTypeName(composite.getIndicatorType()));
                nodes.add(composite);
                Map<String, Object> edge = new HashMap<>();
                edge.put("source", indicatorId);
                edge.put("target", composite.getIndicatorId());
                edges.add(edge);
            }
        }

        //复合指标
        else if (sourceIndicator.getIndicatorType() == Indicator.TYPE_COMPOSITE){
            List<String> composites = indicatorMapper.getOtherIndicatorsByCompositeIndicatorId(indicatorId);
            for (String compositeId : composites){
                Indicator composite = getById(compositeId);
                composite.setIndicatorTypeName(IndicatorUtils.getTypeName(composite.getIndicatorType()));
                nodes.add(composite);
                Map<String, Object> edge = new HashMap<>();
                edge.put("source", composite.getIndicatorId());
                edge.put("target", indicatorId);
                edges.add(edge);
            }
        }

        //衍生原子指标与派生指标
        else {
            List<String> otherIndicators = indicatorMapper.getCompositeIndicatorIdByOtherIndicator(indicatorId);
            Indicator dependentIndicator = getById(sourceIndicator.getDependentIndicatorId());
            dependentIndicator.setIndicatorTypeName(IndicatorUtils.getTypeName(dependentIndicator.getIndicatorType()));
            nodes.add(dependentIndicator);
            Map<String, Object> edge = new HashMap<>();
            edge.put("source", dependentIndicator.getIndicatorId());
            edge.put("target", indicatorId);
            edges.add(edge);
            for (String otherIndicatorId : otherIndicators){
                Indicator otherIndicator = getById(otherIndicatorId);
                otherIndicator.setIndicatorTypeName(IndicatorUtils.getTypeName(otherIndicator.getIndicatorType()));
                nodes.add(otherIndicator);
                edge = new HashMap<>();
                edge.put("source", indicatorId);
                edge.put("target", otherIndicator.getIndicatorId());
                edges.add(edge);
            }
        }

        records.put("nodes", nodes);
        records.put("edges", edges);
        return records;
    }

    /**
     * @description: 按搜索条件查询指标
     * @param {User} user 请求查询的用户
     * @param {IndicatorQuery} indicatorQuery 指标查询条件
     * @return {List<Indicator>} 指标列表
     * @author: wch
     */  
    @Override
    public List<Indicator> getIndicators(User user, IndicatorQuery indicatorQuery) {
        QueryWrapper<Indicator> queryWrapper = getQueryWrapper(indicatorQuery);
        List<Indicator> indicators = indicatorMapper.selectIndicators(user, queryWrapper);
        for (Indicator indicator : indicators)
            getIndicatorInfo(indicator);
        return indicators;
    }

    /**
     * @description: 分页按搜索条件查询指标
     * @param {User} user 请求查询的用户
     * @param {IndicatorQuery} indicatorQuery 指标查询条件
     * @return {Page<Indicator>} 指标分页列表
     * @author: wch
     */ 
    @Override
    public Page<Indicator> getIndicatorsWithPage(User user, IndicatorQuery indicatorQuery) {
        QueryWrapper<Indicator> queryWrapper = getQueryWrapper(indicatorQuery);
        Page<Indicator> page = new Page<>(indicatorQuery.getPage(), indicatorQuery.getPageSize());
        List<Indicator> indicators = indicatorMapper.selectIndicators(page, user, queryWrapper);
        int index = (int)((page.getCurrent() - 1) * page.getSize());
        for (Indicator indicator : indicators){
            indicator.setIndex(++index);
            getIndicatorInfo(indicator);
        }
        page.setRecords(indicators);
        return page;
    }

    /**
     * @description: 新增指标
     * @param {User} user 请求新增指标的用户
     * @param {Indicator} indicator 新增的指标信息
     * @return {Boolean} 新增成功则返回true,否则抛出异常
     * @author: wch
     */ 
    @Override
    @Transactional
    public Boolean saveIndicator(User user, Indicator indicator) {
        indicator.setCreatorId(user.getUserId());
        indicator.setLastOperateTime(new Date());
        indicator.setLastOperatorId(user.getUserId());

        //添加衍生词,修饰词,运算指标等信息
        save(indicator);
        if (indicator.getIndicatorType() == Indicator.TYPE_DERIVATION){
            for (Integer derivation : indicator.getDerivations())
                indicatorMapper.insertIndicatorDerivation(indicator.getIndicatorId(), derivation);
        }
        else if (indicator.getIndicatorType() == Indicator.TYPE_MODIFIER){
            for (List<Integer> modifier : indicator.getModifiers())
                indicatorMapper.insertIndicatorModifier(indicator.getIndicatorId(), modifier.get(1));
        }
        else if (indicator.getIndicatorType() == Indicator.TYPE_COMPOSITE){
            for (String composited : indicator.getCompositeds())
                indicatorMapper.insertCompositeIndicator(indicator.getIndicatorId(), composited);
        }

        //添加指标新版本
        Version version = new Version();
        version.setIndicatorId(indicator.getIndicatorId());
        version.setOperatorId(user.getUserId());
        version.setOperation("创建了该指标");
        versionService.save(version);
        return true;
    }

    /**
     * @description: 更新指标信息(不包括指标状态)
     * @param {User} user 请求更新指标的用户
     * @param {Indicator} indicator 更新的指标信息
     * @return {Boolean} 更新成功则返回true,否则抛出异常
     * @author: wch
     */
    @Override
    @Transactional
    public Boolean updateIndicator(User user, Indicator newIndicator) {
        Indicator oldIndicator = getById(newIndicator.getIndicatorId());
        getIndicatorInfo(oldIndicator);
        newIndicator.setLastOperateTime(new Date());
        newIndicator.setLastOperatorId(user.getUserId());
        updateById(newIndicator);

        sortIndicator(oldIndicator);
        sortIndicator(newIndicator);
        //比较衍生词,修饰词,运算指标是否有变化
        if (newIndicator.getIndicatorType() == Indicator.TYPE_DERIVATION && !oldIndicator.getDerivations().equals(newIndicator.getDerivations())){
            indicatorMapper.deleteIndicatorDerivation(newIndicator.getIndicatorId());
            for (Integer derivation : newIndicator.getDerivations())
                indicatorMapper.insertIndicatorDerivation(newIndicator.getIndicatorId(), derivation);
        }
        else if (newIndicator.getIndicatorType() == Indicator.TYPE_MODIFIER && !oldIndicator.getModifiers().equals(newIndicator.getModifiers())){
            indicatorMapper.deleteIndicatorModifier(newIndicator.getIndicatorId());
            for (List<Integer> modifier : newIndicator.getModifiers())
                indicatorMapper.insertIndicatorModifier(newIndicator.getIndicatorId(), modifier.get(1));
        }
        else if (newIndicator.getIndicatorType() == Indicator.TYPE_COMPOSITE && !oldIndicator.getCompositeds().equals(newIndicator.getCompositeds())){
            indicatorMapper.deleteCompositeIndicator(newIndicator.getIndicatorId());
            for (String composited : newIndicator.getCompositeds())
                indicatorMapper.insertCompositeIndicator(newIndicator.getIndicatorId(), composited);
        }

        //更新指标版本信息
        Version version = versionService.getVersionInfo(oldIndicator, newIndicator);
        if (version.getOperation() != null && version.getOperation().length() != 0) {
            version.updateVersion();
            versionService.save(version);
        }

        return true;
    }

    /**
     * @description: 更新指标状态
     * @param {User} user 请求更新指标状态的用户
     * @param {int} newState 新的指标状态
     * @param {Indicator} indicator 待更新的指标
     * @return {Boolean} 更新成功则返回true,否则抛出异常
     * @author: wch
     */ 
    @Override
    @Transactional
    public Boolean updateIndicatorState(User user, int newState, Indicator indicator) {
        indicator.setLastOperateTime(null);
        indicator.setLastOperateTime(new Date());
        indicator.setLastOperatorId(user.getUserId());
        indicator.setIndicatorState(newState);
        updateById(indicator);

        Version version = versionMapper.findLastVersionByIndicatorId(indicator.getIndicatorId());
        version.setVersionId(null);
        version.setOperatorId(user.getUserId());
        if (newState == 3)
            version.setOperation("发布了该指标");
        else
            version.setOperation("下线了该指标");
        System.out.println(newState);
        System.out.println("operation: " + version.getOperation());
        versionService.save(version);
        return true;
    }

    /**
     * @description: 验证指标中的信息是否合法
     * @param {Indicator} indicator 待验证的指标
     * @return {Boolean} 验证成功则返回true,否则抛出异常
     * @throws {IllegalArgumentException} 参数异常
     * @author: wch
     */   
    @Override
    public Boolean verifyIndicator(Indicator indicator) throws IllegalArgumentException{
        if (indicator.getIndicatorId() == null || indicator.getIndicatorId().length() == 0)
            throw new IllegalArgumentException("指标标识不能为空");

        if (indicator.getIndicatorId().length() > Indicator.IDSIZE)
            throw new IllegalArgumentException("指标标识长度过长");

        if (indicator.getIndicatorName() == null || indicator.getIndicatorName().length() == 0)
            throw new IllegalArgumentException("指标名称不能为空");

        if (indicator.getIndicatorName().length() > Indicator.IDSIZE)
            throw new IllegalArgumentException("指标名称长度过长");

        if (indicator.getIndicatorType() != Indicator.TYPE_DERIVATION
                && indicator.getIndicatorType() != Indicator.TYPE_MODIFIER
                && indicator.getDependentIndicatorId() != null)
            throw new IllegalArgumentException("不需要依赖的主原子指标");

        if ((indicator.getIndicatorType() == Indicator.TYPE_DERIVATION
                || indicator.getIndicatorType() == Indicator.TYPE_MODIFIER)
                && indicator.getDependentIndicatorId() == null)
            throw new IllegalArgumentException("依赖的主原子指标标识不能为空");

        if ((indicator.getIndicatorType() == Indicator.TYPE_DERIVATION || indicator.getIndicatorType() == Indicator.TYPE_MODIFIER)
                && getById(indicator.getDependentIndicatorId()).getIndicatorType() != Indicator.TYPE_ATOMIC)
            throw new IllegalArgumentException("依赖的主原子指标不存在");

        if (IndicatorUtils.getTypeName(indicator.getIndicatorType()) == null)
            throw new IllegalArgumentException("指标类型不存在");

        if (IndicatorUtils.getStateName(indicator.getIndicatorState()) == null)
            throw new IllegalArgumentException("指标状态不存在");

        if (indicator.getDomainId() == null)
            throw new IllegalArgumentException("指标域不能为空");

        if (indicator.getSecurityLevel() < Indicator.MIN_SECURITY_LEVEL
                || indicator.getSecurityLevel() > Indicator.MAX_SECURITY_LEVEL)
            throw new IllegalArgumentException("指标安全级别不合法");

        if (indicator.getAnalyzableDimensions() != null
                && indicator.getAnalyzableDimensions().length() > Indicator.TEXTSIZE)
            throw new IllegalArgumentException("可分析维度长度过长");

        if (indicator.getAffiliatedReportLinks() != null
                && indicator.getAffiliatedReportLinks().length() > Indicator.LINKSIZE)
            throw new IllegalArgumentException("关联报表链接长度过长");

        if (indicator.getBusinessCaliber() == null || indicator.getBusinessCaliber().length() == 0)
            throw new IllegalArgumentException("业务口径不能为空");

        if (indicator.getBusinessCaliber().length() > Indicator.TEXTSIZE)
            throw new IllegalArgumentException("业务口径长度过长");

        if (indicator.getBusinessCaliberLeader() == null || indicator.getBusinessCaliberLeader().length() == 0)
            throw new IllegalArgumentException("业务口径负责人不能为空");

        if (indicator.getBusinessCaliberLeader().length() > Indicator.IDSIZE)
            throw new IllegalArgumentException("业务口径负责人长度过长");

        if (indicator.getTechnicalCaliber() != null && indicator.getTechnicalCaliber().length() > Indicator.TEXTSIZE)
            throw new IllegalArgumentException("技术口径长度过长");

        if (indicator.getTechnicalCaliberLeader() != null
                && indicator.getTechnicalCaliberLeader().length() > Indicator.IDSIZE)
            throw new IllegalArgumentException("技术口径负责人长度过长");

        if (indicator.getRealtimeTechnicalCaliber() != null
                && indicator.getRealtimeTechnicalCaliber().length() > Indicator.TEXTSIZE)
            throw new IllegalArgumentException("实时技术口径长度过长");

        if (indicator.getCompetentAuthoritie() != null
                && indicator.getCompetentAuthoritie().length() > Indicator.IDSIZE)
            throw new IllegalArgumentException("主管部门长度过长");

        if (indicator.getIndicatorType() != Indicator.TYPE_DERIVATION
                && (indicator.getDerivations() != null && indicator.getDerivations().size() != 0))
            throw new IllegalArgumentException("非衍生原子指标不能添加衍生词");

        if (indicator.getIndicatorType() == Indicator.TYPE_DERIVATION
                && (indicator.getDerivations() == null || indicator.getDerivations().size() == 0))
            throw new IllegalArgumentException("衍生词不能为空");

        if (indicator.getIndicatorType() == Indicator.TYPE_DERIVATION
                && indicator.getDerivations().size() > Indicator.MAX_DERIVATIONS)
            throw new IllegalArgumentException("衍生词数量不能超过" + Indicator.MAX_DERIVATIONS);

        if (indicator.getIndicatorType() != Indicator.TYPE_MODIFIER
                && (indicator.getModifiers() != null && indicator.getModifiers().size() != 0))
            throw new IllegalArgumentException("非派生指标不能添加修饰词");

         if (indicator.getIndicatorType() == Indicator.TYPE_MODIFIER &&
            (indicator.getModifiers() == null || indicator.getModifiers().size() == 0))
            throw new IllegalArgumentException("修饰词不能为空");

        if (indicator.getIndicatorType() == Indicator.TYPE_MODIFIER
                && indicator.getModifiers().size() > Indicator.MAX_MODIFIERS)
            throw new IllegalArgumentException("修饰词数量不能超过" + Indicator.MAX_MODIFIERS);

        if (indicator.getIndicatorType() != Indicator.TYPE_MODIFIER && indicator.getTimeCycleId() != null)
            throw new IllegalArgumentException("非派生指标不能添加时间周期");

        if (indicator.getTimeCycleId() != null && timeCycleService.getById(indicator.getTimeCycleId()) == null)
            throw new IllegalArgumentException("时间周期" + indicator.getTimeCycleId() + "不存在");

        if (indicator.getIndicatorType() != Indicator.TYPE_COMPOSITE && indicator.getCalculateRule() != null)
            throw new IllegalArgumentException("非复合指标不能添加运算规则");

        if (indicator.getIndicatorType() == Indicator.TYPE_COMPOSITE
                && (indicator.getCalculateRule() == null || indicator.getCalculateRule().length() == 0))
            throw new IllegalArgumentException("运算规则不能为空");

        if (indicator.getCalculateRule() != null && indicator.getCalculateRule().length() > Indicator.TEXTSIZE)
            throw new IllegalArgumentException("运算规则长度过长");

        if (indicator.getIndicatorType() != Indicator.TYPE_COMPOSITE
                && (indicator.getCompositeds() != null && indicator.getCompositeds().size() != 0))
            throw new IllegalArgumentException("非复合指标不能添加其他指标");

        if (indicator.getIndicatorType() == Indicator.TYPE_COMPOSITE
                && (indicator.getCompositeds() == null || indicator.getCompositeds().size() == 0))
            throw new IllegalArgumentException("其他指标不能为空");

        if (indicator.getAnalyzableDimensions() != null && indicator.getAnalyzableDimensions().length() == 0)
            indicator.setAnalyzableDimensions(null);
        if (indicator.getAffiliatedReportLinks() != null && indicator.getAffiliatedReportLinks().length() == 0)
            indicator.setAffiliatedReportLinks(null);
        if (indicator.getTechnicalCaliber() != null && indicator.getTechnicalCaliber().length() == 0)
            indicator.setTechnicalCaliber(null);
        if (indicator.getTechnicalCaliberLeader() != null && indicator.getTechnicalCaliberLeader().length() == 0)
            indicator.setTechnicalCaliberLeader(null);
        if (indicator.getRealtimeTechnicalCaliber() != null && indicator.getRealtimeTechnicalCaliber().length() == 0)
            indicator.setRealtimeTechnicalCaliber(null);
        if (indicator.getCompetentAuthoritie() != null && indicator.getCompetentAuthoritie().length() == 0)
            indicator.setCompetentAuthoritie(null);

        if (indicator.getDerivations() != null){
            for (Integer derivation : indicator.getDerivations()){
                if (derivationService.getById(derivation) == null)
                    throw new IllegalArgumentException("衍生词" + derivation + "不存在");
            }
        }
        else if (indicator.getModifiers() != null){
            for (List<Integer> modifier : indicator.getModifiers()){
                if (modifierService.getById(modifier.get(1)) == null)
                    throw new IllegalArgumentException("修饰词" + modifier.get(1) + "不存在");
            }
        }
        else if (indicator.getCompositeds() != null){
            for (String compositeds : indicator.getCompositeds()){
                if (getById(compositeds) == null)
                    throw new IllegalArgumentException("指标" + compositeds + "不存在");
            }
        }

        return true;
    }

    /**
     * @description: 获取指标完整信息(指标域名称,指标类型名称,指标状态名称等)
     * @param {Indicator} indicator 指标对象
     * @return {*}
     * @author: wch
     */    
    private void getIndicatorInfo(Indicator indicator){
        indicator.getInfo();
        if (indicator.getIndicatorType() == 2){
            indicator.setDerivations(new ArrayList<>());
            indicator.setDerivationNames(new ArrayList<>());
            List<Integer> derivationIds = indicatorMapper.getIndicatorDerivations(indicator.getIndicatorId());
            for (Integer derivationId: derivationIds) {
                indicator.getDerivations().add(derivationId);
                indicator.getDerivationNames().add(derivationService.getById(derivationId).getDerivationName());
            }
        }
        else if (indicator.getIndicatorType() == 3){
            indicator.setModifiers(new ArrayList<>());
            indicator.setModifierNames(new ArrayList<>());
            List<Integer> modifierIds = indicatorMapper.getIndicatorModifiers(indicator.getIndicatorId());
            for (Integer modifierId: modifierIds) {
                List<Integer> modifier = new ArrayList<>();
                modifier.add(modifierService.getById(modifierId).getParentModifierId());
                modifier.add(modifierId);
                indicator.getModifiers().add(modifier);
                indicator.getModifierNames().add(modifierService.getById(modifierId).getModifierName());
            }
        }
        else if (indicator.getIndicatorType() == 4){
            indicator.setCompositeds(new ArrayList<>());
            indicator.setCompositedNames(new ArrayList<>());
            List<String> compositedIds = indicatorMapper.getOtherIndicatorsByCompositeIndicatorId(indicator.getIndicatorId());
            for (String compositedId : compositedIds) {
                indicator.getCompositeds().add(compositedId);
                indicator.getCompositedNames().add(getById(compositedId).getIndicatorName());
            }
        }
    }

    /**
     * @description: 根据传入的搜索条件构造数据库查询条件
     * @param {IndicatorQuery} indicatorQuery 指标搜索条件
     * @return {QueryWrapper<Indicator>} 数据库查询条件对象QueryWrapper<Indicator>
     * @author: wch
     */    
    private QueryWrapper<Indicator> getQueryWrapper(IndicatorQuery indicatorQuery){
        QueryWrapper<Indicator> queryWrapper = new QueryWrapper<>();
        if (indicatorQuery.getIndicatorType() != null)
            queryWrapper.eq("i.indicator_type", indicatorQuery.getIndicatorType());
        if (indicatorQuery.getIndicatorState() != null)
            queryWrapper.eq("i.indicator_state", indicatorQuery.getIndicatorState());
        if (indicatorQuery.getDomainId() != null)
            queryWrapper.eq("i.domain_id", indicatorQuery.getDomainId());
        if (indicatorQuery.getCreatorId() != null)
            queryWrapper.eq("i.creator_id", indicatorQuery.getCreatorId());
        if (indicatorQuery.getKeyword() != null)
            queryWrapper.like("i.indicator_name", indicatorQuery.getKeyword());
        return queryWrapper;
    }

    /**
     * @description: 对指标的衍生词,修饰词,运算指标按id从小到大进行排序
     * @param {Indicator} indicator 待排序指标对象
     * @return {*}
     * @author: wch
     */
    private void sortIndicator(Indicator indicator) {
        if (indicator.getIndicatorType() == Indicator.TYPE_DERIVATION && indicator.getDerivations() != null)
            indicator.getDerivations().sort(Comparator.naturalOrder());
        if (indicator.getIndicatorType() == Indicator.TYPE_MODIFIER && indicator.getModifiers() != null)
            indicator.getModifiers().sort((o1, o2) -> {
                if (o1.get(0) > o2.get(0)) {
                    return 1;
                } else if (o1.get(0) < o2.get(0)) {
                    return -1;
                } else {
                    return o1.get(1).compareTo(o2.get(1));
                }
            });
        if (indicator.getIndicatorType() == Indicator.TYPE_COMPOSITE && indicator.getCompositeds() != null)
            indicator.getCompositeds().sort(Comparator.naturalOrder());
    }
}
