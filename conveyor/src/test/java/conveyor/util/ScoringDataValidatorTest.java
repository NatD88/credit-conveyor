package conveyor.util;

import conveyor.dto.CreditDTO;
import conveyor.dto.ScoringDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScoringDataValidatorTest {

    @Autowired
    private ScoringDataValidator scoringDataValidator;

    private Errors errors;
    private ScoringDataDTO scoringDataDTO;

    @Test
    void supports() {

        assertTrue(scoringDataValidator.supports(ScoringDataDTO.class));
        assertFalse(scoringDataValidator.supports(CreditDTO.class));
    }

    @BeforeEach
    public void initLoanApplicationRequestDTO() {
        scoringDataDTO = CreateAndInitTestDTOs.createAndInitTestScoringDataDTO();
        errors = new BeanPropertyBindingResult(scoringDataDTO, "scoringDataDTO");
    }

    @Test
    void validateNoErrors() {
        scoringDataValidator.validate(scoringDataDTO,errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    void validateMiddleNameOneSymbol() {
        scoringDataDTO.setMiddleName("а");
        scoringDataValidator.validate(scoringDataDTO,errors);
        List<ObjectError> list =  errors.getAllErrors();
        FieldError fieldError = (FieldError) list.get(0);
        assertEquals(fieldError.getField(),"middleName");
        assertEquals( fieldError.getDefaultMessage(),"Отчество (при наличии) должно содержать от 2 до 30 символов");
    }

    @Test
    void validateMiddleNameNotValidSymbols() {
        scoringDataDTO.setMiddleName("dvfgs678vds");
        scoringDataValidator.validate(scoringDataDTO,errors);
        List<ObjectError> list = errors.getAllErrors();
        FieldError fieldError = (FieldError) list.get(0);
        assertEquals(fieldError.getField(), "middleName");
        assertEquals(fieldError.getDefaultMessage(), "Отчество (при наличии) должно содержать от 2 до 30 латинских букв");
    }

    @Test
    void validateAgeBefore18() {
        scoringDataDTO.setBirthday(LocalDate.of(2015, 11, 1));
        scoringDataValidator.validate(scoringDataDTO,errors);
        List<ObjectError> list = errors.getAllErrors();
        FieldError fieldError = (FieldError) list.get(0);
        assertEquals(fieldError.getField(), "birthday");
        assertEquals(fieldError.getDefaultMessage(), "Возраст должен быть более 18 лет");
    }

    @Test
    void validateMiddleNameMore30SymbolsAndAgeBefore18() {
        scoringDataDTO.setMiddleName("Ivanovigrsgrtghdthdfththdthdthdffffffffffsthshshshthch");
        scoringDataDTO.setBirthday(LocalDate.of(2010,5,2));
        scoringDataValidator.validate(scoringDataDTO,errors);
        List<ObjectError> list = errors.getAllErrors();
        FieldError fieldError = (FieldError) list.get(0);
        assertEquals(fieldError.getField(), "middleName");
        assertEquals(fieldError.getDefaultMessage(), "Отчество (при наличии) должно содержать от 2 до 30 символов");
        fieldError = (FieldError) list.get(1);
        assertEquals(fieldError.getField(), "birthday");
        assertEquals(fieldError.getDefaultMessage(), "Возраст должен быть более 18 лет");
    }

    @Test
    void validatePassportIssueDateAfterToday() {
        scoringDataDTO.setPassportIssueDate(LocalDate.of(2024,10,2));
        scoringDataValidator.validate(scoringDataDTO, errors);
        List<ObjectError> list =  errors.getAllErrors();
        FieldError fieldError = (FieldError) list.get(0);
        assertEquals(fieldError.getField(),"passportIssueDate");
        assertEquals( fieldError.getDefaultMessage(),"Дата выдачи паспорта не должна быть более текущей даты");
    }

}