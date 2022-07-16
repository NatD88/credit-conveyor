package deal.service;

import deal.dto.CreditDTO;
import deal.dto.LoanApplicationRequestDTO;
import deal.dto.LoanOfferDTO;
import deal.dto.ScoringDataDTO;
import deal.util.FeignClientConveyor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest()
class FeignServiceConveyorTest {

    @Autowired
    private FeignServiceConveyor feignServiceConveyor;

    @MockBean
    FeignClientConveyor feignClientConveyor;

    @Mock
    LoanApplicationRequestDTO loanApplicationRequestDTO;

    @Mock
    ScoringDataDTO scoringDataDTO;

    @Test
    void getLoanOffers() {

        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        List<LoanOfferDTO> list = new ArrayList();
        for(int i = 0; i < 4 ; i++) {
            list.add(loanOfferDTO);
        }

        Mockito.when(feignClientConveyor.getLoanOffers(Mockito.any())).thenReturn(list);

        List<LoanOfferDTO> respList = feignServiceConveyor.getLoanOffers(loanApplicationRequestDTO);
        Assertions.assertEquals(list.size(), respList.size());
    }

    @Test
    void getCreditDTO() {

        CreditDTO creditDTO = CreditDTO.builder()
                .term(51)
                .build();
        Mockito.when(feignClientConveyor.getCreditDTO(Mockito.any())).thenReturn(creditDTO);
        CreditDTO respCreditDTO = feignServiceConveyor.getCreditDTO(scoringDataDTO, 5L);
        Assertions.assertEquals(creditDTO.getTerm(), respCreditDTO.getTerm());
    }
}