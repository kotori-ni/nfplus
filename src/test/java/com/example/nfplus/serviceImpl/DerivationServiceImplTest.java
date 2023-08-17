package com.example.nfplus.serviceImpl;

import com.example.nfplus.entity.Derivation;
import com.example.nfplus.entity.WordsQuery;
import com.example.nfplus.mapper.DerivationMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DerivationServiceImplTest {
    @Autowired
    private DerivationServiceImpl derivationService;
    @Autowired
    private DerivationMapper derivationMapper;

    @Test
    public void getDerivationsTest(){
        Derivation obj = new Derivation();
        obj.setDerivationName("affaf");
        Derivation derivation =derivationService.query().eq("derivation_name", obj.getDerivationName()).one();
        System.out.println(derivation);
    }

    @Test
    public void getDerivationsWithPageTest(){
        WordsQuery wordsQuery = new WordsQuery();
        wordsQuery.setSort("all");
        wordsQuery.setKeyword("wc");

//        QueryWrapper queryWrapper = derivationService.getQueryWrapper(wordsQuery);
//        Page<Derivation> page = new Page<>(1,10);
    }

    @Test
    public void findQuoteIndicatorsTest(){
        List<String> indicatorIds = derivationMapper.selectQuoteIndicators(7);
        System.out.println(indicatorIds.size() == 0);
    }

}