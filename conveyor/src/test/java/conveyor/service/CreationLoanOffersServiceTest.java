package conveyor.service;

import conveyor.dto.LoanApplicationRequestDTO;
import conveyor.dto.LoanOfferDTO;
import conveyor.util.CreateAndInitTestDTOs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CreationLoanOffersServiceTest {

    @Autowired
    CreationLoanOffersService creationLoanOffersService;

    @Test
    void createLoanOffersList() {
        assertThrows(RuntimeException.class, () -> creationLoanOffersService.createLoanOffersList(null));
        LoanApplicationRequestDTO loanApplicationRequestDTO = CreateAndInitTestDTOs.createAndInitLoanApplicationRequestDTO();
        assertNotNull(creationLoanOffersService.createLoanOffersList(loanApplicationRequestDTO));
        List<LoanOfferDTO> loanOfferDTOS = creationLoanOffersService.createLoanOffersList(loanApplicationRequestDTO);
        assertEquals(4, loanOfferDTOS.size());
        for (int i = 0; i < loanOfferDTOS.size()-1; i++) {
            assertTrue(loanOfferDTOS.get(i).getRate().doubleValue() >= loanOfferDTOS.get(i+1).getRate().doubleValue() );
        }
    }

    @Test
    void calcMonthlyPayment() {
        assertThrows(RuntimeException.class, () -> CreationLoanOffersService.calcMonthlyPayment(null, 5, new BigDecimal(10000)));
        assertThrows(RuntimeException.class, () -> CreationLoanOffersService.calcMonthlyPayment(new BigDecimal(10), 5, null));
        assertNotNull(CreationLoanOffersService.calcMonthlyPayment(new BigDecimal(10), 6, new BigDecimal(100000)));
        BigDecimal monthlyPayment = CreationLoanOffersService.calcMonthlyPayment(new BigDecimal(10), 6, new BigDecimal(100000));
        assertEquals(17136.43, monthlyPayment.doubleValue(), 5.5);

    }

    @Test
    void createLoanOffer() {
        assertThrows(RuntimeException.class, () ->creationLoanOffersService.createLoanOffer(null, false, false));
        LoanApplicationRequestDTO loanApplicationRequestDTO = CreateAndInitTestDTOs.createAndInitLoanApplicationRequestDTO();
        assertNotNull(creationLoanOffersService.createLoanOffer(loanApplicationRequestDTO, false, false));
        LoanOfferDTO loanOfferDTO = creationLoanOffersService.createLoanOffer(loanApplicationRequestDTO, false, false);
        assertNotNull(loanOfferDTO.getApplicationId());
        assertNotNull(loanOfferDTO.getIsInsuranceEnabled());
        assertEquals(false, loanOfferDTO.getIsInsuranceEnabled());
        assertNotNull(loanOfferDTO.getIsSalaryClient());
        assertEquals(false, loanOfferDTO.getIsSalaryClient());
        assertNotNull(loanOfferDTO.getMonthlyPayment());
        assertNotNull(loanOfferDTO.getRate());
        assertNotNull(loanOfferDTO.getRequestedAmount());
        assertEquals(loanApplicationRequestDTO.getAmount(), loanOfferDTO.getRequestedAmount());
        assertNotNull(loanOfferDTO.getTerm());
        assertEquals(loanApplicationRequestDTO.getTerm(), loanOfferDTO.getTerm());
        assertNotNull(loanOfferDTO.getTotalAmount());
    }
}