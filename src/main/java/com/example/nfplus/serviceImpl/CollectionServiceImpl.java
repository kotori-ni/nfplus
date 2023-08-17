/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 20:56:45
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:46:11
 */

package com.example.nfplus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.nfplus.entity.Collection;
import com.example.nfplus.mapper.*;
import com.example.nfplus.service.CollectionService;
import com.example.nfplus.service.ModifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements CollectionService {
    @Autowired
    private ModifierService modifierService;

    /**
     * @description: 验证收藏对象中的信息是否正确
     * @param collection 待验证收藏对象
     * @return {Boolean} true:正确 false:错误
     * @author: wch
     */
    @Override
    public Boolean verifyCollection(Collection collection) throws IllegalArgumentException {
        if (collection.getUserId() == null)
            throw new IllegalArgumentException("缺少用户信息");
        int value_num = (collection.getIndicatorId() != null ? 1 : 0) +
                (collection.getDerivationId() != null ? 1 : 0) +
                (collection.getModifierId() != null ? 1 : 0) +
                (collection.getTimeCycleId() != null ? 1 : 0);
        if (value_num != 1)
            throw new IllegalArgumentException("请求参数不正确");
        if (collection.getModifierId() != null
                && modifierService.getById(collection.getModifierId()).getParentModifierId() != null)
            throw new IllegalArgumentException("只能收藏修饰词键");
        return true;
    }

    /**
     * @description: 查找收藏对象是否存在
     * @param collection 收藏对象
     * @return {Boolean} true:存在 false:不存在
     * @author: wch
     */
    @Override
    public Boolean isCollect(Collection collection) {
        QueryWrapper<Collection> queryWrapper = getQuertWrapper(collection);
        return (getOne(queryWrapper) != null);
    }

    /**
     * @description: 删除收藏对象
     * @param collection 待删除收藏对象
     * @return {Boolean} true:删除成功 false:删除失败(对象不存在或者其他原因导致的删除失败)
     * @author: wch
     */
    @Override
    public Boolean removeCollection(Collection collection) {
        QueryWrapper<Collection> queryWrapper = getQuertWrapper(collection);
        return remove(queryWrapper);
    }

    /**
     * @description: 根据收藏对象获取查询条件
     * @param collection 收藏对象
     * @return {QueryWrapper<Collection>}
     *         查询条件对象QueryWrapper<Collection>，用于验证收藏对象是否存在
     */
    private QueryWrapper<Collection> getQuertWrapper(Collection collection) {
        QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", collection.getUserId());
        if (collection.getIndicatorId() != null)
            queryWrapper.eq("indicator_id", collection.getIndicatorId());
        else if (collection.getDerivationId() != null)
            queryWrapper.eq("derivation_id", collection.getDerivationId());
        else if (collection.getModifierId() != null)
            queryWrapper.eq("modifier_id", collection.getModifierId());
        else
            queryWrapper.eq("time_cycle_id", collection.getTimeCycleId());
        return queryWrapper;
    }
}
