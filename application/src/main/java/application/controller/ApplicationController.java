package application.controller;

import application.dto.LoanApplicationRequestDTO;
import application.dto.LoanOfferDTO;
import application.service.FeignServiceDeal;
import application.util.ApplicationNotFoundException;
import application.util.LoanApplicationRequestValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(value = "application conveyor controller")
@RequestMapping("/application")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    private final LoanApplicationRequestValidator loanApplicationRequestValidator;
    private final FeignServiceDeal feignServiceDeal;

    @PostMapping
    @ApiOperation(value = "prescoring and getting four loan offers from ms deal")
    public List<LoanOfferDTO> getLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO,
                                            BindingResult bindingResult) throws MethodArgumentNotValidException {
        log.info("LoanApplicationRequestDTO entered the ApplicationController on mapping /application " +
                "loanApplicationRequestDTO: {}", loanApplicationRequestDTO);
        loanApplicationRequestValidator.validate(loanApplicationRequestDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("validation of loanApplicationRequestDTO has errors, MethodArgumentNotValidException will be thrown");
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
        return feignServiceDeal.getLoanOffers(loanApplicationRequestDTO);
    }

    @PutMapping("/offer")
    @ApiOperation(value = "send chosen LoanOfferDTO to ms deal")
    public void saveLoanOffer(@RequestBody LoanOfferDTO loanOfferDTO) {
        log.info("loanOfferDTO entered the ApplicationController on mapping /application/offer " +
                "loanOfferDTO: {}", loanOfferDTO);
        feignServiceDeal.sendLoanOffer(loanOfferDTO);
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<String> handleApplicationNotFoundException(ApplicationNotFoundException e) {
        log.warn("ApplicationNotFoundException handled");
        return new ResponseEntity<>(e.getErrorMessage(), HttpStatus.NOT_FOUND);
    }
}
