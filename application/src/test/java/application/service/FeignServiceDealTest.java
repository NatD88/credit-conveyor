package application.service;

import application.dto.LoanApplicationRequestDTO;
import application.dto.LoanOfferDTO;
import application.util.FeignClientDeal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FeignServiceDealTest {

    @MockBean
    FeignClientDeal feignClientDeal;

    LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO(new BigDecimal(15000),
            6,
            "Max",
            "Ivanov",
            "jytjt",
            "fhwehi@mail.ru",
            LocalDate.of(1991, 11, 5),
            "1245",
            "123456");

    LoanOfferDTO loanOfferDTO = new LoanOfferDTO(1L,
            new BigDecimal(100000),
            new BigDecimal(130000),
            0,
            new BigDecimal(15000),
            new BigDecimal(12),
            true,
            true);


    @Test
    void getLoanOffers() {
        List<LoanOfferDTO> list = new ArrayList<>();
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        list.add(loanOfferDTO);

        Mockito.when(feignClientDeal.getLoanOffers(Mockito.any())).thenReturn(list);
        FeignServiceDeal feignServiceDeal = new FeignServiceDeal(feignClientDeal);
        List<LoanOfferDTO> resList = feignServiceDeal.getLoanOffers(loanApplicationRequestDTO);
        Assertions.assertEquals(list, resList);
    }

    @Test
    void sendLoanOffer() {
        FeignServiceDeal feignServiceDeal = new FeignServiceDeal(feignClientDeal);
        Assertions.assertDoesNotThrow(() -> feignServiceDeal.sendLoanOffer(loanOfferDTO));
    }
}