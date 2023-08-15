package com.example.nfplus.serviceImpl;

import com.example.nfplus.entity.Version;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VersionServiceImplTest {
    @Test
    void updateVersionTest(){
        Version version = new Version();
        System.out.println(version.getVersionNum());
        version.updateVersion();
        System.out.println(version.getVersionNum());
        version.updateVersion();
        System.out.println(version.getVersionNum());
    }
}