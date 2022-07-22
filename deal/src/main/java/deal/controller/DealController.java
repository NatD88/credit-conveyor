package deal.controller;

import deal.dto.*;
import deal.entity.ClientApplication;
import deal.service.DealService;
import deal.service.FeignServiceConveyor;
import deal.service.KafkaSendService;
import deal.util.*;
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
    private final KafkaSendService kafkaSendService;


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
        EmailMessage emailMessage = dealService.createEmail(ThemeEmail.FINISH_REGISTRATION, loanOfferDTO.getApplicationId());
        kafkaSendService.send(emailMessage);
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
        EmailMessage emailMessage = dealService.createEmail(ThemeEmail.CREATE_DOCUMENT, applicationID);
        kafkaSendService.send(emailMessage);
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
        EmailMessage emailMessage = dealService.createEmail(ThemeEmail.APPLICATION_DENIED, e.getApplicationID());
        kafkaSendService.send(emailMessage);
        return e.getRejectMessage();
    }

    @PostMapping("/document/{applicationId}/send")
    @ApiOperation(value = "requesting documents sending")
    public void sendDocuments(@PathVariable Long applicationId) {
        log.info("documents request received, applicationId: {}", applicationId);
        EmailMessage emailMessage = dealService.createEmail(ThemeEmail.SEND_DOCUMENT, applicationId);
        kafkaSendService.send(emailMessage);
    }

    @PostMapping("/document/{applicationId}/deny")
    @ApiOperation(value = "deny documents signing")
    public void denyApplication(@PathVariable Long applicationId) {
        log.info("deny documents request received, applicationId: {}", applicationId);
        dealService.denyApp(applicationId);
        EmailMessage emailMessage = dealService.createEmail(ThemeEmail.APPLICATION_DENIED, applicationId);
        kafkaSendService.send(emailMessage);
    }

    @PostMapping("/document/{applicationId}/sign")
    @ApiOperation(value = "requesting documents signing")
    public void signRequestDocuments(@PathVariable Long applicationId) {
        log.info("documents signing received, applicationId: {}", applicationId);
        dealService.createAndSaveSES(applicationId);
        EmailMessage emailMessage = dealService.createEmail(ThemeEmail.SEND_SES, applicationId);
        kafkaSendService.send(emailMessage);
    }

    @PostMapping("/document/{applicationId}/code")
    @ApiOperation(value = "approving documents")
    public void approveDocuments(@PathVariable Long applicationId, @RequestBody int code) {
        log.info("approving documents, applicationId: {}, code: {}", applicationId, code);
        if (dealService.checkSesCode(applicationId, code)) {
            dealService.updateClientApplicationStatus(applicationId, ApplicationStatus.DOCUMENT_SIGNED);
            dealService.updateCredit(applicationId, CreditStatus.ISSUED);
            EmailMessage emailMessage = dealService.createEmail(ThemeEmail.CREDIT_ISSUED, applicationId);
            kafkaSendService.send(emailMessage);
        }
    }

    @GetMapping("/admin/application/{applicationId}")
    @ApiOperation(value = "getting client application")
    public ClientApplication getClientApplication(@PathVariable Long applicationId) {
        return dealService.getClientApplication(applicationId);
    }

    @GetMapping("/admin/application")
    @ApiOperation(value = "getting all client applications")
    public List<ClientApplication> getAllClientApplications() {
        return dealService.getAllClientApplications();
    }

    @PutMapping("/admin/application/{applicationId}/status")
    @ApiOperation(value = "update clientApplication status")
    public void updateClientApplicationStatus(@PathVariable Long applicationId, ApplicationStatus applicationStatus) {
        dealService.updateClientApplicationStatus(applicationId, applicationStatus);
    }
}
