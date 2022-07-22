package gateway.controller;

import gateway.dto.FinishRegistrationRequestDTO;
import gateway.dto.LoanApplicationRequestDTO;
import gateway.dto.LoanOfferDTO;
import gateway.service.FeignServiceApplMs;
import gateway.service.FeignServiceDealMs;
import gateway.util.ApplicationNotFoundException;
import gateway.util.BadRequestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(value = "application conveyor controller")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    private final FeignServiceApplMs feignServiceApplMs;
    private final FeignServiceDealMs feignServiceDealMs;

    @PostMapping
    @ApiOperation(value = "request prescoring and getting four loan offers from ms application")
    public List<LoanOfferDTO> getLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("LoanApplicationRequestDTO entered getLoanOffers in ApplicationController. loanApplicationRequestDTO: {}", loanApplicationRequestDTO);
        return feignServiceApplMs.getLoanOffers(loanApplicationRequestDTO);
    }

    @PutMapping("/offer")
    @ApiOperation(value = "send chosen LoanOfferDTO to ms deal")
    public void saveLoanOffer(@RequestBody LoanOfferDTO loanOfferDTO) {
        log.info(" LoanOfferDTO entered saveLoanOffer in ApplicationController. loanOfferDTO: {}", loanOfferDTO);
        feignServiceApplMs.sendLoanOffer(loanOfferDTO);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException e) {
        log.warn("BadRequestException handled: {}", e.getResponseBody());
        return new ResponseEntity<>(e.getResponseBody(), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/registration/{applicationId}")
    @ApiOperation(value = "finish registration")
    public void finishRegistration(@RequestBody FinishRegistrationRequestDTO finishRegistrationRequestDTO,
                                   @PathVariable Long applicationId) {
        log.info("FinishRegistrationRequestDTO entered  /registration/{applicationId}. finishRegistrationRequestDTO: {}", finishRegistrationRequestDTO);
        feignServiceDealMs.finishReg(finishRegistrationRequestDTO, applicationId);
    }

    @PostMapping("/{applicationId}/deny")
    public void denyApplication(@PathVariable Long applicationId) {
        feignServiceDealMs.denyApp(applicationId);
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<String> handleApplicationNotFoundException(ApplicationNotFoundException e) {
        log.warn("ApplicationNotFoundException handled");
        return new ResponseEntity<>(e.getErrorMessage(), HttpStatus.NOT_FOUND);
    }
}
