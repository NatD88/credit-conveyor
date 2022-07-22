package gateway.util;

import gateway.dto.LoanApplicationRequestDTO;
import gateway.dto.LoanOfferDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "conveyorApplication",  url = "${application.url}")
public interface FeignClientApplication {

    @RequestMapping(method = RequestMethod.POST, value = "")
    List<LoanOfferDTO> getLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO);

    @RequestMapping(method = RequestMethod.PUT, value = "/offer")
    void sendLoanOffer(@RequestBody LoanOfferDTO loanOfferDTO);

}
