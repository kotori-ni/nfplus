/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 18:18:47
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:45:42
 */
package com.example.nfplus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.nfplus.entity.Indicator;
import com.example.nfplus.entity.User;
import com.example.nfplus.entity.Version;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VersionService extends IService<Version> {
    /**
     * @description: 查找指标的所有版本
     * @param user        请求查询的用户
     * @param indicatorId 指标id
     * @return {List<Version>} 指标版本列表,按时间倒序排列
     * @author: wch
     */
    public List<Version> getIndicatorVersions(User user, String indicatorId);

    /**
     * @description: 根据指标修改前后的信息,生成新指标版本
     * @param oldIndicator 修改前的指标
     * @param newIndicator 修改后的指标
     * @return {Version} 新指标版本
     * @author: wch
     */
    public Version getVersionInfo(Indicator oldIndicator, Indicator newIndicator);
}
