/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:36:05
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 11:17:14
 */
package com.example.nfplus.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.nfplus.entity.Indicator;
import com.example.nfplus.entity.IndicatorQuery;
import com.example.nfplus.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface IndicatorService extends IService<Indicator> {
    /**
     * @description: 获取指标详细信息
     * @param {User} user 请求查询指标的用户
     * @param {String} indicatorId 指标id
     * @return {Indicator} 指标信息
     * @author: wch
     */    
    public Indicator getOneIndicator(User user, String indicatorId);

    /**
     * @description: 获取浏览量最高的五个指标
     * @return {List<Indicator>} 指标列表
     * @author: wch
     */    
    public List<Indicator> getViewMaxIndicators();

    /**
     * @description: 获取用户收藏的所有指标
     * @param {User} user 请求查询的用户
     * @return {List<Indicator>} 指标列表
     * @author: wch
     */    
    public List<Indicator> getUserFavourIndicators(User user);

    /**
     * @description: 分页按搜索条件获取用户收藏的指标
     * @param {User} user 请求查询的用户
     * @param {IndicatorQuery} indicatorQuery 分页查询条件
     * @return {Page<Indicator>} 指标分页列表
     * @author: wch
     */    
    public Page<Indicator> getUserFavourIndicatorsWithPage(User user, IndicatorQuery indicatorQuery);

    /**
     * @description: 获取指标的所有创建者
     * @return {List<User>} 指标创建者列表
     * @author: wch
     */
    public List<User> getIndicatorCreators();

    /**
     * @description: 获取指标的血缘树结点信息与边信息
     * @param {String} indicatorId 指标id
     * @return {Map<String, Object>} 指标血缘树结点信息与边信息 {nodes: [], edges: []} nodes: 结点信息, edges: 边信息
     * @author: wch
     */    
    public Map<String, Object> getIndicatorTreeById(String indicatorId);

    /**
     * @description: 按搜索条件查询指标
     * @param {User} user 请求查询的用户
     * @param {IndicatorQuery} indicatorQuery 指标查询条件
     * @return {List<Indicator>} 指标列表
     * @author: wch
     */    
    public List<Indicator> getIndicators(User user, IndicatorQuery indicatorQuery);

    /**
     * @description: 分页按搜索条件查询指标
     * @param {User} user 请求查询的用户
     * @param {IndicatorQuery} indicatorQuery 指标查询条件
     * @return {Page<Indicator>} 指标分页列表
     * @author: wch
     */    
    public Page<Indicator> getIndicatorsWithPage(User user, IndicatorQuery indicatorQuery);

    /**
     * @description: 新增指标
     * @param {User} user 请求新增指标的用户
     * @param {Indicator} indicator 新增的指标信息
     * @return {Boolean} 新增成功则返回true,否则抛出异常
     * @author: wch
     */    
    public Boolean saveIndicator(User user, Indicator indicator);

    /**
     * @description: 更新指标信息(不包括指标状态)
     * @param {User} user 请求更新指标的用户
     * @param {Indicator} indicator 更新的指标信息
     * @return {Boolean} 更新成功则返回true,否则抛出异常
     * @author: wch
     */
    public Boolean updateIndicator(User user, Indicator indicator);

    /**
     * @description: 更新指标状态
     * @param {User} user 请求更新指标状态的用户
     * @param {int} newState 新的指标状态
     * @param {Indicator} indicator 待更新的指标
     * @return {Boolean} 更新成功则返回true,否则抛出异常
     * @author: wch
     */    
    public Boolean updateIndicatorState(User user, int newState, Indicator indicator);

    /**
     * @description: 验证指标中的信息是否合法
     * @param {Indicator} indicator 待验证的指标
     * @return {Boolean} 验证成功则返回true,否则抛出异常
     * @author: wch
     */    
    public Boolean verifyIndicator(Indicator indicator);

}
