package conveyor.service;

import conveyor.dto.ScoringDataDTO;
import conveyor.entity.EmploymentPosition;
import conveyor.entity.EmploymentStatus;
import conveyor.entity.Gender;
import conveyor.entity.MaritalStatus;
import conveyor.util.BooleanAndRate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

@Service
@Slf4j
public class ScoringService {

    @Autowired
    private Environment env;

    public BooleanAndRate executeScoring (ScoringDataDTO scoringDataDTO) {
        log.info("scoring starts");
        if (scoringDataDTO == null) {
            throw new RuntimeException("scoringDataDTO is null!");
        }
        if (scoringDataDTO.getIsInsuranceEnabled() == null) {
            throw new RuntimeException("isInsuranceEnabled in scoringDataDTO is null!");
        }
        if (scoringDataDTO.getIsSalaryClient() == null) {
            throw new RuntimeException("isSalaryClient in scoringDataDTO is null!");
        }
        if (scoringDataDTO.getEmployment() == null) {
            throw new RuntimeException("Employment in scoringDataDTO is null!");
        }
        if (scoringDataDTO.getEmployment().getEmploymentStatus() == null) {
            throw new RuntimeException("EmploymentStatus in Employment in scoringDataDTO is null!");
        }
        if (scoringDataDTO.getEmployment().getPosition() == null) {
            throw new RuntimeException("Position in Employment in scoringDataDTO is null!");
        }
        if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() == null) {
            throw new RuntimeException("WorkExperienceCurrent in Employment in scoringDataDTO is null!");
        }
        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() == null) {
            throw new RuntimeException("WorkExperienceTotal in Employment in scoringDataDTO is null!");
        }
        if (scoringDataDTO.getEmployment().getSalary() == null) {
            throw new RuntimeException("Salary in Employment in scoringDataDTO is null!");
        }
        if (scoringDataDTO.getMaritalStatus() == null) {
            throw new RuntimeException("MaritalStatus in scoringDataDTO is null!");
        }
        if (scoringDataDTO.getDependentAmount() == null) {
            throw new RuntimeException("DependentAmount in scoringDataDTO is null!");
        }
        if (scoringDataDTO.getBirthday() == null) {
            throw new RuntimeException("Birthday in scoringDataDTO is null!");
        }
        if (scoringDataDTO.getGender()== null) {
            throw new RuntimeException("Gender in scoringDataDTO is null!");
        }

        BigDecimal baseRate = new BigDecimal(Integer.parseInt(Objects.requireNonNull(env.getProperty("baseRate"))));
        BigDecimal rate = baseRate;

        if (scoringDataDTO.getIsInsuranceEnabled()) {
            rate = rate.subtract(new BigDecimal(3));
        }

        if (scoringDataDTO.getIsSalaryClient()) {
            rate = rate.subtract(new BigDecimal(1));
        }

        if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            return new BooleanAndRate(false, rate);
        }

        if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.SELF_EMPLOYED) {
            rate = rate.add(new BigDecimal(1));
        }

        if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.BUSINESS_OWNER) {
            rate = rate.add(new BigDecimal(3));
        }

        if (scoringDataDTO.getEmployment().getPosition() == EmploymentPosition.MIDDLE_MANAGER) {
            rate = rate.subtract(new BigDecimal(2));
        }

        if (scoringDataDTO.getEmployment().getPosition() == EmploymentPosition.TOP_MANAGER) {
            rate = rate.subtract(new BigDecimal(4));
        }

        if (scoringDataDTO.getAmount().compareTo(scoringDataDTO.getEmployment().getSalary().multiply(new BigDecimal(20))) >= 0) {
            return new BooleanAndRate(false, rate);
        }

        if (scoringDataDTO.getMaritalStatus() == MaritalStatus.MARRIED) {
            rate = rate.subtract(new BigDecimal(3));
        }

        if (scoringDataDTO.getMaritalStatus() == MaritalStatus.DIVORCED) {
            rate = rate.add(new BigDecimal(1));
        }

        if (scoringDataDTO.getDependentAmount() > 1) {
            rate = rate.add(new BigDecimal(1));
        }

        Period period = Period.between(scoringDataDTO.getBirthday(), LocalDate.now());
        if ( period.getYears() < 20) {
            return new BooleanAndRate(false, rate);
        }

        if (period.getYears() > 60) {
            return new BooleanAndRate(false, rate);
        }

        if (scoringDataDTO.getGender() == Gender.FEMALE &&
                period.getYears() > 35 &&
                period.getYears() < 60) {
            rate = rate.subtract(new BigDecimal(3));
        }

        if (scoringDataDTO.getGender() == Gender.MALE &&
                period.getYears() > 30 &&
                period.getYears() < 55) {
            rate = rate.subtract(new BigDecimal(3));
        }

        if (scoringDataDTO.getGender() == Gender.OTHER) {
            rate = rate.add(new BigDecimal(3));
        }

        if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3) {
            return new BooleanAndRate(false, rate);
        }

        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() < 12) {
            return new BooleanAndRate(false, rate);
        }

        log.info("scoring ends, rate: {}", rate);
        return new BooleanAndRate(true, rate);
    }
}
