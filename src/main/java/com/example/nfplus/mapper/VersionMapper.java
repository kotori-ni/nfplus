/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 18:13:10
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:41:55
 */

package com.example.nfplus.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.nfplus.entity.Version;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface VersionMapper extends BaseMapper<Version> {
	/**
	 * @description: 查询指标的最新版本
	 * @param id 指标id
	 * @return {Version} 最新版本信息
	 * @author: wch
	 */
	@Select("select * from versions where indicator_id=#{id} order by operate_time desc limit 1")
	public Version findLastVersionByIndicatorId(String id);

	/**
	 * @description: 按搜索条件查询指标的版本信息
	 * @param queryWrapper 查询条件
	 * @return {List<Version>} 指标版本信息列表
	 * @author: wch
	 */
	@Select("SELECT v.*,  operator.username " +
			"FROM versions v " +
			"LEFT JOIN users operator ON v.operator_id = operator.user_id " +
			"${ew.customSqlSegment}")
	@Results({
			@Result(property = "operatorName", column = "username", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	})
	public List<Version> selectIndicatorVersions(@Param("ew") QueryWrapper<Version> queryWrapper);
}
