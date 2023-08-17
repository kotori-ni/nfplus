/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 18:14:07
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:41:33
 */

package com.example.nfplus.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.nfplus.entity.Indicator;
import com.example.nfplus.entity.User;
import com.example.nfplus.entity.Version;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface IndicatorMapper extends BaseMapper<Indicator> {
	/**
	 * @description: 按搜索条件不分页查询指标
	 * @param user         请求查询的用户
	 * @param queryWrapper 搜索条件
	 * @return {List<Indicator>} 指标列表
	 * @author: wch
	 */
	@Select("SELECT i.*, creator.username creator_name, c.collection_id, d.domain_name, operator.username operator_name, de.indicator_name dependent_indicator_name, t.time_cycle_name, "
			+
			"CASE WHEN c.collection_id IS NOT NULL THEN TRUE ELSE FALSE END AS isCollect " +
			"FROM indicators i " +
			"LEFT JOIN users creator ON i.creator_id = creator.user_id " +
			"LEFT JOIN domains d ON d.domain_id = i.domain_id " +
			"LEFT JOIN users operator ON i.last_operator_id = operator.user_id " +
			"LEFT JOIN time_cycles t ON i.time_cycle_id = t.time_cycle_id " +
			"LEFT JOIN indicators de ON i.dependent_indicator_id = de.indicator_id " +
			"LEFT JOIN user_collections c ON #{user.userId} = c.user_id AND i.indicator_id = c.indicator_id " +
			"${ew.customSqlSegment}")
	@Results({
			@Result(property = "indicatorId", column = "indicator_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "isCollect", column = "isCollect", javaType = Boolean.class, jdbcType = JdbcType.BOOLEAN),
			@Result(property = "creatorName", column = "creator_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "lastOperatorName", column = "operator_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "domainName", column = "domain_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "timeCycleName", column = "time_cycle_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "dependentIndicatorName", column = "dependent_indicator_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "version", column = "indicator_id", javaType = Version.class, jdbcType = JdbcType.VARCHAR, one = @One(select = "com.example.nfplus.mapper.VersionMapper.findLastVersionByIndicatorId"))
	})
	List<Indicator> selectIndicators(User user, @Param("ew") QueryWrapper<Indicator> queryWrapper);

	/**
	 * @description: 按搜索条件分页查询指标
	 * @param page         分页信息
	 * @param user         请求查询的用户
	 * @param queryWrapper 搜索条件
	 * @return {List<Indicator>} 指标列表
	 * @author: wch
	 */
	// 条件分页查询指标
	@Select("SELECT i.*, creator.username creator_name, c.collection_id, d.domain_name, operator.username operator_name, de.indicator_name dependent_indicator_name, t.time_cycle_name, "
			+
			"CASE WHEN c.collection_id IS NOT NULL THEN TRUE ELSE FALSE END AS isCollect " +
			"FROM indicators i " +
			"LEFT JOIN users creator ON i.creator_id = creator.user_id " +
			"LEFT JOIN domains d ON d.domain_id = i.domain_id " +
			"LEFT JOIN users operator ON i.last_operator_id = operator.user_id " +
			"LEFT JOIN time_cycles t ON i.time_cycle_id = t.time_cycle_id " +
			"LEFT JOIN indicators de ON i.dependent_indicator_id = de.indicator_id " +
			"LEFT JOIN user_collections c ON #{user.userId} = c.user_id AND i.indicator_id = c.indicator_id " +
			"${ew.customSqlSegment}")
	@Results({
			@Result(property = "indicatorId", column = "indicator_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "isCollect", column = "isCollect", javaType = Boolean.class, jdbcType = JdbcType.BOOLEAN),
			@Result(property = "creatorName", column = "creator_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "lastOperatorName", column = "operator_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "domainName", column = "domain_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "timeCycleName", column = "time_cycle_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "dependentIndicatorName", column = "dependent_indicator_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "version", column = "indicator_id", javaType = Version.class, jdbcType = JdbcType.VARCHAR, one = @One(select = "com.example.nfplus.mapper.VersionMapper.findLastVersionByIndicatorId"))
	})
	List<Indicator> selectIndicators(Page<Indicator> page, User user,
			@Param("ew") QueryWrapper<Indicator> queryWrapper);

	/**
	 * @description: 按指标id查询指标
	 * @param user        请求查询的用户
	 * @param id 指标id
	 * @return {Indicator} 指标信息
	 * @author: wch
	 */
	@Select("SELECT i.*, creator.username creator_name, c.collection_id, d.domain_name, operator.username operator_name, de.indicator_name dependent_indicator_name, t.time_cycle_name, "
			+
			"CASE WHEN c.collection_id IS NOT NULL THEN TRUE ELSE FALSE END AS isCollect " +
			"FROM indicators i " +
			"LEFT JOIN users creator ON i.creator_id = creator.user_id " +
			"LEFT JOIN domains d ON d.domain_id = i.domain_id " +
			"LEFT JOIN users operator ON i.last_operator_id = operator.user_id " +
			"LEFT JOIN time_cycles t ON i.time_cycle_id = t.time_cycle_id " +
			"LEFT JOIN indicators de ON i.dependent_indicator_id = de.indicator_id " +
			"LEFT JOIN user_collections c ON #{user.userId} = c.user_id AND i.indicator_id = c.indicator_id " +
			"where i.indicator_id = #{id}")
	@Results({
			@Result(property = "indicatorId", column = "indicator_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "isCollect", column = "isCollect", javaType = Boolean.class, jdbcType = JdbcType.BOOLEAN),
			@Result(property = "creatorName", column = "creator_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "lastOperatorName", column = "operator_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "domainName", column = "domain_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "timeCycleName", column = "time_cycle_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "dependentIndicatorName", column = "dependent_indicator_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "version", column = "indicator_id", javaType = Version.class, jdbcType = JdbcType.VARCHAR, one = @One(select = "com.example.nfplus.mapper.VersionMapper.findLastVersionByIndicatorId")),
			@Result(property = "favoursNum", column = "indicator_id", javaType = Integer.class, jdbcType = JdbcType.VARCHAR, one = @One(select = "com.example.nfplus.mapper.CollectionMapper.findFavourNumByIndicatorId"))
	})
	Indicator selectOneIndicator(User user, String id);

	/**
	 * @description: 查找所有指标创建者
	 * @return {List<User>} 指标创建者列表
	 * @author: wch
	 */
	@Select("SELECT DISTINCT u.* FROM users u JOIN indicators i ON i.creator_id = u.user_id")
	public List<User> selectIndicatorCreators();

	/**
	 * @description: 查找指标引用的所有衍生词
	 * @param id 指标id
	 * @return {List<Integer>} 衍生词id列表
	 * @author: wch
	 */
	@Select("select derivation_id from indicator_derivation where indicator_id = #{id}")
	public List<Integer> getIndicatorDerivations(String id);

	/**
	 * @description: 查找指标引用的所有修饰词
	 * @param id 指标id
	 * @return {List<Integer>} 修饰词id列表
	 * @author: wch
	 */
	@Select("select modifier_id from indicator_modifier where indicator_id = #{id}")
	public List<Integer> getIndicatorModifiers(String id);

	/**
	 * @description: 查找复合指标中所有参与运算的指标列表
	 * @param id 复合指标id
	 * @return {List<String>} 参与运算的指标id列表
	 * @author: wch
	 */
	@Select("select composite_indicator_id from indicator_composite_indicator where indicator_id = #{id}")
	public List<String> getOtherIndicatorsByCompositeIndicatorId(String id);

	/**
	 * @description: 查找引用了该指标作为运算指标的复合指标列表
	 * @param id 指标id
	 * @return {List<String>} 复合指标id列表
	 * @author: wch
	 */
	@Select("select indicator_id from indicator_composite_indicator where composite_indicator_id = #{id}")
	public List<String> getCompositeIndicatorIdByOtherIndicator(String id);

	/**
	 * @description: 添加指标衍生词信息
	 * @param indicator_id  指标id
	 * @param derivation_id 衍生词id
	 * @return {*}
	 * @author: wch
	 */
	@Insert("insert into indicator_derivation value (#{indicator_id}, #{derivation_id})")
	public void insertIndicatorDerivation(String indicator_id, int derivation_id);

	/**
	 * @description: 添加指标修饰词信息
	 * @param indicator_id 指标id
	 * @param modifier_id  修饰词id
	 * @return {*}
	 * @author: wch
	 */
	@Insert("insert into indicator_modifier value (#{indicator_id}, #{modifier_id})")
	public void insertIndicatorModifier(String indicator_id, int modifier_id);

	/**
	 * @description: 添加复合指标中参与运算的指标信息
	 * @param indicator_id 复合指标id
	 * @param composite_id 参与运算的指标id
	 * @return {*}
	 * @author: wch
	 */
	@Insert("insert into indicator_composite_indicator value (#{indicator_id}, #{composite_id})")
	public void insertCompositeIndicator(String indicator_id, String composite_id);

	/**
	 * @description: 删除指标所有衍生词信息
	 * @param indicator_id 指标id
	 * @return {*}
	 * @author: wch
	 */
	@Delete("delete from indicator_derivation where indicator_id = #{indicator_id}")
	public void deleteIndicatorDerivation(String indicator_id);

	/**
	 * @description: 删除指标所有修饰词信息
	 * @param indicator_id 指标id
	 * @return {*}
	 */
	@Delete("delete from indicator_modifier where indicator_id = #{indicator_id}")
	public void deleteIndicatorModifier(String indicator_id);

	/**
	 * @description: 删除复合指标中所有参与运算的指标信息
	 * @param indicator_id 复合指标id
	 * @return {*}
	 */
	@Delete("delete from indicator_composite_indicator where indicator_id = #{indicator_id}")
	public void deleteCompositeIndicator(String indicator_id);
}
