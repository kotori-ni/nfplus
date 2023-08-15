package com.example.nfplus.serviceImpl;

import com.example.nfplus.entity.Domain;
import com.example.nfplus.mapper.DomainMapper;
import com.example.nfplus.service.DomainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DomainServiceImplTest {
    @Autowired
    private DomainService domainService;

    @Test
    public void getDomainTest(){
        List<Domain> domains = domainService.list();
        for (Domain domain : domains){
            System.out.println(domain);
        }
    }
}