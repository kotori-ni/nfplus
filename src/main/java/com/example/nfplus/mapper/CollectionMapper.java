/*
 * @Description:
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 18:13:35
 * @LastEditors: wch
 * @LastEditTime: 2023-08-14 17:02:57
 */


package com.example.nfplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.nfplus.entity.Collection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CollectionMapper extends BaseMapper<Collection> {
    /**
     * @description: 根据指标id查询指标收藏数量
     * @param id 指标id
     * @return {Integer} 指标的收藏数量
     * @author: wch
     */    
    @Select("select count(*) from user_collections where indicator_id = #{id}")
    public Integer findFavourNumByIndicatorId(String id);
}
