package deal.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import deal.dto.*;
import deal.entity.ClientApplication;
import deal.service.DealService;
import deal.service.FeignServiceConveyor;
import deal.service.KafkaSendService;
import deal.util.ApplicationNotFoundException;
import deal.util.ApplicationStatus;
import deal.util.BadRequestException;
import deal.util.RejectScoringDealException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
    @MockBean
    private KafkaSendService kafkaSendService;

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

        Mockito.when(feignServiceConveyor.getLoanOffers(Mockito.any())).thenReturn(loanOfferDTOS);

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

        Mockito.when(feignServiceConveyor.getCreditDTO(Mockito.any(), Mockito.any())).thenReturn(new CreditDTO());

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
        DealController dealController = new DealController(dealService, feignServiceConveyor, kafkaSendService);
        Map<Object, Object> testMap = new HashMap<>();
        testMap.put("passport", "incorrect passport value");
        ResponseEntity<Object> response = dealController.handleBadRequestException(new BadRequestException(testMap));
        assertEquals((Map) testMap, response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleApplicationNotFoundException() {
        DealController dealController = new DealController(dealService, feignServiceConveyor, kafkaSendService);
        ResponseEntity<String> response = dealController.handleApplicationNotFoundException(new ApplicationNotFoundException(55L));
        assertEquals("Заявка с номером 55 не найдена в базе!", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test

    void handleRejectScoringDealException() {
        DealController dealController = new DealController(dealService, feignServiceConveyor, kafkaSendService);
        String strResponse = dealController.handleRejectScoringDealException(new RejectScoringDealException("Отказано в выдаче кредита!!", 1L));
        assertEquals("Отказано в выдаче кредита!!", strResponse);
    }

    @Test
    void sendDocuments() throws Exception {
        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        post("/deal/document/55/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());

    }

    @Test
    void signRequestDocuments() throws Exception {
        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        post("/deal/document/333/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }


    @Test
    void approveDocuments() throws Exception {
        Mockito.when(dealService.checkSesCode(Mockito.any(), Mockito.anyInt())).thenReturn(false);
        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        post("/deal/document/987/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(2534));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void getClientApplication() throws Exception {
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.setSes_code(1232);
        Mockito.when(dealService.getClientApplication(Mockito.any())).thenReturn(clientApplication);


        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        get("/deal/admin/application/542")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ses_code").value(1232));
    }

    @Test
    void updateClientApplicationStatus() throws Exception {
        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        put("/deal/admin/application/456/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(ApplicationStatus.DOCUMENT_CREATED));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }
}