package dossier.service;

import dossier.dto.ApplicationStatus;
import dossier.dto.ClientApplication;
import dossier.util.FeignClientDeal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FeignServiceDealTest {
    @MockBean
    FeignClientDeal feignClientDeal;

    ClientApplication clientApplication = ClientApplication.builder()
            .applicationID(20L)
            .applicationStatus(ApplicationStatus.APPROVED)
            .build();

    @Test
    void getClientApplication() {
        Mockito.when(feignClientDeal.getClientApplication(Mockito.any())).thenReturn(clientApplication);
        FeignServiceDeal feignServiceDeal = new FeignServiceDeal(feignClientDeal);
        ClientApplication resClientApplication = feignServiceDeal.getClientApplication(10L);
        Assertions.assertEquals(clientApplication, resClientApplication);
    }

    @Test
    void updateClientApplicationStatus() {
        FeignServiceDeal feignServiceDeal = new FeignServiceDeal(feignClientDeal);
        Assertions.assertDoesNotThrow(() -> feignServiceDeal.updateClientApplicationStatus(20L, ApplicationStatus.CC_DENIED));
    }
}