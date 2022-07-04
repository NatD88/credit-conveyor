package deal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import conveyor.dto.CreditDTO;
import conveyor.dto.LoanOfferDTO;
import deal.util.FeignClientConveyor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @MockBean
    FeignClientConveyor feignClientConveyor;

    @Test
    void getLoanOffers() {

        ResponseEntity<List<LoanOfferDTO>> respList = new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);

        Mockito.when(feignClientConveyor.getLoanOffers(Mockito.any())).thenReturn(respList);
        Assertions.assertEquals(HttpStatus.OK, respList.getStatusCode());
    }

    @Test
    void getCreditDTO() {

        ResponseEntity<CreditDTO> response = new ResponseEntity<>(new CreditDTO(), HttpStatus.OK);
        Mockito.when(feignClientConveyor.getCreditDTO(Mockito.any())).thenReturn(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}