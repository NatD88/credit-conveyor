package application.service;

import application.dto.LoanApplicationRequestDTO;
import application.dto.LoanOfferDTO;
import application.util.FeignClientDeal;
import application.util.ValidatorDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeignServiceDeal {

    private final FeignClientDeal feignClientDeal;
    private final ValidatorDTO validatorDTO;

    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("loanApplicationRequestDTO entered method getLoanOffers of FeignServiceDeal class." +
                "FeignClient will be send loanApplicationRequestDTO to ms-deal." +
                "loanApplicationRequestDTO: {}", loanApplicationRequestDTO);
        validatorDTO.validate(loanApplicationRequestDTO);
        return feignClientDeal.getLoanOffers(loanApplicationRequestDTO);
    }

    public void sendLoanOffer(LoanOfferDTO loanOfferDTO) {
        log.info("loanOfferDTO entered method sendLoanOffer of FeignServiceDeal class." +
                "FeignClient will be send loanOfferDTO to ms-deal." +
                "loanOfferDTO: {}", loanOfferDTO);
        validatorDTO.validate(loanOfferDTO);
        feignClientDeal.sendChosenLoanOffer(loanOfferDTO);
    }
}
