package com.ita.if103java.ims;

import com.ita.if103java.ims.dto.warehouse.advice.WarehouseItemAdviceDto;
import com.ita.if103java.ims.service.WarehouseAdvisorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ImsApplicationTests {

    @Autowired
    private WarehouseAdvisorService service;

    @Test
    void contextLoads() {
        final WarehouseItemAdviceDto advice = service.getAdvice(2L, 1L);
        System.out.println(advice);
    }

}
