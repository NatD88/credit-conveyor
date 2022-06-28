package conveyor.service;

import conveyor.dto.CreditDTO;
import conveyor.dto.EmploymentDTO;
import conveyor.dto.PaymentScheduleElement;
import conveyor.dto.ScoringDataDTO;
import conveyor.dto.EmploymentPosition;
import conveyor.dto.EmploymentStatus;
import conveyor.dto.Gender;
import conveyor.dto.MaritalStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@SpringBootTest
class CalculatingCreditParametersServiceTest {

    @Autowired
    private CalculatingCreditParametersService calculatingCreditParametersService;


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
            new EmploymentDTO(EmploymentStatus.EMPLOYED,
                    "1234545",
                    new BigDecimal(50000),
                    EmploymentPosition.WORKER,
                    6,
                    36),
            "12345678912345678987",
            false,
            false);

    @Test
    void createCreditDTO() {

        Assertions.assertThrows(RuntimeException.class, () -> calculatingCreditParametersService.createCreditDTO(null,new BigDecimal(13)));
        CreditDTO creditDTO = calculatingCreditParametersService.createCreditDTO(scoringDataDTO,new BigDecimal(13));

        Assertions.assertNotNull(creditDTO);
        Assertions.assertNotNull(creditDTO.getAmount());
        Assertions.assertEquals(scoringDataDTO.getAmount(),creditDTO.getAmount());
        Assertions.assertNotNull(creditDTO.getTerm());
        Assertions.assertEquals(scoringDataDTO.getTerm(),creditDTO.getTerm());
        Assertions.assertNotNull(creditDTO.getRate());
        Assertions.assertNotNull(creditDTO.getPsk());
        Assertions.assertNotNull(creditDTO.getPaymentSchedule());
        Assertions.assertNotNull(creditDTO.getMonthlyPayment());
        Assertions.assertNotNull(creditDTO.getIsSalaryClient());
        Assertions.assertEquals(scoringDataDTO.getIsInsuranceEnabled(),creditDTO.getIsInsuranceEnabled());
        Assertions.assertNotNull(creditDTO.getIsInsuranceEnabled());
        Assertions.assertEquals(scoringDataDTO.getAmount(),creditDTO.getAmount());
    }

    @Test
    void createPaymentSchedule() {
        Assertions.assertThrows(RuntimeException.class, () -> calculatingCreditParametersService.createPaymentSchedule(null,0,new BigDecimal(50), new BigDecimal(30)));
        Assertions.assertThrows(RuntimeException.class, () -> calculatingCreditParametersService.createPaymentSchedule(new BigDecimal(50),0,null, new BigDecimal(30)));
        Assertions.assertThrows(RuntimeException.class, () -> calculatingCreditParametersService.createPaymentSchedule(new BigDecimal(50),0,new BigDecimal(30), null));
        List<PaymentScheduleElement> list = calculatingCreditParametersService.createPaymentSchedule(new BigDecimal(10000),10,new BigDecimal(12), new BigDecimal(1000));
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.size() > 0);
        for (PaymentScheduleElement paymentScheduleElement : list) {
            Assertions.assertNotNull(paymentScheduleElement);
            Assertions.assertNotNull(paymentScheduleElement.getTotalPayment());
            Assertions.assertNotNull(paymentScheduleElement.getRemainingDebt());
            Assertions.assertNotNull(paymentScheduleElement.getNumber());
            Assertions.assertNotNull(paymentScheduleElement.getInterestPayment());
            Assertions.assertNotNull(paymentScheduleElement.getDebtPayment());
            Assertions.assertNotNull(paymentScheduleElement.getDate());
        }
    }

    @Test
    void calcPsk() {
        Assertions.assertThrows(RuntimeException.class, () -> calculatingCreditParametersService.calcPsk(null, new BigDecimal(50000)));
        List<PaymentScheduleElement> list = calculatingCreditParametersService.createPaymentSchedule(new BigDecimal(100000),6,new BigDecimal(13), new BigDecimal("17314.18"));
        Assertions.assertThrows(RuntimeException.class, () -> calculatingCreditParametersService.calcPsk(list, null));
        Assertions.assertNotNull((calculatingCreditParametersService.calcPsk(list,new BigDecimal(50000))));
        BigDecimal psk = calculatingCreditParametersService.calcPsk(list, new BigDecimal(100000).subtract(new BigDecimal(CreationLoanOffersService.INSURANCE_PRICE)));
        Assertions.assertEquals(71.66363, psk.doubleValue(), 0.1);
    }
}