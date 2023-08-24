/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 20:58:39
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:20:49
 */
package com.example.nfplus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.nfplus.entity.Indicator;
import com.example.nfplus.entity.Modifier;
import com.example.nfplus.entity.User;
import com.example.nfplus.entity.WordsQuery;
import com.example.nfplus.mapper.IndicatorMapper;
import com.example.nfplus.mapper.ModifierMapper;
import com.example.nfplus.service.ModifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;

@Service
public class ModifierServiceImpl extends ServiceImpl<ModifierMapper, Modifier> implements ModifierService {
    @Autowired
    private ModifierMapper modifierMapper;
    @Autowired
    private IndicatorMapper indicatorMapper;

    /**
     * @description: 获取所有修饰词
     * @param user        请求查询的用户
     * @param needAll     是否需要"全部"这个结点
     * @param allowParent 修饰词值是否可选 可选则disable属性为false,否则为true
     * @return {List<Map<String, Object>>} 修饰词列表
     * @author: wch
     */
    @Override
    public List<Map<String, Object>> getAllModifier(User user, Boolean needAll, Boolean allowParent) {
        List<Modifier> modifiers = list();
        List<Map<String, Object>> modifierHierarchy = new ArrayList<>();

        // 构建父子域关系映射
        Map<Integer, List<Modifier>> modifierMap = new HashMap<>();
        for (Modifier modifier : modifiers) {
            int parentId = 0;
            if (modifier.getParentModifierId() != null)
                parentId = modifier.getParentModifierId();
            modifierMap.putIfAbsent(parentId, new ArrayList<>());
            modifierMap.get(parentId).add(modifier);
        }

        // 从根节点开始递归构建类似结构的数据
        List<Modifier> rootModifiers = modifierMap.get(0);
        if (needAll) {
            Map<String, Object> all = new HashMap<>();
            all.put("value", 0);
            all.put("label", "全部");
            modifierHierarchy.add(all);
        }

        if (rootModifiers != null) {
            for (Modifier rootModifier : rootModifiers) {
                Map<String, Object> modifierNode = createAllModifierNode(rootModifier, modifierMap, allowParent);
                modifierHierarchy.add(modifierNode);
            }
        }

        return modifierHierarchy;
    }

    /**
     * @description: 按搜索条件获取修饰词
     * @param user       请求查询的用户
     * @param wordsQuery 查询条件
     * @return {List<Modifier>} 修饰词列表
     * @author: wch
     */
    @Override
    public List<Modifier> getModifiers(User user, WordsQuery wordsQuery) {
        QueryWrapper<Modifier> queryWrapper = getQueryWrapper(wordsQuery);
        List<Modifier> modifiers = modifierMapper.selectModifiers(user, queryWrapper);
        for (Modifier modifier : modifiers){
            int quoteNum = 0;
            for (Modifier modifierValue : modifier.getModifierValues())
                quoteNum += modifierMapper.selectQuoteIndicatorNum(modifierValue.getModifierId());
            modifier.setQuoteNum(quoteNum);
        }
        return modifiers;
    }

    /**
     * @description: 按搜索条件分页获取修饰词
     * @param user       请求查询的用户
     * @param wordsQuery 查询条件
     * @return {Page<Modifier>} 修饰词分页列表
     * @author: wch
     */
    @Override
    public Page<Modifier> getModifiersWithPage(User user, WordsQuery wordsQuery) {
        QueryWrapper<Modifier> queryWrapper = getQueryWrapper(wordsQuery);
        Page<Modifier> page = new Page<>(wordsQuery.getPage(), wordsQuery.getPageSize());
        List<Modifier> modifierKeys = modifierMapper.selectModifiers(page, user, queryWrapper);
        int index = (int) ((page.getCurrent() - 1) * page.getSize());
        for (Modifier modifierKey : modifierKeys) {
            modifierKey.setIndex(++index);
            int quoteNum = 0;
            for (Modifier modifierValue : modifierKey.getModifierValues())
                quoteNum += modifierMapper.selectQuoteIndicatorNum(modifierValue.getModifierId());
            modifierKey.setQuoteNum(quoteNum);
        }
        page.setRecords(modifierKeys);
        return page;
    }

    /**
     * @description: 获取引用该修饰词的指标列表
     * @param modifierId 修饰词id
     * @return {List<Indicator>} 指标列表
     * @author: wch
     */
    @Override
    public List<Indicator> findQuoteIndicators(int modifierId) {
        QueryWrapper<Modifier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("modifier_key_id", modifierId);
        List<Modifier> modifierValues = list(queryWrapper);
        List<Indicator> indicators = new ArrayList<>();

        for (Modifier modifierValue : modifierValues) {
            List<String> indicatorIds = modifierMapper.selectQuoteIndicators(modifierValue.getModifierId());
            if (indicatorIds == null || indicatorIds.size() == 0)
                continue;
            List<Indicator> childIndicators = indicatorMapper.selectBatchIds(indicatorIds);
            for (Indicator childIndicator : childIndicators) {
                if (!indicators.contains(childIndicator))
                    indicators.add(childIndicator);
            }
        }
        return indicators;
    }

    /**
     * @description: 根据修饰词键的名称获取修饰词键
     * @param name 修饰词键名称
     * @return {Modifier} 修饰词键对象
     * @author: wch
     */
    @Override
    public Modifier findModifierKeyByName(String name) {
        QueryWrapper<Modifier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("modifier_name", name);
        queryWrapper.isNull("modifier_key_id");
        return getOne(queryWrapper);
    }

    /**
     * @description: 新增修饰词
     * @param user     请求新增的用户
     * @param modifier 新增的修饰词
     * @return {*}
     * @author: wch
     */
    @Override
    public void addModifier(User user, Modifier modifier) {
        verifyModifier(modifier);
        Modifier existModifier = findModifierKeyByName(modifier.getModifierName());
        if (existModifier != null)
            throw new IllegalArgumentException("修饰词" + modifier.getModifierName() + "已存在");
        modifier.setModifierId(null);
        modifier.setParentModifierId(null);
        modifier.setCreatorId(user.getUserId());
        save(modifier);

        int modifierKeyId = findModifierKeyByName(modifier.getModifierName()).getModifierId();
        List<String> modifierValues = new ArrayList<>();

        if (modifier.getModifierValues() != null) {
            for (Modifier modifierValue : modifier.getModifierValues()) {
                if (modifierValue.getModifierName() == null || modifierValue.getModifierName().length() == 0)
                    throw new IllegalArgumentException("缺少字段枚举名称");
                modifierValues.add(modifierValue.getModifierName());
            }
        }

        if (modifierValues.size() != 0) {
            Set<String> set = new HashSet<>(modifierValues);
            if (set.size() < modifierValues.size())
                throw new IllegalArgumentException("存在重复的字段枚举名称");
        }

        if (modifier.getModifierValues() != null) {
            for (Modifier modifierValue : modifier.getModifierValues()) {
                modifierValue.setModifierId(null);
                modifierValue.setCreatorId(user.getUserId());
                modifierValue.setParentModifierId(modifierKeyId);
                save(modifierValue);
            }
        }
    }

    /**
     * @description: 批量新增修饰词
     * @param user     请求新增的用户
     * @param modifiers 新增的修饰词
     * @return {*}
     * @author: wch
     */
    @Override
    @Transactional
    public void batchAddModifier(User user, List<Modifier> modifiers) {
        for (int i = 0; i < modifiers.size(); i++){
            if (modifiers.get(i).getModifierValueNames() == null || modifiers.get(i).getModifierValueNames().size() == 0)
                continue;

            QueryWrapper<Modifier> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("modifier_name", modifiers.get(i).getModifierName());
            queryWrapper.isNull("modifier_key_id");
            Modifier modifierKey = getOne(queryWrapper);
            for (int j = 0; j < modifiers.get(i).getModifierValueNames().size(); j++){
                Modifier modifierValue = new Modifier();
                modifierValue.setModifierName(modifiers.get(i).getModifierValueNames().get(j));
                modifierValue.setParentModifierId(modifierKey.getModifierId());
                modifierValue.setCreatorId(user.getUserId());
                save(modifierValue);
            }
        }
    }

    /**
     * @description: 更新修饰词信息
     * @param user     请求更新的用户
     * @param modifier 更新的修饰词
     * @return {*}
     * @author: wch
     */
    @Override
    @Transactional
    public void updateModifier(User user, Modifier modifier) {
        modifier.setCreatorId(getById(modifier.getModifierId()).getCreatorId());
        List<String> oldModifierValues = modifierMapper.getModifierValueNameByKeyId(modifier.getModifierId());
        List<String> newModifierValues = new ArrayList<>();
        for (Modifier modifierValue : modifier.getModifierValues()) {
            newModifierValues.add(modifierValue.getModifierName());
        }

        // 求出修改前和修改后的修饰词值交集
        Set<String> intersection = new HashSet<>(oldModifierValues);
        intersection.retainAll(newModifierValues);
        System.out.println(intersection);

        // 删除修改后不存在的修饰词值
        for (String oldModifierValue : oldModifierValues) {
            if (!intersection.contains(oldModifierValue)) {
                removeModifierValue(oldModifierValue, modifier.getModifierId());
            }
        }

        // 添加新修饰词值
        for (String newModifierValue : newModifierValues) {
            if (!intersection.contains(newModifierValue)) {
                Modifier newModifier = new Modifier();
                newModifier.setModifierName(newModifierValue);
                newModifier.setParentModifierId(modifier.getModifierId());
                newModifier.setCreatorId(user.getUserId());
                save(newModifier);
            }
        }
    }

    /**
     * @description: 删除修饰词
     * @param modifier 待删除的修饰词
     * @return {*}
     * @author: wch
     */
    @Override
    @Transactional
    public void removeModifer(Modifier modifier) throws IllegalArgumentException{
        int quoteNum = 0;
        List<Modifier> modifierValues = query().eq("modifier_key_id", modifier.getModifierId()).list();
        for (Modifier modifierValue : modifierValues){
            quoteNum += modifierMapper.selectQuoteIndicatorNum(modifierValue.getModifierId());
        }
        if (quoteNum > 0)
            throw new IllegalArgumentException("有 " + quoteNum + " 个指标引用了该修饰词，不可删除");
        for (Modifier modifierValue : modifierValues){
            removeById(modifierValue);
        }
        removeById(modifier);
    }

    /**
     * @description: 验证修饰词信息是否正确
     * @param modifier 待验证的修饰词对象
     * @return {Boolean} true:正确 错误则抛出异常
     * @author: wch
     */
    @Override
    public Boolean verifyModifier(Modifier modifier) throws IllegalArgumentException {
        if (modifier.getModifierName() == null)
            throw new IllegalArgumentException("缺少修饰词名称");
        if (modifier.getParentModifierId() != null && getById(modifier.getParentModifierId()) == null)
            throw new IllegalArgumentException("修饰词键不存在");
        if (modifier.getParentModifierId() != null
                && getById(modifier.getParentModifierId()).getParentModifierId() != null)
            throw new IllegalArgumentException("修饰词键错误");
        return true;
    }

    /**
     * @description: 删除修饰词值
     * @param value    修饰词值名称
     * @param parentId 修饰词键id
     * @return {Boolean} true:删除成功 false:删除失败
     * @author: wch
     */
    private Boolean removeModifierValue(String value, int parentId) {
        QueryWrapper<Modifier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("modifier_key_id", parentId);
        queryWrapper.eq("modifier_name", value);
        return remove(queryWrapper);
    }

    /**
     * @description: 根据传入的搜索条件构造数据库查询条件(只找修饰词键)
     * @param wordsQuery 搜索条件
     * @return {QueryWrapper<Modifier>} 数据库查询条件对象
     * @author: wch
     */
    private QueryWrapper<Modifier> getQueryWrapper(WordsQuery wordsQuery) {
        QueryWrapper<Modifier> queryWrapper = new QueryWrapper<>();

        queryWrapper.and(new Consumer<QueryWrapper<Modifier>>() {
            @Override
            public void accept(QueryWrapper<Modifier> modifierQueryWrapper) {
                modifierQueryWrapper.isNull("modifier_key_id");
            }
        });

        if (wordsQuery.getSort() != null && wordsQuery.getKeyword() != null) {
            queryWrapper.and(new Consumer<QueryWrapper<Modifier>>() {
                @Override
                public void accept(QueryWrapper<Modifier> modifierQueryWrapper) {
                    modifierQueryWrapper.like(
                            wordsQuery.getSort().equals("all") || wordsQuery.getSort().equals("modifierName"),
                            "modifier_name", wordsQuery.getKeyword());
                    modifierQueryWrapper.or();
                    modifierQueryWrapper.like(
                            wordsQuery.getSort().equals("all") || wordsQuery.getSort().equals("creator"), "username",
                            wordsQuery.getKeyword());
                }
            });
        }
        return queryWrapper;
    }

    /**
     * @description: 构造修饰词键-修饰词值树形结构
     * @param modifier    修饰词键
     * @param modifierMap 修饰词键-修饰词值映射
     * @param allowParent 是否允许父节点被选中
     * @return {*}
     * @author: wch
     */
    private Map<String, Object> createAllModifierNode(Modifier modifier, Map<Integer, List<Modifier>> modifierMap,
            Boolean allowParent) {
        Map<String, Object> modifierNode = new HashMap<>();
        modifierNode.put("value", modifier.getModifierId());
        modifierNode.put("label", modifier.getModifierName());

        List<Modifier> modifierValues = modifierMap.get(modifier.getModifierId());
        if (modifierValues != null) {
            List<Map<String, Object>> valueNodes = new ArrayList<>();
            for (Modifier modifier_value : modifierValues) {
                Map<String, Object> valueNode = createAllModifierNode(modifier_value, modifierMap, allowParent);
                valueNodes.add(valueNode);
            }
            modifierNode.put("children", valueNodes);

            if (!allowParent)
                modifierNode.put("disabled", true);
        }

        return modifierNode;
    }
}
