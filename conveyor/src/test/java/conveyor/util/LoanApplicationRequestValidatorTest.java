package conveyor.util;

import conveyor.dto.CreditDTO;
import conveyor.dto.LoanApplicationRequestDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoanApplicationRequestValidatorTest {

    @Autowired
    private LoanApplicationRequestValidator loanApplicationRequestValidator;

    private LoanApplicationRequestDTO loanApplicationRequestDTO;
    private Errors errors;

    @Test
    void supports() {
        assertTrue(loanApplicationRequestValidator.supports(LoanApplicationRequestDTO.class));
        assertFalse(loanApplicationRequestValidator.supports(CreditDTO.class));
    }

    @BeforeEach
    public void initLoanApplicationRequestDTO() {
        loanApplicationRequestDTO = CreateAndInitTestDTOs.createAndInitLoanApplicationRequestDTO();
        errors = new BeanPropertyBindingResult(loanApplicationRequestDTO, "loanApplicationRequestDTO");
    }

    @Test
    void validateNoErrors() {
        loanApplicationRequestValidator.validate(loanApplicationRequestDTO, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    void validateMiddleNameOneSymbol() {
        loanApplicationRequestDTO.setMiddleName("q");
        loanApplicationRequestValidator.validate(loanApplicationRequestDTO, errors);
        List<ObjectError> list = errors.getAllErrors();
        FieldError fieldError = (FieldError) list.get(0);
        assertEquals(fieldError.getField(), "middleName");
        assertEquals(fieldError.getDefaultMessage(), "Отчество (при наличии) должно содержать от 2 до 30 символов");
    }

    @Test
    void validateMiddleNameNotValidSymbols() {
        loanApplicationRequestDTO.setMiddleName("d3453454353s678vds");
        loanApplicationRequestValidator.validate(loanApplicationRequestDTO, errors);
        List<ObjectError> list = errors.getAllErrors();
        FieldError fieldError = (FieldError) list.get(0);
        assertEquals(fieldError.getField(), "middleName");
        assertEquals(fieldError.getDefaultMessage(), "Отчество (при наличии) должно содержать от 2 до 30 латинских букв");
    }

    @Test
    void validateAgeBefore18() {
        loanApplicationRequestDTO.setBirthday(LocalDate.of(2015, 11, 1));
        loanApplicationRequestValidator.validate(loanApplicationRequestDTO, errors);
        List<ObjectError> list = errors.getAllErrors();
        FieldError fieldError = (FieldError) list.get(0);
        assertEquals(fieldError.getField(), "birthday");
        assertEquals(fieldError.getDefaultMessage(), "Возраст должен быть более 18 лет");
    }

    @Test
    void validateMiddleNameMore30SymbolsAndAgeBefore18() {
        loanApplicationRequestDTO.setMiddleName("Ivanovigrsgrtghdthdfththdthdthdffffffffffsthshshshthch");
        loanApplicationRequestDTO.setBirthday(LocalDate.of(2010, 5, 2));
        loanApplicationRequestValidator.validate(loanApplicationRequestDTO, errors);
        List<ObjectError> list = errors.getAllErrors();
        FieldError fieldError = (FieldError) list.get(0);
        assertEquals(fieldError.getField(), "middleName");
        assertEquals(fieldError.getDefaultMessage(), "Отчество (при наличии) должно содержать от 2 до 30 символов");
        fieldError = (FieldError) list.get(1);
        assertEquals(fieldError.getField(), "birthday");
        assertEquals(fieldError.getDefaultMessage(), "Возраст должен быть более 18 лет");
    }

}