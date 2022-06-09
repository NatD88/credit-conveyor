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
public class ScoringDataValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ScoringDataDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.info("validation of ScoringDataDTO started");
        ScoringDataDTO scoringDataDTO =
                (ScoringDataDTO) target;

        if (scoringDataDTO.getMiddleName() != null && (!Objects.equals(scoringDataDTO.getMiddleName(), "") &&
                ((scoringDataDTO.getMiddleName().length() < 2) ||
                        (scoringDataDTO.getMiddleName().length() > 30)))) {
            errors.rejectValue("middleName", "error.middleName", "Отчество (при наличии) должно содержать от 2 до 30 символов");
        }
        if ((scoringDataDTO.getMiddleName() != null) && (!Objects.equals(scoringDataDTO.getMiddleName(), "") &&
                !scoringDataDTO.getMiddleName().matches("^[a-zA-Z]+$"))) {
            errors.rejectValue("middleName", "error.middleName.symbolTypes", "Отчество (при наличии) должно содержать от 2 до 30 латинских букв");
        }

        Period period = Period.between(scoringDataDTO.getBirthday(), LocalDate.now());

        if (period.getYears() < 18) {
            errors.rejectValue("birthday", "error.birthday", "Возраст должен быть более 18 лет");
        }

        if (scoringDataDTO.getPassportIssueDate().isAfter(LocalDate.now())) {
            errors.rejectValue("passportIssueDate", "error.passportIssueDate", "Дата выдачи паспорта не должна быть более текущей даты");
        }
        log.info("validation of ScoringDataDTO completed. errors: {}", errors.getAllErrors());
    }
}

