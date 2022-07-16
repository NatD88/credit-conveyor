package deal.service;

import deal.dto.CreditDTO;
import deal.dto.LoanApplicationRequestDTO;
import deal.dto.LoanOfferDTO;
import deal.dto.ScoringDataDTO;
import deal.util.BadRequestException;
import deal.util.FeignClientConveyor;
import deal.util.RejectScoringDealException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeignServiceConveyor {

    private final FeignClientConveyor feignClientConveyor;

    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("loanApplicationRequestDTO entered getLoanOffers of  FeignServiceConveyor. loanApplicationRequestDTO: {}", loanApplicationRequestDTO);
        return feignClientConveyor.getLoanOffers(loanApplicationRequestDTO);
    }

    public CreditDTO getCreditDTO(ScoringDataDTO scoringDataDTO, Long applicationID) {
        log.info("scoringDataDTO and applicationID entered getCreditDTO. scoringDataDTO: {},applicationID: {} ", scoringDataDTO,applicationID);
        CreditDTO responseCreditDTO;
        try {
            responseCreditDTO = feignClientConveyor.getCreditDTO(scoringDataDTO);
            log.info("CreditDTO successfully received from ms-conveyor");
        } catch (BadRequestException ex) {
            log.warn("BadRequestException caught");
            ex.setApplicationID(applicationID);
            throw ex;
        } catch (Exception e) {
            log.warn("Exception caught");
            String rejectString = feignClientConveyor.getRejectScoring(scoringDataDTO);
            throw new RejectScoringDealException(rejectString, applicationID);
        }
        return responseCreditDTO;
    }
}
