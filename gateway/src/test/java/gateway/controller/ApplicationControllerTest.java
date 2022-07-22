package gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gateway.dto.FinishRegistrationRequestDTO;
import gateway.dto.LoanApplicationRequestDTO;
import gateway.dto.LoanOfferDTO;
import gateway.service.FeignServiceApplMs;
import gateway.service.FeignServiceDealMs;
import gateway.util.ApplicationNotFoundException;
import gateway.util.BadRequestException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationController.class)
class ApplicationControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private MockMvc mockMvc;

    @MockBean
    FeignServiceApplMs feignServiceApplMs;
    @MockBean
    FeignServiceDealMs feignServiceDealMs;

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

        List<LoanOfferDTO> list = new ArrayList<>();
        Mockito.when(feignServiceApplMs.getLoanOffers(Mockito.any())).thenReturn(list);
        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(loanApplicationRequestDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(list)));
    }

    @Test
    void saveLoanOffer() throws Exception {
        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        put("/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(loanOfferDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void handleBadRequestException() {
        ApplicationController applicationController = new ApplicationController(feignServiceApplMs, feignServiceDealMs);
        Map<Object, Object> map = new HashMap<>();
        ResponseEntity<Object> response = applicationController.handleBadRequestException(new BadRequestException(map));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void finishRegistration() throws Exception {
        FinishRegistrationRequestDTO finishRegistrationRequestDTO = new FinishRegistrationRequestDTO();
        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        put("/registration/15")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(finishRegistrationRequestDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());

    }

    @Test
    void denyApplication() throws Exception {
        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        post("/15/deny")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(20L));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void handleApplicationNotFoundException() {
        ApplicationController applicationController = new ApplicationController(feignServiceApplMs, feignServiceDealMs);
        ResponseEntity<String> response = applicationController.handleApplicationNotFoundException(new ApplicationNotFoundException("Заявка с номером 55 не найдена в базе!"));
        assertEquals("Заявка с номером 55 не найдена в базе!", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}