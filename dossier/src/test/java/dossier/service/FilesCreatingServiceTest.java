package dossier.service;

import dossier.dto.*;
import dossier.util.ValidatorClientApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilesCreatingServiceTest {

    @MockBean
    ValidatorClientApplication validatorClientApplication;

    FilesCreatingService filesCreatingService = new FilesCreatingService(validatorClientApplication);

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
        List<PaymentScheduleElement> paymentList = new ArrayList<>();
        PaymentScheduleElement paymentScheduleElement = PaymentScheduleElement.builder()
                .date(LocalDate.now())
                .debtPayment(new BigDecimal(100))
                .interestPayment(new BigDecimal(100))
                .number(1)
                .remainingDebt(new BigDecimal(100))
                .totalPayment(new BigDecimal(100))
                .build();
        paymentList.add(paymentScheduleElement);
        Credit credit = Credit.builder()
                .amount(new BigDecimal(100000))
                .term(15)
                .isInsuranceEnables(true)
                .isSalaryClient(false)
                .paymentSchedule(paymentList)
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
    void createClientAppFile() {
        ClientApplication clientApplication = initAllInClientApplication();
        String fileName = filesCreatingService.createClientAppFile(clientApplication);
        File file = new File(fileName);
        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.isFile());
    }

    @Test
    void createCreditContractFile() {
        ClientApplication clientApplication = initAllInClientApplication();
        String fileName = filesCreatingService.createCreditContractFile(clientApplication);
        File file = new File(fileName);
        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.isFile());
    }

    @Test
    void createPaymentScheduleFile() {
        ClientApplication clientApplication = initAllInClientApplication();
        String fileName = filesCreatingService.createPaymentScheduleFile(clientApplication);
        File file = new File(fileName);
        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.isFile());
    }
}