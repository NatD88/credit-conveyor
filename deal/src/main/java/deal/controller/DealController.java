package deal.controller;

import deal.dto.LoanOfferDTO;
import deal.dto.LoanApplicationRequestDTO;
import deal.dto.CreditDTO;
import deal.dto.ScoringDataDTO;
import deal.dto.FinishRegistrationRequestDTO;
import deal.entity.ClientApplication;
import deal.service.DealService;
import deal.service.FeignServiceConveyor;
import deal.util.ApplicationNotFoundException;
import deal.util.BadRequestException;
import deal.util.RejectScoringDealException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deal")
@Api(value = "deal controller")
@RequiredArgsConstructor
@Slf4j
public class DealController {

    private final DealService dealService;
    private final FeignServiceConveyor feignServiceConveyor;

    @PostMapping("/application")
    @ApiOperation(value = "get four loan offers from ms conveyor and save Client and Application to DB")
    public List<LoanOfferDTO> getPossibleCreditConditions(
            @RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("LoanApplicationRequestDTO entered to /deal/application");

        List<LoanOfferDTO> loanOfferDTOList = feignServiceConveyor.getLoanOffers(loanApplicationRequestDTO);

        Long applicationID = dealService.saveClientAndApplication(loanApplicationRequestDTO);
        log.info("client and application saved, applicationID received, applicationID: {}", applicationID);
        log.info("List<LoanOfferDTO> loanOfferDTOList: {}", loanOfferDTOList);
        return dealService.setApplicationIDToLoanOffers(applicationID, loanOfferDTOList);
    }

    @PutMapping("/offer")
    @ApiOperation(value = "save chosen LoanOfferDTO to DB, update applicationStatus and statusHistoryList")
    public void processingLoanOfferDTO(@RequestBody LoanOfferDTO loanOfferDTO) {
        log.info("LoanOfferDTO entered  /deal/offer . loanOfferDTO: {}", loanOfferDTO);
        dealService.saveChosenLoanOfferDTO(loanOfferDTO);
        log.info("loanOfferDTO saved to DB");
    }

    @PutMapping("/calculate/{applicationID}")
    @ApiOperation("create ScoringFataDTO, update info in DB (tables Clients, Applications, Passports), send request to ms conveyor")
    public void processingFinishRegistrationRequestDTO(@RequestBody FinishRegistrationRequestDTO finishRegistrationRequestDTO,
                                                                            @PathVariable Long applicationID) {

        log.info("FinishRegistrationRequestDTO entered  /deal/calculate/{applicationId}. finishRegistrationRequestDTO: {}", finishRegistrationRequestDTO);
        log.info("applicationID entered deal/calculate/{applicationId}. applicationID: {}", applicationID);
        ClientApplication clientApplication = dealService.updateDB(finishRegistrationRequestDTO, applicationID);
        log.info("DB updated, clientApplication: {}  ", clientApplication);
        ScoringDataDTO scoringDataDTO = dealService.createScoringDataDTO(clientApplication);
        log.info("scoringDataDTO created: {}  ", scoringDataDTO);
        dealService.checkApplicationStatus(applicationID);
        log.info("ApplicationStatus checked and corrected in case of previous validate error");

       CreditDTO responseCreditDTO = feignServiceConveyor.getCreditDTO(scoringDataDTO, applicationID);
        log.info("ResponseEntity<CreditDTO> received to DealController");
        dealService.saveCredit(responseCreditDTO, applicationID);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException e) {
        log.warn("BadRequestException handled");
        if (e.getApplicationID() != null) {
            dealService.denieApplicationStatus(e.getApplicationID());
        }
        return new ResponseEntity<>(e.getResponseBody(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<String> handleApplicationNotFoundException(ApplicationNotFoundException e) {
        log.warn("ApplicationNotFoundException handled");
        return new ResponseEntity<>(String.format("Заявка с номером %d не найдена в базе!", e.getApplicationID()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RejectScoringDealException.class)
    public String handleRejectScoringDealException(RejectScoringDealException e) {
        log.warn("handle RejectScoringDealException. the ResponseEntity with denial will return");
        dealService.denieApplicationStatus(e.getApplicationID());
        return e.getRejectMessage();
    }
}
