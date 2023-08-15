package com.example.nfplus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.nfplus.mapper")
public class NfplusApplication {

    public static void main(String[] args) {
        SpringApplication.run(NfplusApplication.class, args);
    }

}
