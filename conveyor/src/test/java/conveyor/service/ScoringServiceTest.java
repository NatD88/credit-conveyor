package conveyor.service;

import conveyor.dto.EmploymentDTO;
import conveyor.dto.ScoringDataDTO;
import conveyor.dto.EmploymentStatus;
import conveyor.dto.MaritalStatus;
import conveyor.util.BooleanAndRate;
import conveyor.util.CreateAndInitTestDTOs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScoringServiceTest {

    @Autowired
    private Environment env;
    private BigDecimal baseRate;

    @Autowired
    ScoringService scoringService;

    private ScoringDataDTO scoringDataDTO;
    private  EmploymentDTO employmentDTO;

    @BeforeEach
     void initScoringDataDTO() {
        baseRate = new BigDecimal(Integer.parseInt(Objects.requireNonNull(env.getProperty("baseRate"))));
        scoringDataDTO = CreateAndInitTestDTOs.createAndInitTestScoringDataDTOWithEmployment();
        employmentDTO = CreateAndInitTestDTOs.createAndInitTestEmploymentDTO();
    }

    @Test
    void executeScoringNulls(){
        assertThrows(RuntimeException.class, () -> scoringService.executeScoring(null));
        scoringDataDTO.setAmount(null);
        assertThrows(RuntimeException.class, () -> scoringService.executeScoring(scoringDataDTO));
        scoringDataDTO = CreateAndInitTestDTOs.createAndInitTestScoringDataDTOWithEmployment();
        scoringDataDTO.setBirthday(null);
        assertThrows(RuntimeException.class, () -> scoringService.executeScoring(scoringDataDTO));
        scoringDataDTO = CreateAndInitTestDTOs.createAndInitTestScoringDataDTOWithEmployment();
        scoringDataDTO.setGender(null);
        assertThrows(RuntimeException.class, () -> scoringService.executeScoring(scoringDataDTO));
        scoringDataDTO = CreateAndInitTestDTOs.createAndInitTestScoringDataDTOWithEmployment();
        scoringDataDTO.setMaritalStatus(null);
        assertThrows(RuntimeException.class, () -> scoringService.executeScoring(scoringDataDTO));
        scoringDataDTO = CreateAndInitTestDTOs.createAndInitTestScoringDataDTOWithEmployment();
        scoringDataDTO.setDependentAmount(null);
        assertThrows(RuntimeException.class, () -> scoringService.executeScoring(scoringDataDTO));
        scoringDataDTO = CreateAndInitTestDTOs.createAndInitTestScoringDataDTOWithEmployment();
        scoringDataDTO.setIsInsuranceEnabled(null);
        assertThrows(RuntimeException.class, () -> scoringService.executeScoring(scoringDataDTO));
        scoringDataDTO = CreateAndInitTestDTOs.createAndInitTestScoringDataDTOWithEmployment();
        scoringDataDTO.setIsSalaryClient(null);
        assertThrows(RuntimeException.class, () -> scoringService.executeScoring(scoringDataDTO));

    }

    @Test
    void executeScoringNoRateChanges() {
        BooleanAndRate booleanAndRate = scoringService.executeScoring(scoringDataDTO);
        assertEquals(baseRate, booleanAndRate.getRate());
        assertTrue(booleanAndRate.isOk());
    }

    @Test
    void executeScoringMarried() {
        scoringDataDTO.setMaritalStatus(MaritalStatus.MARRIED);
        BooleanAndRate booleanAndRate = scoringService.executeScoring(scoringDataDTO);
        assertEquals(baseRate.subtract(new BigDecimal(3)),booleanAndRate.getRate());
        assertTrue(booleanAndRate.isOk());
    }

    @Test
    void executeScoringDivorced() {
        scoringDataDTO.setMaritalStatus(MaritalStatus.DIVORCED);
        BooleanAndRate booleanAndRate = scoringService.executeScoring(scoringDataDTO);
        assertEquals(baseRate.add(new BigDecimal(1)),booleanAndRate.getRate());
        assertTrue(booleanAndRate.isOk());
    }

    @Test
    void executeScoringIsInsuranceEnabled() {
        scoringDataDTO.setIsInsuranceEnabled(true);
        BooleanAndRate booleanAndRate = scoringService.executeScoring(scoringDataDTO);
        assertEquals(baseRate.subtract(new BigDecimal(3)), booleanAndRate.getRate());
        assertTrue(booleanAndRate.isOk());
    }

    @Test
    void executeScoringAgeLessThen20() {
        scoringDataDTO.setBirthday(LocalDate.of(2003,10,1));
        BooleanAndRate booleanAndRate = scoringService.executeScoring(scoringDataDTO);
        assertFalse(booleanAndRate.isOk());
    }

    @Test
    void executeScoringSmallSalary() {
        employmentDTO.setSalary(new BigDecimal(100));
        scoringDataDTO.setEmployment(employmentDTO);
        BooleanAndRate booleanAndRate = scoringService.executeScoring(scoringDataDTO);
        assertFalse(booleanAndRate.isOk());
    }

    @Test
    void executeScoringSmallWorkExperienceCurrent() {
        employmentDTO.setWorkExperienceCurrent(2);
        scoringDataDTO.setEmployment(employmentDTO);
        BooleanAndRate booleanAndRate = scoringService.executeScoring(scoringDataDTO);
        assertFalse(booleanAndRate.isOk());
    }

    @Test
    void executeScoringSmallWorkExperienceTotal() {
        employmentDTO.setWorkExperienceTotal(10);
        scoringDataDTO.setEmployment(employmentDTO);
        BooleanAndRate booleanAndRate = scoringService.executeScoring(scoringDataDTO);
        assertFalse(booleanAndRate.isOk());
    }

    @Test
    void executeScoringUnemployed() {
        employmentDTO.setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        scoringDataDTO.setEmployment(employmentDTO);
        BooleanAndRate booleanAndRate = scoringService.executeScoring(scoringDataDTO);
        assertFalse(booleanAndRate.isOk());
    }
}