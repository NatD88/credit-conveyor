package conveyor.util;

import conveyor.dto.EmploymentDTO;
import conveyor.dto.LoanApplicationRequestDTO;
import conveyor.dto.ScoringDataDTO;
import conveyor.entity.EmploymentPosition;
import conveyor.entity.EmploymentStatus;
import conveyor.entity.Gender;
import conveyor.entity.MaritalStatus;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateAndInitTestDTOs {

    public static LoanApplicationRequestDTO createAndInitLoanApplicationRequestDTO() {
        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();
        loanApplicationRequestDTO.setAmount(new BigDecimal(15000));
        loanApplicationRequestDTO.setBirthday(LocalDate.of(1990, 12, 1));
        loanApplicationRequestDTO.setEmail("sefsae@mail.ru");
        loanApplicationRequestDTO.setFirstName("Ivanov");
        loanApplicationRequestDTO.setMiddleName("");
        loanApplicationRequestDTO.setPassportNumber("123456");
        loanApplicationRequestDTO.setPassportSeries("1234");
        loanApplicationRequestDTO.setTerm(10);

        return loanApplicationRequestDTO;
    }

    public static ScoringDataDTO createAndInitTestScoringDataDTOWithEmployment() {

        ScoringDataDTO scoringDataDTO = createAndInitTestScoringDataDTO();
        EmploymentDTO employmentDTO = createAndInitTestEmploymentDTO();
        scoringDataDTO.setEmployment(employmentDTO);

        return scoringDataDTO;
    }

    public static EmploymentDTO createAndInitTestEmploymentDTO() {
        EmploymentDTO employmentDTO = new EmploymentDTO();
        employmentDTO.setEmployerINN("123456789");
        employmentDTO.setEmploymentStatus(EmploymentStatus.WORKING);
        employmentDTO.setPosition(EmploymentPosition.OFFICE_WORKER);
        employmentDTO.setSalary(new BigDecimal(50000));
        employmentDTO.setWorkExperienceCurrent(6);
        employmentDTO.setWorkExperienceTotal(36);

        return employmentDTO;
    }

    public static ScoringDataDTO createAndInitTestScoringDataDTO() {
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();

        scoringDataDTO.setAccount("12345678912345678987");
        scoringDataDTO.setAmount(new BigDecimal(50000));
        scoringDataDTO.setBirthday(LocalDate.of(2000, 11, 11));
        scoringDataDTO.setDependentAmount(1);

        EmploymentDTO employmentDTO = Mockito.mock(EmploymentDTO.class);

        scoringDataDTO.setEmployment(employmentDTO);
        scoringDataDTO.setFirstName("Max");
        scoringDataDTO.setGender(Gender.MALE);
        scoringDataDTO.setIsInsuranceEnabled(false);
        scoringDataDTO.setIsSalaryClient(false);
        scoringDataDTO.setLastName("Ivanov");
        scoringDataDTO.setMaritalStatus(MaritalStatus.NOT_MARRIED);
        scoringDataDTO.setMiddleName("");
        scoringDataDTO.setPassportIssueBranch("ROVD 1");
        scoringDataDTO.setPassportIssueDate(LocalDate.of(2020, 10, 5));
        scoringDataDTO.setPassportNumber("123456");
        scoringDataDTO.setPassportSeries("1234");
        scoringDataDTO.setTerm(7);

        return scoringDataDTO;
    }
}
