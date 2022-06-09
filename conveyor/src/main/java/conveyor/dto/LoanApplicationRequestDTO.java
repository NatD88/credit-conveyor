package conveyor.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoanApplicationRequestDTO {

    @NotNull
    @Min(value = 10000, message = "Сумма кредита должна быть больше 10000")
    private BigDecimal amount;

    @NotNull
    @Min(value = 6, message = "Срок кредита дожен быть не меньше 6")
    @Max(value = 90, message = "Срок кредита должен быть не больше 90")

    private Integer term;

    @NotNull
    @Size (min = 2, max = 30, message = "Имя должно содержать от 2 до 30 символов")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Имя должно содержать только латинские буквы")
    private String firstName;

    @NotNull
    @Size (min = 2, max = 30, message = "Фамилия должна содержать от 2 до 30 символов")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Фамилия должна содержать только латинские буквы")
    private String lastName;

    private String middleName;

    @NotNull
    @Pattern(regexp = "^(.+)@(\\S+)$", message = "Некорректный формат email")
    private String email;

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthday;

    @NotNull
    @Size (min = 4, max = 4, message = "В серии паспорта должно быть 4 цифры")
    @Pattern(regexp = "^[0-9]+$", message = "Серия паспорта состоит только из цифр")
    private String passportSeries;

    @NotNull
    @Size (min = 6, max = 6, message = "Номер паспорта состоит из 6 цифр")
    @Pattern(regexp = "^[0-9]+$", message = "Номер паспорта состоит только из цифр")
    private String passportNumber;
}
