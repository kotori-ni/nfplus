/*
 * @Description: 指标工具类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:16:53
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 11:23:45
 */


package com.example.nfplus.utils;

import java.util.HashMap;
import java.util.Map;

import com.example.nfplus.entity.Indicator;

public class IndicatorUtils {

    private final static Map<Integer, String> state_map = new HashMap<>();
    private final static Map<Integer, String> type_map = new HashMap<>();

    static {
        state_map.put(Indicator.STATE_NEW, "新建");
        state_map.put(Indicator.STATE_DRAFT, "草稿");
        state_map.put(Indicator.STATE_ONLINE, "已发布");
        state_map.put(Indicator.STATE_OFFLINE, "已下线");

        type_map.put(Indicator.TYPE_ATOMIC, "主原子指标");
        type_map.put(Indicator.TYPE_DERIVATION, "衍生原子指标");
        type_map.put(Indicator.TYPE_MODIFIER, "派生指标");
        type_map.put(Indicator.TYPE_COMPOSITE, "复合指标");
    }

    //根据指标状态获取状态名称
    public static String getStateName(int state_id){
        return state_map.get(state_id);
    }

    //根据指标类型获取类型名称
    public static String getTypeName(int type_id){
        return type_map.get(type_id);
    }
}
