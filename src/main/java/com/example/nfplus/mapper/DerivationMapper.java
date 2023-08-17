/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 18:12:51
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:37:33
 */

package com.example.nfplus.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.nfplus.entity.Derivation;
import com.example.nfplus.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface DerivationMapper extends BaseMapper<Derivation> {
	/**
	 * @description: 按条件查询衍生词
	 * @param user 请求搜索的用户
	 * @param queryWrapper 搜索条件
	 * @return {List<Derivation>} 符合条件的衍生词列表
	 * @author: wch
	 */
	@Select("SELECT d.*, u.username, c.collection_id, " +
			"CASE WHEN c.collection_id IS NOT NULL THEN TRUE ELSE FALSE END AS isCollect " +
			"FROM derivations d " +
			"LEFT JOIN users u ON d.creator_id = u.user_id " +
			"LEFT JOIN user_collections c ON #{user.userId} = c.user_id AND d.derivation_id = c.derivation_id " +
			"${ew.customSqlSegment}")
	@Results({
			@Result(property = "derivationId", column = "derivation_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
			@Result(property = "creatorName", column = "username", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "isCollect", column = "isCollect", javaType = Boolean.class, jdbcType = JdbcType.BOOLEAN),
			@Result(property = "quoteNum", column = "derivation_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER,
					one = @One(select = "com.example.nfplus.mapper.DerivationMapper.selectQuoteIndicatorNum"))
	})
	List<Derivation> selectDerivations(User user, @Param("ew") QueryWrapper<Derivation> queryWrapper);

	/**
	 * @description: 按条件分页查询衍生词
	 * @param page 分页信息
	 * @param user 请求搜索的用户
	 * @param queryWrapper 搜索条件
	 * @return {*}
	 * @author: wch
	 */
	@Select("SELECT d.*, u.username, c.collection_id, " +
			"CASE WHEN c.collection_id IS NOT NULL THEN TRUE ELSE FALSE END AS isCollect " +
			"FROM derivations d " +
			"LEFT JOIN users u ON d.creator_id = u.user_id " +
			"LEFT JOIN user_collections c ON #{user.userId} = c.user_id AND d.derivation_id = c.derivation_id " +
			"${ew.customSqlSegment}")
	@Results({
			@Result(property = "derivationId", column = "derivation_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
			@Result(property = "creatorName", column = "username", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "isCollect", column = "isCollect", javaType = Boolean.class, jdbcType = JdbcType.BOOLEAN),
			@Result(property = "quoteNum", column = "derivation_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER,
					one = @One(select = "com.example.nfplus.mapper.DerivationMapper.selectQuoteIndicatorNum"))
	})
	List<Derivation> selectDerivations(Page<Derivation> page, User user,
			@Param("ew") QueryWrapper<Derivation> queryWrapper);

	/**
	 * @description: 获取所有衍生词名称
	 * @return {List<String>} 衍生词名称列表
	 * @author: wch
	 */
	@Select("select derivation_name from derivations")
	public List<String> getDerivationNames();

	/**
	 * @description: 获取引用该衍生词的指标数量
	 * @param id 衍生词id
	 * @return {Integer} 指标数量
	 * @author: wch
	 */
	@Select("select count(*) from indicator_derivation where derivation_id = #{id}")
	public Integer selectQuoteIndicatorNum(int id);

	/**
	 * @description: 获取引用该衍生词的指标id列表
	 * @param id 衍生词id
	 * @return {List<String>} 指标id列表
	 * @author: wch
	 */
	@Select("select indicator_id from indicator_derivation where derivation_id = #{id}")
	public List<String> selectQuoteIndicators(int id);
}
