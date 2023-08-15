/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 18:15:11
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 10:22:20
 */

package com.example.nfplus.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.nfplus.entity.Modifier;
import com.example.nfplus.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface ModifierMapper extends BaseMapper<Modifier> {
	/**
	 * @description: 按搜索条件查询修饰词
	 * @param {User}                   user
	 * @param {QueryWrapper<Modifier>} queryWrapper 查询条件
	 * @return {List<Modifier>} 修饰词列表
	 * @author: wch
	 */
	@Select("SELECT m.*, u.username, c.collection_id, " +
			"CASE WHEN c.collection_id IS NOT NULL THEN TRUE ELSE FALSE END AS isCollect " +
			"FROM modifiers m " +
			"LEFT JOIN users u ON m.creator_id = u.user_id " +
			"LEFT JOIN user_collections c ON #{user.userId} = c.user_id AND m.modifier_id = c.modifier_id " +
			"${ew.customSqlSegment}")
	@Results({
			@Result(property = "modifierId", column = "modifier_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
			@Result(property = "creatorName", column = "username", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "isCollect", column = "isCollect", javaType = Boolean.class, jdbcType = JdbcType.BOOLEAN),
			@Result(property = "modifierValues", column = "modifier_id", javaType = List.class, jdbcType = JdbcType.VARCHAR, many = @Many(select = "com.example.nfplus.mapper.ModifierMapper.selectModifierByKeyId"))
	})
	List<Modifier> selectModifiers(User user, @Param("ew") QueryWrapper<Modifier> queryWrapper);

	/**
	 * @description: 按搜索条件分页查询修饰词
	 * @param {Page<Modifier>}         page 分页信息
	 * @param {User}                   user
	 * @param {QueryWrapper<Modifier>} queryWrapper 查询条件
	 * @return {List<Modifier>} 修饰词列表
	 * @author: wch
	 */
	@Select("SELECT m.*, u.username, c.collection_id, " +
			"CASE WHEN c.collection_id IS NOT NULL THEN TRUE ELSE FALSE END AS isCollect " +
			"FROM modifiers m " +
			"LEFT JOIN users u ON m.creator_id = u.user_id " +
			"LEFT JOIN user_collections c ON #{user.userId} = c.user_id AND m.modifier_id = c.modifier_id " +
			"${ew.customSqlSegment}")
	@Results({
			@Result(property = "modifierId", column = "modifier_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
			@Result(property = "creatorName", column = "username", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "isCollect", column = "isCollect", javaType = Boolean.class, jdbcType = JdbcType.BOOLEAN),
			@Result(property = "modifierValues", column = "modifier_id", javaType = List.class, jdbcType = JdbcType.VARCHAR, many = @Many(select = "com.example.nfplus.mapper.ModifierMapper.selectModifierByKeyId"))
	})
	List<Modifier> selectModifiers(Page<Modifier> page, User user, @Param("ew") QueryWrapper<Modifier> queryWrapper);

	/**
	 * @description: 查询修饰词键的所有修饰词值
	 * @param {int} id 修饰词键id
	 * @return {List<Modifier>} 修饰词值列表
	 * @author: wch
	 */
	@Select("select * from modifiers where modifier_key_id = #{id}")
	List<Modifier> selectModifierByKeyId(int id);

	/**
	 * @description: 查询修饰词键的所有修饰词值名称
	 * @param {int} id 修饰词键id
	 * @return {List<String>} 修饰词值名称列表
	 * @author: wch
	 */
	@Select("select modifier_name from modifiers where modifier_key_id = #{id}")
	List<String> getModifierValueNameByKeyId(int id);

	/**
	 * @description: 查询引用了该修饰词的指标id列表
	 * @param {int} id 修饰词id
	 * @return {List<String>} 指标id列表
	 * @author: wch
	 */
	@Select("select indicator_id from indicator_modifier where modifier_id = #{id}")
	List<String> selectQuoteIndicators(int id);
}
