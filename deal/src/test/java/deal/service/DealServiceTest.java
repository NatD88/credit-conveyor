package deal.service;


import deal.dto.*;
import deal.entity.*;
import deal.repository.*;
import deal.util.ApplicationNotFoundException;
import deal.util.ApplicationStatus;
import deal.util.ThemeEmail;
import net.bytebuddy.dynamic.DynamicType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DealServiceTest {

    @Mock
    private LoanApplicationRequestDTO mockLoanApplicationRequestDTO;

    @Mock
    private Passport mockPassport;
    @Mock
    Credit mockCredit;
    @Mock
    Client mockClient;
    @Mock
    ApplicationRepository mockApplicationRepository;
    @Mock
    ClientRepository mockClientRepository;
    @Mock
    PassportRepository mockPassportRepository;
    @Mock
    CreditRepository mockCreditRepository;
    @Mock
    EmploymentRepository mockEmploymentRepository;

    @Autowired
    private DealService dealService;

    @Test
    void initPassport() {
        Passport passport = dealService.initPassport(mockLoanApplicationRequestDTO);
        Assertions.assertNotNull(passport);
    }

    @Test
    void initClient() {
        Client client = dealService.initClient(mockLoanApplicationRequestDTO, mockPassport);
        Assertions.assertNotNull(client);
    }

    @Test
    void initCredit() {
        Credit credit = dealService.initCredit(mockLoanApplicationRequestDTO);
        Assertions.assertNotNull(credit);
    }

    @Test
    void initStatusHistoryList() {
        List<ApplicationStatusHistory> list = dealService.initStatusHistoryList();
        Assertions.assertNotNull(list);
    }

    @Test
    void initClientApplication() {
        ClientApplication clientApplication = dealService.initClientApplication(mockCredit, mockClient);
        Assertions.assertNotNull(clientApplication);
    }

    @Test
    void saveClientAndApplication() {
        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO(new BigDecimal(15000),
                6,
                "Max",
                "Ivanov",
                "jytjt",
                "fhwehi@mail.ru",
                LocalDate.of(1991, 11, 5),
                "1245",
                "123456");
        Assertions.assertNotNull(dealService.saveClientAndApplication(loanApplicationRequestDTO));
    }

    @Test
    void setApplicationIDToLoanOffers() {
        Assertions.assertThrows(RuntimeException.class, () -> dealService.setApplicationIDToLoanOffers(15L, null));
    }

    @Test
    void saveChosenLoanOfferDTO() {
        Assertions.assertThrows(RuntimeException.class, () -> dealService.saveChosenLoanOfferDTO(null));
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        loanOfferDTO.setApplicationId(15L);
        Assertions.assertThrows(ApplicationNotFoundException.class, () -> dealService.saveChosenLoanOfferDTO(loanOfferDTO));
    }

    @Test
    void updateDB() {
        Assertions.assertThrows(RuntimeException.class, () -> dealService.updateDB(null, 55L));
        Assertions.assertThrows(RuntimeException.class, () -> dealService.updateDB(new FinishRegistrationRequestDTO(), null));
    }

    ClientApplication initAllInClientApplication() {
        Passport passport = Passport.builder()
                .series("1234")
                .number("262626")
                .issueBranch("wfqw4tgq")
                .issueDate(LocalDate.of(1900,10,10))
                .build();
        Employment employment = Employment.builder()
                .employer("12312313")
                .employmentPosition(EmploymentPosition.MID_MANAGER)
                .employmentStatus(EmploymentStatus.SELF_EMPLOYED)
                .salary(new BigDecimal(80000))
                .workExperienceCurrent(15)
                .workExperienceTotal(35)
                .build();
        Client client = Client.builder()
                .account("12345678965478987456")
                .birthDate(LocalDate.of(1990,10,10))
                .dependentAmount(1)
                .email("sfsef@sf.r")
                .employment(employment)
                .firstName("sdfsrf")
                .lastName("sgdrge")
                .middleName("fwefrqwe")
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.MARRIED)
                .passport(passport)
                .build();
        Credit credit = Credit.builder()
                .amount(new BigDecimal(100000))
                .term(15)
                .isInsuranceEnables(true)
                .isSalaryClient(false)
                .build();
        ClientApplication clientApplication = ClientApplication.builder()
                .applicationID(15L)
                .client(client)
                .credit(credit)
                .ses_code(2453)
                .statusHistoryList(new ArrayList<>())
                .build();

        return clientApplication;
    }

    @Test
    void createScoringDataDTO() {
        Assertions.assertThrows(RuntimeException.class, () -> dealService.createScoringDataDTO(null));
        ScoringDataDTO scoringDataDTO = dealService.createScoringDataDTO(initAllInClientApplication());
        Assertions.assertNotNull(scoringDataDTO);
        Assertions.assertEquals(Gender.MALE, scoringDataDTO.getGender());
        Assertions.assertEquals(15, scoringDataDTO.getTerm());
    }

    @Test
    void denieApplicationStatus() {
        Assertions.assertThrows(RuntimeException.class, () -> dealService.denieApplicationStatus(null));
        ClientApplication clientApplication = initAllInClientApplication();
        Optional<ClientApplication> optionalClientApplication = Optional.of(clientApplication);
        DealService dealServiceWithMocks = new DealService(mockClientRepository, mockPassportRepository,mockCreditRepository,
                mockApplicationRepository, mockEmploymentRepository);
        Mockito.when(mockApplicationRepository.findById(Mockito.any())).thenReturn(optionalClientApplication);
        dealServiceWithMocks.denieApplicationStatus(15L);
        Assertions.assertEquals(ApplicationStatus.CC_DENIED, optionalClientApplication.get().getApplicationStatus());
    }

    @Test
    void checkApplicationStatus() {
        Assertions.assertThrows(RuntimeException.class, () -> dealService.checkApplicationStatus(null));
        ClientApplication clientApplication = initAllInClientApplication();
        Optional<ClientApplication> optionalClientApplication = Optional.of(clientApplication);
        DealService dealServiceWithMocks = new DealService(mockClientRepository, mockPassportRepository,mockCreditRepository,
                mockApplicationRepository, mockEmploymentRepository);
        Mockito.when(mockApplicationRepository.findById(Mockito.any())).thenReturn(optionalClientApplication);
        dealServiceWithMocks.checkApplicationStatus(15L);
        Assertions.assertEquals(ApplicationStatus.APPROVED, optionalClientApplication.get().getApplicationStatus());
    }

    @Test
    void createEmail() {

        DealService dealServiceWithMocks = new DealService(mockClientRepository, mockPassportRepository,mockCreditRepository,
                mockApplicationRepository, mockEmploymentRepository);
        ClientApplication clientApplication = initAllInClientApplication();
        Optional<ClientApplication> optionalClientApplication = Optional.of(clientApplication);
        Mockito.when(mockApplicationRepository.findById(Mockito.any())).thenReturn(optionalClientApplication);
        EmailMessage emailMessage = dealServiceWithMocks.createEmail(ThemeEmail.CREATE_DOCUMENT, 15L);
        Assertions.assertEquals("sfsef@sf.r", emailMessage.getAddress());
        Assertions.assertEquals(ThemeEmail.CREATE_DOCUMENT, emailMessage.getThemeEmail());
    }

    @Test
    void getClientApplication() {
        DealService dealServiceWithMocks = new DealService(mockClientRepository, mockPassportRepository,mockCreditRepository,
                mockApplicationRepository, mockEmploymentRepository);
        Mockito.when(mockApplicationRepository.findById(Mockito.any())).thenReturn(null);
        Assertions.assertThrows(ApplicationNotFoundException.class, () -> dealService.getClientApplication(15L));
    }


    @Test
    void checkSesCode() {
        DealService dealServiceWithMocks = new DealService(mockClientRepository, mockPassportRepository,mockCreditRepository,
                mockApplicationRepository, mockEmploymentRepository);
        ClientApplication clientApplication = initAllInClientApplication();
        Optional<ClientApplication> optionalClientApplication = Optional.of(clientApplication);
        Mockito.when(mockApplicationRepository.findById(Mockito.any())).thenReturn(optionalClientApplication);
        Assertions.assertTrue(dealServiceWithMocks.checkSesCode(15L, 2453));
    }

}