package com.example.nfplus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.nfplus.entity.Indicator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IndicatorServiceImplTest {
    @Test
    public void selectIndicatorsTest(){
        QueryWrapper<Indicator> queryWrapper = new QueryWrapper<>();
        System.out.println(queryWrapper.getCustomSqlSegment());
        System.out.println(queryWrapper.getSqlSegment());

        queryWrapper.eq("indicator_type", 4);
        System.out.println(queryWrapper.getCustomSqlSegment());
        System.out.println(queryWrapper.getSqlSegment());
    }
}