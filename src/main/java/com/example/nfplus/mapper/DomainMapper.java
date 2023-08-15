/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:37:47
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 09:36:16
 */

package com.example.nfplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.nfplus.entity.Domain;
import com.example.nfplus.entity.Indicator;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface DomainMapper extends BaseMapper<Domain> {
	/**
	 * @description: 获取所有指标域(带子指标域数量,指标数量信息)
	 * @return {List<Domain>} 指标域列表
	 * @author: wch
	 */
	@Select("select d.*, u.username from domains d " +
			"left join users u on d.creator_id = u.user_id")
	@Results({
			@Result(property = "domainId", column = "domain_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
			@Result(property = "creatorName", column = "username", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "indicatorNum", column = "domain_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER, one = @One(select = "com.example.nfplus.mapper.DomainMapper.getDomainIndicatorNum")),
			@Result(property = "childNum", column = "domain_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER, one = @One(select = "com.example.nfplus.mapper.DomainMapper.getDomainChildNum")),
	})
	public List<Domain> getAllDomain();

	/**
	 * @description: 获取指标域中的所有指标
	 * @param {int} id 指标域id
	 * @return {List<Indicator>} 指标列表
	 * @author: wch
	 */
	@Select("select * from indicators where domain_id = #{id}")
	public List<Indicator> selectQuoteIndicators(int id);

	/**
	 * @description: 获取指标域中的指标数量
	 * @param {int} id 指标域id
	 * @return {Integer} 指标数量信息
	 * @author: wch
	 */
	@Select("select count(*) from indicators where domain_id = #{id}")
	public Integer getDomainIndicatorNum(int id);

	/**
	 * @description: 获取指标域中的子指标域数量
	 * @param {int} id 指标域id
	 * @return {Integer} 子指标域数量信息
	 * @author: wch
	 */
	@Select("select count(*) from domains where parent_domain_id = #{id}")
	public Integer getDomainChildNum(int id);
}
