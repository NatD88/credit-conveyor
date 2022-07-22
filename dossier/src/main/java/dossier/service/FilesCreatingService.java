package dossier.service;

import dossier.dto.*;

import dossier.util.ValidatorClientApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilesCreatingService {

    private final ValidatorClientApplication validatorClientApplication;

    public String createClientAppFile(ClientApplication clientApplication) {
        log.info("clientApplication entered method createClientAppFile in FilesCreatingService ");
        String fileName = "clientApp.txt";
        try (FileWriter writer = new FileWriter(fileName, false)) {
            log.info("creating file started");
            String text = "Client application №" + clientApplication.getApplicationID() + " from " + LocalDateTime.now();
            writer.write(text);
            writer.append('\n');
            text = "Client info: ";
            writer.write(text);
            writer.append('\n');
            Client client = clientApplication.getClient();
            text = "firstname: " + client.getFirstName();
            writer.write(text);
            writer.append('\n');
            text = "lastname: " + client.getLastName();
            writer.write(text);
            writer.append('\n');
            if (client.getMiddleName() != null && !client.getMiddleName().equals("")) {
                text = "middle name: " + client.getMiddleName();
                writer.write(text);
                writer.append('\n');
            }
            text = "birthday: " + client.getBirthDate();
            writer.write(text);
            writer.append('\n');
            text = "email: " + client.getEmail();
            writer.write(text);
            writer.append('\n');
            text = "gender: " + client.getGender();
            writer.write(text);
            writer.append('\n');
            text = "marital status: " + client.getMaritalStatus();
            writer.write(text);
            writer.append('\n');
            text = "dependent amount " + client.getDependentAmount();
            writer.write(text);
            writer.append('\n');
            Passport passport = client.getPassport();
            text = "passport: " + passport.getSeries() + " " + passport.getNumber() + " " + passport.getIssueBranch() + " " + passport.getIssueDate();
            writer.write(text);
            writer.append('\n');
            Employment employment = client.getEmployment();
            text = "Employment: " + employment.getEmployer() + " " + employment.getEmploymentPosition() + " " + employment.getEmploymentStatus();
            writer.write(text);
            writer.append('\n');
            text = "Salary: " + employment.getSalary();
            writer.write(text);
            writer.append('\n');
            text = "work experience current" + " " + employment.getWorkExperienceCurrent();
            writer.write(text);
            writer.append('\n');
            text = "work experience total" + " " + employment.getWorkExperienceTotal();
            writer.write(text);

            writer.flush();
        } catch (IOException ex) {
            log.error("IOException during creating Client application file");
            System.out.println(ex.getMessage());
        }
        log.info("file with Client application successfully created. fileName: {}", fileName);
        return fileName;
    }

    public String createCreditContractFile(ClientApplication clientApplication) {
        log.info("clientApplication entered method createCreditContractFile in FilesCreatingService ");
        String fileName = "creditContract.txt";
        try (FileWriter writer = new FileWriter(fileName, false)) {
            log.info("creating file started");
            String text = "Credit contract №" + clientApplication.getApplicationID() + " from " + LocalDateTime.now();
            writer.write(text);
            writer.append('\n');
            Client client = clientApplication.getClient();
            text = "Client: " + client.getFirstName() + " " + client.getLastName();
            if (client.getMiddleName() != null && !client.getMiddleName().equals("")) {
                text = text + " " + client.getMiddleName();
            }
            text = text + " " + client.getBirthDate();
            writer.write(text);
            writer.append('\n');
            text = "Credit info:";
            writer.write(text);
            writer.append('\n');
            Credit credit = clientApplication.getCredit();
            text = " Amount: " + credit.getAmount();
            writer.write(text);
            writer.append('\n');
            text = " Term: " + credit.getTerm();
            writer.write(text);
            writer.append('\n');
            text = " Monthly payment: " + credit.getMonthlyPayment();
            writer.write(text);
            writer.append('\n');
            text = " Rate: " + credit.getRate();
            writer.write(text);
            writer.append('\n');
            text = " PSK: " + credit.getPsk();
            writer.write(text);
            writer.append('\n');
            text = " Services: ";
            writer.write(text);
            writer.append('\n');
            text = "   insurance: " + credit.isInsuranceEnables();
            writer.write(text);
            writer.append('\n');
            text = "   is salary client: " + credit.isSalaryClient();
            writer.write(text);
            writer.append('\n');
            writer.flush();

        } catch (IOException ex) {
            log.error("IOException during creating Credit contract file");
            System.out.println(ex.getMessage());
        }
        log.info("file with Credit contract successfully created. fileName: {}", fileName);
        return fileName;
    }

    public String createPaymentScheduleFile(ClientApplication clientApplication) {
        log.info("clientApplication entered method createPaymentScheduleFile in FilesCreatingService ");
        String fileName = "PaymentSchedule.txt";
        try (FileWriter writer = new FileWriter(fileName, false)) {
            log.info("creating file started");
            String text = "Payment Schedule for contract №" + clientApplication.getApplicationID() + " from " + LocalDateTime.now();
            writer.write(text);
            writer.append('\n');
            Credit credit = clientApplication.getCredit();
            List<PaymentScheduleElement> payments = credit.getPaymentSchedule();
            for (PaymentScheduleElement payment : payments) {
                text = "№" + payment.getNumber();
                writer.write(text);
                writer.append('\n');
                text = "Date: " + payment.getDate();
                writer.write(text);
                writer.append('\n');
                text = "Total Payment: " + payment.getTotalPayment();
                writer.write(text);
                writer.append('\n');
                text = "Interest Payment: " + payment.getInterestPayment();
                writer.write(text);
                writer.append('\n');
                text = "Debt Payment: " + payment.getDebtPayment();
                writer.write(text);
                writer.append('\n');
                text = "Remain: " + payment.getRemainingDebt();
                writer.write(text);
                writer.append('\n');
                writer.append('\n');
            }
        } catch (IOException ex) {
            log.error("IOException during creating Payment Schedule file");
            System.out.println(ex.getMessage());
        }
        log.info("file with Payment Schedule successfully created. fileName: {}", fileName);

        return fileName;
    }

    public List<String> createFiles(ClientApplication clientApplication) {
        log.info("clientApplication entered method createFiles in FilesCreatingService ");
        validatorClientApplication.validate(clientApplication);
        List<String> list = new ArrayList<>();
        list.add(createClientAppFile(clientApplication));
        list.add(createCreditContractFile(clientApplication));
        list.add(createPaymentScheduleFile(clientApplication));
        log.info("all files successfully created. fileNames: {}", list);
        return list;
    }
}
