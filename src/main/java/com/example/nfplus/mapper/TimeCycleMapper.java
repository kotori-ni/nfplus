/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 18:15:49
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 10:28:11
 */

package com.example.nfplus.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.nfplus.entity.Indicator;
import com.example.nfplus.entity.TimeCycle;
import com.example.nfplus.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface TimeCycleMapper extends BaseMapper<TimeCycle> {
	/**
	 * @description: 按搜索条件查询时间周期
	 * @param {User}                    user 用户信息
	 * @param {QueryWrapper<TimeCycle>} queryWrapper 查询条件
	 * @return {List<TimeCycle>} 时间周期列表
	 * @author: wch
	 */
	@Select("SELECT t.*, u.username, c.collection_id, " +
			"CASE WHEN c.collection_id IS NOT NULL THEN TRUE ELSE FALSE END AS isCollect " +
			"FROM time_cycles t " +
			"LEFT JOIN users u ON t.creator_id = u.user_id " +
			"LEFT JOIN user_collections c ON #{user.userId} = c.user_id AND t.time_cycle_id = c.time_cycle_id " +
			"${ew.customSqlSegment}")
	@Results({
			@Result(property = "creatorName", column = "username", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "isCollect", column = "isCollect", javaType = Boolean.class, jdbcType = JdbcType.BOOLEAN)
	})
	List<TimeCycle> selectTimeCycles(User user, @Param("ew") QueryWrapper<TimeCycle> queryWrapper);

	/**
	 * @description: 按搜索条件分页查询时间周期
	 * @param {Page<TimeCycle>}         page 分页信息
	 * @param {User}                    user 用户信息
	 * @param {QueryWrapper<TimeCycle>} queryWrapper 查询条件
	 * @return {List<TimeCycle>} 时间周期列表
	 */
	@Select("SELECT t.*, u.username, c.collection_id, " +
			"CASE WHEN c.collection_id IS NOT NULL THEN TRUE ELSE FALSE END AS isCollect " +
			"FROM time_cycles t " +
			"LEFT JOIN users u ON t.creator_id = u.user_id " +
			"LEFT JOIN user_collections c ON #{user.userId} = c.user_id AND t.time_cycle_id = c.time_cycle_id " +
			"${ew.customSqlSegment}")
	@Results({
			@Result(property = "creatorName", column = "username", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "isCollect", column = "isCollect", javaType = Boolean.class, jdbcType = JdbcType.BOOLEAN)
	})
	List<TimeCycle> selectTimeCycles(Page<TimeCycle> page, User user,
			@Param("ew") QueryWrapper<TimeCycle> queryWrapper);

	/**
	 * @description: 查询引用了该时间周期的指标列表
	 * @param {int} id 时间周期id
	 * @return {List<Indicator>} 指标列表
	 * @author: wch
	 */
	@Select("select * from indicators where time_cycle_id = #{id}")
	List<Indicator> selectQuoteIndicators(int id);

	/**
	 * @description: 查询所有时间周期的名称
	 * @return {List<String>} 时间周期名称列表
	 * @author: wch
	 */
	@Select("select time_cycle_name from time_cycles")
	public List<String> getTimeCycleNames();
}
