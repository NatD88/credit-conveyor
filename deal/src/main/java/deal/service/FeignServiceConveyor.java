package deal.service;


import conveyor.dto.CreditDTO;
import conveyor.dto.LoanApplicationRequestDTO;
import conveyor.dto.LoanOfferDTO;
import conveyor.dto.ScoringDataDTO;
import conveyor.util.RejectScoringException;
import deal.util.BadRequestException;
import deal.util.FeignClientConveyor;
import deal.util.RejectScoringDealException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class FeignServiceConveyor {

    private final FeignClientConveyor feignClientConveyor;

    public ResponseEntity<List<LoanOfferDTO>> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return feignClientConveyor.getLoanOffers(loanApplicationRequestDTO);
    }

    public ResponseEntity<CreditDTO> getCreditDTO(ScoringDataDTO scoringDataDTO, Long applicationID) {
        ResponseEntity<CreditDTO> responseEntity;
        try {
            responseEntity = feignClientConveyor.getCreditDTO(scoringDataDTO);
        } catch (BadRequestException ex) {
            ex.setApplicationID(applicationID);
            throw ex;
        } catch (Exception e) {
            String rejectString = feignClientConveyor.getRejectScoring(scoringDataDTO).getBody();
            throw new RejectScoringDealException(rejectString, applicationID);
        }
        return responseEntity;
    }
}
