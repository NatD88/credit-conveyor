package deal.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import conveyor.dto.*;
import deal.dto.FinishRegistrationRequestDTO;
import deal.entity.ClientApplication;
import deal.service.DealService;
import deal.service.FeignServiceConveyor;
import deal.util.ApplicationNotFoundException;
import deal.util.BadRequestException;
import deal.util.RejectScoringDealException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DealController.class)

class DealControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    private DealService dealService;
    @MockBean
    private FeignServiceConveyor feignServiceConveyor;

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

    FinishRegistrationRequestDTO finishRegistrationRequestDTO = new FinishRegistrationRequestDTO(Gender.MALE,
            MaritalStatus.MARRIED,
            1,
            LocalDate.of(1990, 10, 10),
            "Branch...",
            new EmploymentDTO(),
            "123456789456415123");

    @Test
    void getPossibleCreditConditions() throws Exception {

        List<LoanOfferDTO> loanOfferDTOS = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            loanOfferDTOS.add(loanOfferDTO);
        }
        ResponseEntity<List<LoanOfferDTO>> responseEntity = new ResponseEntity<>(loanOfferDTOS, HttpStatus.OK);

        Mockito.when(feignServiceConveyor.getLoanOffers(Mockito.any())).thenReturn(responseEntity);

        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        post("/deal/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(loanApplicationRequestDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());

        Mockito.verify(feignServiceConveyor, Mockito.times(1)).getLoanOffers(Mockito.any());
    }

    @Test
    void processingLoanOfferDTO() throws Exception {
        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        put("/deal/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(loanOfferDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void processingFinishRegistrationRequestDTO() throws Exception {

        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        ClientApplication clientApplication = new ClientApplication();
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();

        Mockito.when(dealService.updateDB(Mockito.any(), Mockito.eq(55L))).thenReturn(clientApplication);
        Mockito.when(dealService.createScoringDataDTO(Mockito.any())).thenReturn(scoringDataDTO);

        ResponseEntity<CreditDTO> responseEntity = new ResponseEntity<>(new CreditDTO(), HttpStatus.OK);
        Mockito.when(feignServiceConveyor.getCreditDTO(Mockito.any(), Mockito.any())).thenReturn(responseEntity);

        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        put("/deal/calculate/55")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(finishRegistrationRequestDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void handleBadRequestException() {
        DealController dealController = new DealController(dealService, feignServiceConveyor);
        Map<Object, Object> testMap = new HashMap<>();
        testMap.put("passport", "incorrect passport value");
        ResponseEntity<Object> response = dealController.handleBadRequestException(new BadRequestException(testMap));
        assertEquals((Map) testMap, response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleApplicationNotFoundException() {
        DealController dealController = new DealController(dealService, feignServiceConveyor);
        ResponseEntity<Object> response = dealController.handleApplicationNotFoundException(new ApplicationNotFoundException(55L));
        assertEquals("Заявка с номером 55 не найдена в базе!", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test

    void handleRejectScoringDealException() {
        DealController dealController = new DealController(dealService, feignServiceConveyor);
        ResponseEntity<String> response = dealController.handleRejectScoringDealException(new RejectScoringDealException("\"Отказано в выдаче кредита!!\"", 1L));
        assertEquals("Отказано в выдаче кредита!!", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}