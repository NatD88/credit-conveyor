package dossier.util;

import dossier.dto.ClientApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ValidatorClientApplication {

    public void validate(ClientApplication clientApplication) {
        log.info("clientApplication validation started");
        if (clientApplication == null) {
            throw new RuntimeException("clientApplication is null!");
        }
        if (clientApplication.getApplicationID() == null) {
            throw new RuntimeException("ApplicationID in clientApplication is null!");
        }
        if (clientApplication.getApplicationStatus() == null) {
            throw new RuntimeException("ApplicationStatus in clientApplication is null!");
        }
        if (clientApplication.getClient() == null) {
            throw new RuntimeException("Client in clientApplication is null!");
        }
        if (clientApplication.getClient().getBirthDate() == null) {
            throw new RuntimeException("Client BirthDate in clientApplication is null!");
        }
        if (clientApplication.getClient().getEmail() == null) {
            throw new RuntimeException("Client email in clientApplication is null!");
        }
        if (clientApplication.getClient().getEmployment() == null) {
            throw new RuntimeException("Client employment in clientApplication is null!");
        }
        if (clientApplication.getClient().getEmployment().getEmployer() == null) {
            throw new RuntimeException("Client Employer in employment in clientApplication is null!");
        }
        if (clientApplication.getClient().getEmployment().getEmploymentPosition() == null) {
            throw new RuntimeException("Client EmploymentPosition in employment in clientApplication is null!");
        }
        if (clientApplication.getClient().getEmployment().getEmploymentStatus() == null) {
            throw new RuntimeException("Client EmploymentStatus in employment in clientApplication is null!");
        }
        if (clientApplication.getClient().getEmployment().getSalary() == null) {
            throw new RuntimeException("Client salary in employment in clientApplication is null!");
        }
        if (clientApplication.getClient().getFirstName() == null) {
            throw new RuntimeException("Client FirstName in clientApplication is null!");
        }
        if (clientApplication.getClient().getGender() == null) {
            throw new RuntimeException("Client Gender in clientApplication is null!");
        }
        if (clientApplication.getClient().getLastName() == null) {
            throw new RuntimeException("Client LastName in clientApplication is null!");
        }
        if (clientApplication.getClient().getMaritalStatus() == null) {
            throw new RuntimeException("Client MaritalStatus in clientApplication is null!");
        }
        if (clientApplication.getClient().getPassport() == null) {
            throw new RuntimeException("Client passport in clientApplication is null!");
        }
        if (clientApplication.getClient().getPassport().getIssueBranch() == null) {
            throw new RuntimeException("Client IssueBranch in passport in clientApplication is null!");
        }
        if (clientApplication.getClient().getPassport().getIssueDate() == null) {
            throw new RuntimeException("Client IssueDate in passport in clientApplication is null!");
        }
        if (clientApplication.getClient().getPassport().getNumber() == null) {
            throw new RuntimeException("Client passport Number in clientApplication is null!");
        }
        if (clientApplication.getClient().getPassport().getSeries() == null) {
            throw new RuntimeException("Client passport series in clientApplication is null!");
        }
        if (clientApplication.getCredit() == null) {
            throw new RuntimeException("credit in clientApplication is null!");
        }
        if (clientApplication.getCredit().getAmount() == null) {
            throw new RuntimeException("credit Amount in clientApplication is null!");
        }
        if (clientApplication.getCredit().getCreditStatus() == null) {
            throw new RuntimeException("credit status in credit in clientApplication is null!");
        }
        if (clientApplication.getCredit().getMonthlyPayment() == null) {
            throw new RuntimeException("credit MonthlyPayment in clientApplication is null!");
        }
        if (clientApplication.getCredit().getPaymentSchedule() == null) {
            throw new RuntimeException("credit PaymentSchedule in clientApplication is null!");
        }
        if (clientApplication.getCredit().getPsk() == null) {
            throw new RuntimeException("psk in credit in clientApplication is null!");
        }
        if (clientApplication.getCredit().getRate() == null) {
            throw new RuntimeException("rate in credit in clientApplication is null!");
        }
        log.info("clientApplication validation finished successfully");
    }
}
