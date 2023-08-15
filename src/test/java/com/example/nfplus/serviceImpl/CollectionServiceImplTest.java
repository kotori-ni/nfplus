package com.example.nfplus.serviceImpl;

import com.example.nfplus.entity.Collection;
import com.example.nfplus.service.CollectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CollectionServiceImplTest {
    @Autowired
    private CollectionService collectionService;

    @Test
    void verifyCollection() {
        Collection collection = new Collection();
        collection.setUserId(1);
        collection.setIndicatorId("atomic_indicator_2");

        try {
            collectionService.verifyCollection(collection);
            System.out.println("-----------success-------------");
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            System.out.println(collection.getIndicatorId() != null);
            System.out.println(collection.getDerivationId() != null);
            System.out.println(collection.getModifierId() != null);
            System.out.println(collection.getTimeCycleId() != null);

            int value_num = (collection.getIndicatorId() != null ? 1 : 0) +
                            (collection.getDerivationId() != null ? 1 : 0) +
                            (collection.getModifierId() != null ? 1 : 0) +
                            (collection.getTimeCycleId()  != null ? 1 : 0);
            System.out.println(value_num);
        }
    }
}