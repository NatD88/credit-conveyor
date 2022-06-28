package deal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import conveyor.dto.CreditDTO;
import conveyor.dto.LoanApplicationRequestDTO;
import conveyor.dto.LoanOfferDTO;
import conveyor.dto.ScoringDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
class ConveyorServiceTest {

    @Autowired
    public  ConveyorService conveyorService;
    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getLoanOfferDTOList() throws URISyntaxException, JsonProcessingException {

        Assertions.assertThrows(RuntimeException.class, () -> conveyorService.getLoanOfferDTOList(null));

        List<LoanOfferDTO> list = new ArrayList();
        LoanOfferDTO loanOfferDTO = LoanOfferDTO.builder()
                .rate(new BigDecimal(15))
                .build();
        list.add(loanOfferDTO);

        mockServer.expect(
                        requestTo(new URI("http://localhost:8080/conveyor/offers")))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(list)));

        ResponseEntity<List<LoanOfferDTO>> responseRes = conveyorService.getLoanOfferDTOList(new LoanApplicationRequestDTO());
        mockServer.verify();
        Assertions.assertEquals(list.get(0).getRate(), responseRes.getBody().get(0).getRate());
    }

    @Test
    void getCreditDTO() throws URISyntaxException, JsonProcessingException {
        Assertions.assertThrows(RuntimeException.class, () -> conveyorService.getCreditDTO(null, 1L));

        CreditDTO creditDTO= CreditDTO.builder()
                .rate(new BigDecimal(20))
                .build();

        mockServer.expect(
                        requestTo(new URI("http://localhost:8080/conveyor/calculation")))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(creditDTO)));

        ResponseEntity<CreditDTO> responseRes = conveyorService.getCreditDTO(new ScoringDataDTO(), 1L);
        mockServer.verify();
        Assertions.assertEquals(creditDTO.getRate(), responseRes.getBody().getRate());
    }
}