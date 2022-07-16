package deal.util;


import deal.config.FeignClientConfig;
import deal.dto.CreditDTO;
import deal.dto.LoanApplicationRequestDTO;
import deal.dto.LoanOfferDTO;
import deal.dto.ScoringDataDTO;
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
    List<LoanOfferDTO> getLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/calculation")
    CreditDTO getCreditDTO(@RequestBody ScoringDataDTO scoringDataDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/calculation")
    String getRejectScoring(@RequestBody ScoringDataDTO scoringDataDTO);
}
