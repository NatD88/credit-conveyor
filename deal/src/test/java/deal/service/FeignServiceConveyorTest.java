package deal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import conveyor.dto.CreditDTO;
import conveyor.dto.LoanApplicationRequestDTO;
import conveyor.dto.LoanOfferDTO;
import conveyor.dto.ScoringDataDTO;
import deal.util.FeignClientConveyor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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