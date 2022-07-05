package deal.service;

import conveyor.dto.*;
import deal.dto.FinishRegistrationRequestDTO;
import deal.entity.*;
import deal.repository.*;
import deal.util.ApplicationNotFoundException;
import deal.util.ApplicationStatus;
import deal.util.ChangeType;
import deal.util.CreditStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealService {

    private final ClientRepository clientRepository;
    private final PassportRepository passportRepository;
    private final CreditRepository creditRepository;
    private final ApplicationRepository applicationRepository;
    private final EmploymentRepository employmentRepository;

    private void validateLoanApplicationRequestDTO(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("validateLoanApplicationRequestDTO started");
        if (loanApplicationRequestDTO == null) {
            throw new RuntimeException("loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getFirstName() == null) {
            throw new RuntimeException("firstName in loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getLastName() == null) {
            throw new RuntimeException("lastName in loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getBirthday() == null) {
            throw new RuntimeException("birthday in loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getEmail() == null) {
            throw new RuntimeException("email in loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getAmount() == null) {
            throw new RuntimeException("amount in loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getTerm() == null) {
            throw new RuntimeException("term in loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getPassportNumber() == null) {
            throw new RuntimeException("passportNumber in loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getPassportSeries() == null) {
            throw new RuntimeException("passportSeries in loanApplicationRequestDTO is null!");
        }
        log.info("validateLoanApplicationRequestDTO ended");
    }

    public Passport initPassport(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("passport init started");
        Passport passport = Passport.builder()
                .series(loanApplicationRequestDTO.getPassportSeries())
                .number(loanApplicationRequestDTO.getPassportNumber())
                .build();
        log.info("passport init ended");
        return passport;
    }

    public Client initClient(LoanApplicationRequestDTO loanApplicationRequestDTO, Passport passport) {
        log.info("client init started");
        Client client = Client.builder()
                .firstName(loanApplicationRequestDTO.getFirstName())
                .lastName(loanApplicationRequestDTO.getLastName())
                .email(loanApplicationRequestDTO.getEmail())
                .birthDate(loanApplicationRequestDTO.getBirthday())
                .passport(passport)
                .build();
        if (loanApplicationRequestDTO.getMiddleName() != null) {
            client.setMiddleName(loanApplicationRequestDTO.getMiddleName());
        }
        log.info("client init ended");
        return client;
    }

    public Credit initCredit(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("credit init started");
        Credit credit = Credit.builder()
                .amount(loanApplicationRequestDTO.getAmount())
                .term(loanApplicationRequestDTO.getTerm())
                .build();
        log.info("credit init ended");
        return credit;
    }

    public List<ApplicationStatusHistory> initStatusHistoryList() {
        log.info("StatusHistoryList init started");
        ApplicationStatusHistory applicationStatusHistory = ApplicationStatusHistory.builder()
                .status(ApplicationStatus.PREAPPROVAL)
                .time(LocalDateTime.now())
                .changeType(ChangeType.AUTOMATIC)
                .build();

        List<ApplicationStatusHistory> statusHistoryList = new ArrayList<ApplicationStatusHistory>();
        statusHistoryList.add(applicationStatusHistory);
        log.info("StatusHistoryList init ended");
        return statusHistoryList;
    }

    public ClientApplication initClientApplication(Credit credit, Client client) {
        log.info("ClientApplication init started");
        ClientApplication clientApplication = ClientApplication.builder()
                .credit(credit)
                .client(client)
                .creationDate(LocalDate.now())
                .applicationStatus(ApplicationStatus.PREAPPROVAL)
                .statusHistoryList(initStatusHistoryList())
                .build();
        log.info("ClientApplication init ended");
        return clientApplication;
    }

    public Long saveClientAndApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("saveClientAndApplication started");
        validateLoanApplicationRequestDTO(loanApplicationRequestDTO);

        Passport passport = initPassport(loanApplicationRequestDTO);
        Client client = initClient(loanApplicationRequestDTO, passport);
        Credit credit = initCredit(loanApplicationRequestDTO);
        ClientApplication clientApplication = initClientApplication(credit, client);

        creditRepository.save(credit);
        log.info("credit saved to DB");
        passportRepository.save(passport);
        log.info("passport saved to DB");
        clientRepository.save(client);
        log.info("client saved to DB");
        applicationRepository.save(clientApplication);
        log.info("clientApplication saved to DB");

        return clientApplication.getApplicationID();
    }

    public List<LoanOfferDTO> setApplicationIDToLoanOffers(Long applicationID, List<LoanOfferDTO> loanOfferDTOList) {
        log.info("setApplicationIDToLoanOffers started. applicationID: {}", applicationID);

        if (applicationID == null) {
            throw new RuntimeException("applicationID is null!");
        }
        if (loanOfferDTOList == null) {
            throw new RuntimeException("loanOfferDTOList is null!");
        }

        for (LoanOfferDTO loanOfferDTO : loanOfferDTOList) {
            loanOfferDTO.setApplicationId(applicationID);
        }
        log.info("applicationID set to loanOfferDTOs. loanOfferDTOList: {} ", loanOfferDTOList);
        return loanOfferDTOList;
    }

    public void saveChosenLoanOfferDTO(LoanOfferDTO loanOfferDTO) {
        log.info("saveChosenLoanOfferDTO started. loanOfferDTO: {}", loanOfferDTO);
        if (loanOfferDTO == null) {
            throw new RuntimeException("loanOfferDTO is null!");
        }
        if (loanOfferDTO.getApplicationId() == null) {
            throw new RuntimeException("ApplicationId in loanOfferDTO is null!");
        }
        Optional<ClientApplication> applicationOptional =
                applicationRepository.findById(loanOfferDTO.getApplicationId());
        if (applicationOptional.isEmpty()) {
            throw new ApplicationNotFoundException(loanOfferDTO.getApplicationId());
        }
        log.info("clientApplication found in DB");
        ClientApplication clientApplication = applicationOptional.get();

        List<ApplicationStatusHistory> statusHistoryList = clientApplication.getStatusHistoryList();
        ApplicationStatusHistory statusHistory = new ApplicationStatusHistory();
        statusHistory.setStatus(ApplicationStatus.APPROVED);
        statusHistory.setTime(LocalDateTime.now());
        statusHistory.setChangeType(ChangeType.MANUAL);
        statusHistoryList.add(statusHistory);
        clientApplication.setStatusHistoryList(statusHistoryList);
        clientApplication.setApplicationStatus(ApplicationStatus.APPROVED);
        clientApplication.setAppliedOffer(loanOfferDTO);
        log.info("ApplicationStatus set, statusHistoryList set");
        applicationRepository.save(clientApplication);
        log.info("clientApplication saved to DB");
        Optional<Credit> creditOptional = creditRepository.findById(clientApplication.getCredit().getCreditID());
        if (creditOptional.isEmpty()) {
            throw new RuntimeException("no credit matching to application in DB");
        }
        log.info("credit found in DB");
        Credit credit = creditOptional.get();
        credit.setInsuranceEnables(loanOfferDTO.getIsInsuranceEnabled());
        credit.setSalaryClient(loanOfferDTO.getIsSalaryClient());

        creditRepository.save(credit);
        log.info("credit saved to DB");
    }

    private void validateFinishRegistrationRequestDTO(FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
        log.info("validateFinishRegistrationRequestDTO started");
        if (finishRegistrationRequestDTO == null) {
            throw new RuntimeException("finishRegistrationRequestDTO is null!");
        }
        if (finishRegistrationRequestDTO.getAccount() == null) {
            throw new RuntimeException("account in finishRegistrationRequestDTO is null!");
        }
        if (finishRegistrationRequestDTO.getEmploymentDTO() == null) {
            throw new RuntimeException("employment in finishRegistrationRequestDTO is null!");
        }
        if (finishRegistrationRequestDTO.getGender() == null) {
            throw new RuntimeException("gender in finishRegistrationRequestDTO is null!");
        }
        if (finishRegistrationRequestDTO.getMaritalStatus() == null) {
            throw new RuntimeException("MaritalStatus in finishRegistrationRequestDTO is null!");
        }
        if (finishRegistrationRequestDTO.getPassportIssueBranch() == null) {
            throw new RuntimeException("PassportIssueBranch in finishRegistrationRequestDTO is null!");
        }
        if (finishRegistrationRequestDTO.getPassportIssueDate() == null) {
            throw new RuntimeException("PassportIssueDate() in finishRegistrationRequestDTO is null!");
        }
        if (finishRegistrationRequestDTO.getEmploymentDTO().getEmployerINN() == null) {
            throw new RuntimeException("EmployerINN in EmploymentDTO in finishRegistrationRequestDTO is null!");
        }
        if (finishRegistrationRequestDTO.getEmploymentDTO().getEmploymentStatus() == null) {
            throw new RuntimeException("EmploymentStatus in EmploymentDTO in finishRegistrationRequestDTO is null!");
        }
        if (finishRegistrationRequestDTO.getEmploymentDTO().getPosition() == null) {
            throw new RuntimeException("EmploymentPosition in EmploymentDTO in finishRegistrationRequestDTO is null!");
        }
        if (finishRegistrationRequestDTO.getEmploymentDTO().getSalary() == null) {
            throw new RuntimeException("Salary in EmploymentDTO in finishRegistrationRequestDTO is null!");
        }
        if (finishRegistrationRequestDTO.getEmploymentDTO().getWorkExperienceCurrent() == null) {
            throw new RuntimeException("WorkExperienceCurrent in EmploymentDTO in finishRegistrationRequestDTO is null!");
        }
        if (finishRegistrationRequestDTO.getEmploymentDTO().getWorkExperienceTotal() == null) {
            throw new RuntimeException("WorkExperienceTotal in EmploymentDTO in finishRegistrationRequestDTO is null!");
        }
        log.info("validateFinishRegistrationRequestDTO finished");
    }

    public ClientApplication updateDB(FinishRegistrationRequestDTO finishRegistrationRequestDTO, Long applicationID) {
        log.info("updateDB started");
        validateFinishRegistrationRequestDTO(finishRegistrationRequestDTO);

        if (applicationID == null) {
            throw new RuntimeException("applicationID is null!");
        }

        Optional<ClientApplication> clientApplicationOptional = applicationRepository.findById(applicationID);
        if (clientApplicationOptional.isEmpty()) {
            log.warn("clientApplication didn't found");
            throw new ApplicationNotFoundException(applicationID);
        }
        ClientApplication clientApplication = clientApplicationOptional.get();

        log.info("clientApplication found");
        Client client = clientApplication.getClient();
        client.setGender(finishRegistrationRequestDTO.getGender());
        client.setMaritalStatus(finishRegistrationRequestDTO.getMaritalStatus());
        client.setDependentAmount(finishRegistrationRequestDTO.getDepartmentAmount());
        client.setAccount(finishRegistrationRequestDTO.getAccount());

        Passport passport = passportRepository.getById(client.getPassport().getPassportID());
        passport.setIssueDate(finishRegistrationRequestDTO.getPassportIssueDate());
        passport.setIssueBranch(finishRegistrationRequestDTO.getPassportIssueBranch());
        passportRepository.save(passport);
        log.info("passport info updated in DB");

        Employment updatedEmployment = convertEmploymentDTOToEmployment(finishRegistrationRequestDTO.getEmploymentDTO());
        if (client.getEmployment() == null) {
            client.setEmployment(updatedEmployment);
            employmentRepository.save(updatedEmployment);
            log.info("employment info updated");
            clientRepository.save(client);
            log.info("client info updated");
        } else {
            Employment employment = employmentRepository.getById(client.getEmployment().getEmploymentID());
            employment.setEmployer(updatedEmployment.getEmployer());
            employment.setEmploymentPosition(updatedEmployment.getEmploymentPosition());
            employment.setEmploymentStatus(updatedEmployment.getEmploymentStatus());
            employment.setSalary(updatedEmployment.getSalary());
            employment.setWorkExperienceCurrent(updatedEmployment.getWorkExperienceCurrent());
            employment.setWorkExperienceTotal(updatedEmployment.getWorkExperienceTotal());
            client.setEmployment(employment);

            employmentRepository.save(employment);
            log.info("employment info updated");
            clientRepository.save(client);
            log.info("client info updated");
        }

        return clientApplication;
    }

    public Employment convertEmploymentDTOToEmployment(EmploymentDTO employmentDTO) {
        log.info("convertEmploymentDTOToEmployment started");
        Employment employment = Employment.builder()
                .employer(employmentDTO.getEmployerINN())
                .employmentStatus(employmentDTO.getEmploymentStatus())
                .salary(employmentDTO.getSalary())
                .employmentPosition(employmentDTO.getPosition())
                .workExperienceTotal(employmentDTO.getWorkExperienceTotal())
                .workExperienceCurrent(employmentDTO.getWorkExperienceCurrent())
                .build();
        log.info("convertEmploymentDTOToEmployment ended");
        return employment;
    }

    public EmploymentDTO convertEmploymentToEmploymentDTO(Employment employment) {
        log.info("convertEmploymentToEmploymentDTO started");
        EmploymentDTO employmentDTO = EmploymentDTO.builder()
                .employerINN(employment.getEmployer())
                .employmentStatus(employment.getEmploymentStatus())
                .salary(employment.getSalary())
                .position(employment.getEmploymentPosition())
                .workExperienceTotal(employment.getWorkExperienceTotal())
                .workExperienceCurrent(employment.getWorkExperienceCurrent())
                .build();
        log.info("convertEmploymentToEmploymentDTO ended");
        return employmentDTO;
    }

    public ScoringDataDTO createScoringDataDTO(ClientApplication clientApplication) {
        log.info("createScoringDataDTO started");
        if (clientApplication == null) {
            throw new RuntimeException("clientApplication is null");
        }

        ScoringDataDTO scoringDataDTO = ScoringDataDTO.builder()
                .amount(clientApplication.getCredit().getAmount())
                .term(clientApplication.getCredit().getTerm())
                .firstName(clientApplication.getClient().getFirstName())
                .lastName(clientApplication.getClient().getLastName())
                .middleName(clientApplication.getClient().getMiddleName())
                .gender(clientApplication.getClient().getGender())
                .birthday(clientApplication.getClient().getBirthDate())
                .passportSeries(clientApplication.getClient().getPassport().getSeries())
                .passportNumber(clientApplication.getClient().getPassport().getNumber())
                .passportIssueDate(clientApplication.getClient().getPassport().getIssueDate())
                .passportIssueBranch(clientApplication.getClient().getPassport().getIssueBranch())
                .maritalStatus(clientApplication.getClient().getMaritalStatus())
                .dependentAmount(clientApplication.getClient().getDependentAmount())
                .employment(convertEmploymentToEmploymentDTO(clientApplication.getClient().getEmployment()))
                .account(clientApplication.getClient().getAccount())
                .isInsuranceEnabled(clientApplication.getCredit().isInsuranceEnables())
                .isSalaryClient(clientApplication.getCredit().isSalaryClient())
                .build();
        log.info("createScoringDataDTO ended, scoringDataDTO: {} ", scoringDataDTO);
        return scoringDataDTO;
    }

    public void denieApplicationStatus(Long applicationID) {
        log.info("denieApplicationStatus started, applicationID: {}", applicationID);
        if (applicationID == null) {
            throw new RuntimeException("applicationID is null!");
        }
        Optional<ClientApplication> clientApplicationOptional = applicationRepository.findById(applicationID);
        if (clientApplicationOptional.isEmpty()) {
            throw new RuntimeException(String.format("application %d not found", applicationID));
        }
        ClientApplication clientApplication = clientApplicationOptional.get();

        List<ApplicationStatusHistory> statusHistoryList = clientApplication.getStatusHistoryList();
        ApplicationStatusHistory statusHistory = new ApplicationStatusHistory();
        statusHistory.setStatus(ApplicationStatus.CC_DENIED);
        statusHistory.setTime(LocalDateTime.now());
        statusHistory.setChangeType(ChangeType.AUTOMATIC);
        statusHistoryList.add(statusHistory);
        clientApplication.setStatusHistoryList(statusHistoryList);
        clientApplication.setApplicationStatus(ApplicationStatus.CC_DENIED);
        applicationRepository.save(clientApplication);
        log.info("denieApplicationStatus ended. ApplicationStatus.CC_DENIED and statusHistoryList saved to DB ");

    }

    public void checkApplicationStatus(Long applicationID) {
        log.info("checkApplicationStatus started, applicationID: {}", applicationID);
        if (applicationID == null) {
            throw new RuntimeException("applicationID is null!");
        }
        Optional<ClientApplication> clientApplicationOptional = applicationRepository.findById(applicationID);
        if (clientApplicationOptional.isEmpty()) {
            throw new RuntimeException(String.format("application %d not found", applicationID));
        }
        ClientApplication clientApplication = clientApplicationOptional.get();

        if (clientApplication.getApplicationStatus() != ApplicationStatus.APPROVED) {
            List<ApplicationStatusHistory> statusHistoryList = clientApplication.getStatusHistoryList();
            ApplicationStatusHistory statusHistory = new ApplicationStatusHistory();
            statusHistory.setStatus(ApplicationStatus.APPROVED);
            statusHistory.setTime(LocalDateTime.now());
            statusHistory.setChangeType(ChangeType.AUTOMATIC);
            statusHistoryList.add(statusHistory);
            clientApplication.setStatusHistoryList(statusHistoryList);
            clientApplication.setApplicationStatus(ApplicationStatus.APPROVED);
            applicationRepository.save(clientApplication);
            log.info("ApplicationStatus.APPROVEDand statusHistoryList saved to DB");
        }
        log.info("checkApplicationStatus ended");
    }

    private void validCreditDTO(CreditDTO creditDTO) {
        if (creditDTO == null) {
            throw new RuntimeException("creditDTO is null!");
        }
        if (creditDTO.getAmount() == null) {
            throw new RuntimeException(" amount in creditDTO is null!");
        }
        if (creditDTO.getIsInsuranceEnabled() == null) {
            throw new RuntimeException("isInsuranceEnabled in creditDTO is null!");
        }
        if (creditDTO.getIsSalaryClient() == null) {
            throw new RuntimeException("isSalaryClient in creditDTO is null!");
        }
        if (creditDTO.getMonthlyPayment() == null) {
            throw new RuntimeException("monthlyPayment in creditDTO is null!");
        }
        if (creditDTO.getPaymentSchedule() == null) {
            throw new RuntimeException("PaymentSchedule in creditDTO is null!");
        }
        if (creditDTO.getPsk() == null) {
            throw new RuntimeException("Psk in creditDTO is null!");
        }
        if (creditDTO.getRate() == null) {
            throw new RuntimeException("rate in creditDTO is null!");
        }
        if (creditDTO.getTerm() == null) {
            throw new RuntimeException("term in creditDTO is null!");
        }
    }

    public void saveCredit(CreditDTO creditDTO, Long applicationID) {
        log.info("saveCredit started");

        validCreditDTO(creditDTO);

        if (applicationID == null) {
            throw new RuntimeException("applicationID is null!");
        }

        Optional<ClientApplication> applicationOptional =
                applicationRepository.findById(applicationID);
        if (applicationOptional.isEmpty()) {
            throw new ApplicationNotFoundException(applicationID);
        }
        log.info("clientApplication found in DB");
        ClientApplication clientApplication = applicationOptional.get();

        Credit credit = clientApplication.getCredit();
        credit.setCreditStatus(CreditStatus.CALCULATED);
        credit.setInsuranceEnables(creditDTO.getIsInsuranceEnabled());
        credit.setSalaryClient(creditDTO.getIsSalaryClient());
        credit.setMonthlyPayment(creditDTO.getMonthlyPayment());
        credit.setAmount(creditDTO.getAmount());
        credit.setPsk(creditDTO.getPsk());
        credit.setPaymentSchedule(creditDTO.getPaymentSchedule());
        credit.setRate(creditDTO.getRate());
        credit.setTerm(creditDTO.getTerm());

        creditRepository.save(credit);
        log.info("saveCredit ended");
    }
}
