package application.controller;

import application.dto.LoanApplicationRequestDTO;
import application.dto.LoanOfferDTO;
import application.service.FeignServiceDeal;
import application.util.ApplicationNotFoundException;
import application.util.LoanApplicationRequestValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(ApplicationController.class)
class ApplicationControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private MockMvc mockMvc;


    @MockBean
    LoanApplicationRequestValidator loanApplicationRequestValidator;
    @MockBean
    FeignServiceDeal feignServiceDeal;

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

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void getLoanOffers() throws Exception {
        List<LoanOfferDTO> loanOfferDTOS = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            loanOfferDTOS.add(loanOfferDTO);
        }

        Mockito.when(feignServiceDeal.getLoanOffers(Mockito.any())).thenReturn(loanOfferDTOS);

        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        post("/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(loanApplicationRequestDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(loanOfferDTOS)));
    }

    @Test
    void saveLoanOffer() throws Exception {
        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        put("/application/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(loanOfferDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void handleApplicationNotFoundException() {
        ApplicationController applicationController = new ApplicationController(loanApplicationRequestValidator, feignServiceDeal);
        ResponseEntity<String> response = applicationController.handleApplicationNotFoundException(new ApplicationNotFoundException("Заявка с номером 55 не найдена в базе!"));
        assertEquals("Заявка с номером 55 не найдена в базе!", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}