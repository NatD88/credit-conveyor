package conveyor.util;

import conveyor.dto.LoanApplicationRequestDTO;
import conveyor.dto.ScoringDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

@Component
@Slf4j
public class LoanApplicationRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return LoanApplicationRequestDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.info("validation of LoanApplicationRequestDTO started");
        LoanApplicationRequestDTO loanApplicationRequestDTO =
                (LoanApplicationRequestDTO) target;

        if ((loanApplicationRequestDTO.getMiddleName() != null) && (!Objects.equals(loanApplicationRequestDTO.getMiddleName(), "") &&
                ((loanApplicationRequestDTO.getMiddleName().length() < 2) ||
                        (loanApplicationRequestDTO.getMiddleName().length() > 30)))) {
            errors.rejectValue("middleName", "error.middleName.length", "Отчество (при наличии) должно содержать от 2 до 30 символов");
        }

        if ((loanApplicationRequestDTO.getMiddleName() != null) && (!Objects.equals(loanApplicationRequestDTO.getMiddleName(), "") &&
                !loanApplicationRequestDTO.getMiddleName().matches("^[a-zA-Z]+$"))) {

            errors.rejectValue("middleName", "error.middleName.symbolTypes", "Отчество (при наличии) должно содержать от 2 до 30 латинских букв");
        }

        Period period = Period.between(loanApplicationRequestDTO.getBirthday(), LocalDate.now());

        if (period.getYears() < 18) {
            errors.rejectValue("birthday", "error.birthday", "Возраст должен быть более 18 лет");
        }
        log.info("validation of LoanApplicationRequestDTO completed. errors: {}", errors.getAllErrors());
    }
}
