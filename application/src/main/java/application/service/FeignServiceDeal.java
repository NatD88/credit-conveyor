package application.service;

import application.dto.LoanApplicationRequestDTO;
import application.dto.LoanOfferDTO;
import application.util.FeignClientDeal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeignServiceDeal {

    private final FeignClientDeal feignClientDeal;

    private void validateLoanApplicationRequestDTO(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("validateLoanApplicationRequestDTO started");
        if (loanApplicationRequestDTO == null) {
            throw new RuntimeException("loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getFirstName() == null) {
            throw new RuntimeException("firstName in loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getLastName() == null) {
            throw new RuntimeException("lastName in loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getBirthday() == null) {
            throw new RuntimeException("birthday in loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getEmail() == null) {
            throw new RuntimeException("email in loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getAmount() == null) {
            throw new RuntimeException("amount in loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getTerm() == null) {
            throw new RuntimeException("term in loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getPassportNumber() == null) {
            throw new RuntimeException("passportNumber in loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getPassportSeries() == null) {
            throw new RuntimeException("passportSeries in loanApplicationRequestDTO is null!");
        }
        log.info("validateLoanApplicationRequestDTO ended");
    }

    private void validateLoanOfferDTO(LoanOfferDTO loanOfferDTO) {
        log.info("validateLoanOfferDTO started");
        if (loanOfferDTO == null) {
            throw new RuntimeException("loanOfferDTO is null!");
        }
        if (loanOfferDTO.getApplicationId() == null) {
            throw new RuntimeException("ApplicationId in loanOfferDTO is null!");
        }
        if (loanOfferDTO.getIsInsuranceEnabled() == null) {
            throw new RuntimeException("IsInsuranceEnabled in loanOfferDTO is null!");
        }
        if (loanOfferDTO.getIsSalaryClient() == null) {
            throw new RuntimeException("IsSalaryClient in loanOfferDTO is null!");
        }
        if (loanOfferDTO.getMonthlyPayment() == null) {
            throw new RuntimeException("MonthlyPayment in loanOfferDTO is null!");
        }
        if (loanOfferDTO.getRate() == null) {
            throw new RuntimeException("rate in loanOfferDTO is null!");
        }
        if (loanOfferDTO.getRequestedAmount() == null) {
            throw new RuntimeException("RequestedAmount in loanOfferDTO is null!");
        }
        if (loanOfferDTO.getTerm() == null) {
            throw new RuntimeException("term in loanOfferDTO is null!");
        }
        if (loanOfferDTO.getTotalAmount() == null) {
            throw new RuntimeException("TotalAmount in loanOfferDTO is null!");
        }
        log.info("validateLoanOfferDTO ended");
    }

    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("loanApplicationRequestDTO entered method getLoanOffers of FeignServiceDeal class." +
                "FeignClient will be send loanApplicationRequestDTO to ms-deal." +
                "loanApplicationRequestDTO: {}", loanApplicationRequestDTO);
        validateLoanApplicationRequestDTO(loanApplicationRequestDTO);
        return feignClientDeal.getLoanOffers(loanApplicationRequestDTO);
    }

    public void sendLoanOffer(LoanOfferDTO loanOfferDTO) {
        log.info("loanOfferDTO entered method sendLoanOffer of FeignServiceDeal class." +
                "FeignClient will be send loanOfferDTO to ms-deal." +
                "loanOfferDTO: {}", loanOfferDTO);
        validateLoanOfferDTO(loanOfferDTO);
        feignClientDeal.sendChosenLoanOffer(loanOfferDTO);
    }
}
