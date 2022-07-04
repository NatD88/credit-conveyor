package conveyor.controller;


import conveyor.dto.*;
import conveyor.service.CalculatingCreditParametersService;
import conveyor.service.CreationLoanOffersService;
import conveyor.service.ScoringService;
import conveyor.util.BooleanAndRate;
import conveyor.util.LoanApplicationRequestValidator;
import conveyor.util.RejectScoringException;
import conveyor.util.ScoringDataValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.awt.*;
import java.util.List;

@RestController
@RequestMapping("/conveyor")
@Api(value = "credit conveyor controller")
@Slf4j
@RequiredArgsConstructor

public class ConveyorController {

    private final LoanApplicationRequestValidator loanApplicationRequestValidator;
    private final ScoringDataValidator scoringDataValidator;
    private final CreationLoanOffersService creationLoanOffersService;
    private final ScoringService scoringService;
    private final CalculatingCreditParametersService calculatingCreditParametersService;

    @PostMapping("/offers")
    @ApiOperation(value = "calculate four loan offers")
    public List<LoanOfferDTO> calcPossibleCreditConditions(@Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO,
                                                                           BindingResult bindingResult) throws MethodArgumentNotValidException {
        log.info("LoanApplicationRequestDTO entered the ConveyorController on mapping /conveyor/offers. " +
                "loanApplicationRequestDTO: {}", loanApplicationRequestDTO);
        loanApplicationRequestValidator.validate(loanApplicationRequestDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("validation of loanApplicationRequestDTO has errors, MethodArgumentNotValidException will be thrown");
            throw new MethodArgumentNotValidException(null, bindingResult);
        }

        List<LoanOfferDTO> loanOfferDTOS =
                creationLoanOffersService.createLoanOffersList(loanApplicationRequestDTO);
        log.info("list of loan offers is ready for the ResponseEntity. loanOfferDTOS: {} ", loanOfferDTOS);
        return loanOfferDTOS;
    }

    @PostMapping("/calculation")
    @ApiOperation(value = "calculate all credit options")
    public CreditDTO calcAllCreditOptions(@Valid @RequestBody ScoringDataDTO scoringDataDTO,
                                                          BindingResult bindingResult) throws MethodArgumentNotValidException {
        log.info("ScoringDataDTO entered the ConveyorController on mapping /conveyor/calculation. " +
                "scoringDataDTO: {}", scoringDataDTO);
        scoringDataValidator.validate(scoringDataDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("validation of scoringDataDTO has errors, MethodArgumentNotValidException will be thrown");
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
        BooleanAndRate booleanAndRate = scoringService.executeScoring(scoringDataDTO);
        CreditDTO creditDTO;
        if (!booleanAndRate.isOk()) {
            log.warn("ScoringService denied the loan. RejectScoringException will be thrown");
            throw new RejectScoringException();

        } else {
            creditDTO = calculatingCreditParametersService.createCreditDTO(scoringDataDTO, booleanAndRate.getRate());
        }
        log.info("creditDTO is ready for the ResponseEntity. creditDTO: {} ", creditDTO);
        return creditDTO;
    }

    @ExceptionHandler(RejectScoringException.class)
    public String handleRejectScoringException(RejectScoringException e) {
        log.info("handle RejectScoringException. the ResponseEntity with denial will return");
        return "Отказано в выдаче кредита!!";
    }
}
