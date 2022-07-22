package gateway.service;

import gateway.dto.FinishRegistrationRequestDTO;
import gateway.dto.LoanApplicationRequestDTO;
import gateway.dto.LoanOfferDTO;
import gateway.util.FeignClientApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeignServiceApplMs {

    private final FeignClientApplication feignClientApplication;

    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("loanApplicationRequestDTO entered getLoanOffers in FeignServiceApplMs");
        return feignClientApplication.getLoanOffers(loanApplicationRequestDTO);
    }

    public void sendLoanOffer(LoanOfferDTO loanOfferDTO) {
        log.info("loanOfferDTO entered sendLoanOffer in FeignServiceApplMs");
        feignClientApplication.sendLoanOffer(loanOfferDTO);
    }
}
