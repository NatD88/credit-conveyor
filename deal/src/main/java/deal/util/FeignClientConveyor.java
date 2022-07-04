package deal.util;

import conveyor.dto.CreditDTO;
import conveyor.dto.LoanApplicationRequestDTO;
import conveyor.dto.LoanOfferDTO;
import conveyor.dto.ScoringDataDTO;
import deal.config.FeignClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "conveyorController",  url = "${conveyor.url}",
        configuration = FeignClientConfig.class)
public interface FeignClientConveyor {

    @RequestMapping(method = RequestMethod.POST, value = "/offers")
         ResponseEntity<List<LoanOfferDTO>> getLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/calculation")
    ResponseEntity<CreditDTO> getCreditDTO(@RequestBody ScoringDataDTO scoringDataDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/calculation")
    ResponseEntity<String> getRejectScoring(@RequestBody ScoringDataDTO scoringDataDTO);
}
