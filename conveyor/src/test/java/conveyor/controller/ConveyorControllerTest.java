package conveyor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import conveyor.dto.*;
import conveyor.dto.EmploymentPosition;
import conveyor.dto.EmploymentStatus;
import conveyor.dto.Gender;
import conveyor.dto.MaritalStatus;
import conveyor.service.CalculatingCreditParametersService;
import conveyor.service.CreationLoanOffersService;
import conveyor.service.ScoringService;
import conveyor.util.BooleanAndRate;
import conveyor.util.LoanApplicationRequestValidator;
import conveyor.util.RejectScoringException;
import conveyor.util.ScoringDataValidator;
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
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConveyorController.class)
class ConveyorControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    private LoanApplicationRequestValidator loanApplicationRequestValidator;

    @MockBean
    private ScoringDataValidator scoringDataValidator;

    @MockBean
    private CreationLoanOffersService creationLoanOffersService;

    @MockBean
    private ScoringService scoringService;

    @MockBean
    private CalculatingCreditParametersService calculatingCreditParametersService;

    LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO(new BigDecimal(15000),
            6,
            "Max",
            "Ivanov",
            "jytjt",
            "fhwehi@mail.ru",
            LocalDate.of(1991, 11, 5),
            "1245",
            "123456");

    ScoringDataDTO scoringDataDTO = new ScoringDataDTO(new BigDecimal(15000),
            10,
            "Max",
            "Ivanov",
            "jytjt",
            Gender.MALE,
            LocalDate.of(1991, 11, 5),
            "1245",
            "123456",
            LocalDate.of(2020, 11, 5),
            "MVD RF",
            MaritalStatus.SINGLE,
            1,
            new EmploymentDTO (EmploymentStatus. EMPLOYED,
                    "1234545",
                    new BigDecimal(50000),
                    EmploymentPosition.WORKER,
            6,
            36),
            "12345678912345678987",
            false,
            false);

    LoanOfferDTO loanOfferDTO = new LoanOfferDTO(1L,
            new BigDecimal(100000),
            new BigDecimal(130000),
            0,
            new BigDecimal(15000),
            new BigDecimal(12),
            true,
            true);

    @Test
    void calcPossibleCreditConditions() throws Exception {

        List<LoanOfferDTO> loanOfferDTOS = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            loanOfferDTOS.add(loanOfferDTO);
        }
        Mockito.when(creationLoanOffersService.createLoanOffersList(Mockito.any())).thenReturn(loanOfferDTOS);

        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(loanApplicationRequestDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].isInsuranceEnabled").value(true));

        Mockito.verify(creationLoanOffersService, Mockito.times(1)).createLoanOffersList(Mockito.any());

        loanApplicationRequestDTO.setPassportSeries("12");
        mockRequest = MockMvcRequestBuilders
                .post("/conveyor/offers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loanApplicationRequestDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void calcAllCreditOptions() throws Exception {

        CreditDTO creditDTO = new CreditDTO(new BigDecimal(100000),
                5,
                new BigDecimal(15000),
                new BigDecimal(13),
                new BigDecimal(13),
                false,
                false,
                new ArrayList<PaymentScheduleElement>());

        Mockito.when(scoringService.executeScoring(Mockito.any())).thenReturn(new BooleanAndRate(true,new BigDecimal(13)));
        Mockito.when(calculatingCreditParametersService.createCreditDTO(Mockito.any(),Mockito.any())).thenReturn(creditDTO);

        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        post("/conveyor/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(scoringDataDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isInsuranceEnabled").value(false));

        scoringDataDTO.setPassportIssueBranch("e");
        mockRequest =
                MockMvcRequestBuilders.
                        post("/conveyor/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(scoringDataDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        Mockito.verify(scoringService, Mockito.times(1)).executeScoring(Mockito.any());
        Mockito.verify(calculatingCreditParametersService, Mockito.times(1)).createCreditDTO(Mockito.any(), Mockito.any());

    }

    @Test
    void handleRejectScoringException() {
        ConveyorController conveyorController = new ConveyorController(loanApplicationRequestValidator, scoringDataValidator, creationLoanOffersService, scoringService, calculatingCreditParametersService);
        ResponseEntity<String> response = conveyorController.handleRejectScoringException(new RejectScoringException());
        assertEquals("Отказано в выдаче кредита!!", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}