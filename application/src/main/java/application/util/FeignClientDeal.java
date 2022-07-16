package application.util;


import application.dto.LoanApplicationRequestDTO;
import application.dto.LoanOfferDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "dealController", url = "${deal.url}")
public interface FeignClientDeal {

    @RequestMapping(method = RequestMethod.POST, value = "/application")
    List<LoanOfferDTO> getLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO);

    @RequestMapping(method = RequestMethod.PUT, value = "/offer")
    void sendChosenLoanOffer(@RequestBody LoanOfferDTO loanOfferDTO);
}
