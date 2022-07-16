package deal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ScoringDataDTO {

    @NotNull
    @Min(value = 10000, message = "Сумма кредита должна быть больше 10000")
    private BigDecimal amount;

    @NotNull
    @Min(value = 6, message = "Срок кредита дожен быть не меньше 6")
    @Max(value = 90, message = "Срок кредита должен быть не больше 90")
    private Integer term;

    @NotNull
    @Size(min = 2, max = 30, message = "Имя должно содержать от 2 до 30 символов")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Имя должно содержать только латинские буквы")
    private String firstName;

    @NotNull
    @Size (min = 2, max = 30, message = "Фамилия должна содержать от 2 до 30 символов")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Фамилия должна содержать только латинские буквы")
    private String lastName;

    private String middleName;

    @NotNull
    private Gender gender;

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

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd")

    @NotNull
    private LocalDate passportIssueDate;

    @NotNull
    @Size (min = 5, max = 50, message = "Орган выдавший паспорт должен содержать от 5 до 30 символов")
    private String passportIssueBranch;

    @NotNull
    private MaritalStatus maritalStatus;

    @NotNull
    @Min(value = 0, message = "Количество иждивенцев не должно быть меньше 0")

    private Integer dependentAmount;

    @Valid
    private EmploymentDTO employment;

    @Size (min = 20, max = 20, message = "Номер счета должен состоять из 20 цифр")
    @Pattern(regexp = "^[0-9]+$", message = "Номер счета состоит только из цифр")
    private String account;

    @NotNull
    private Boolean isInsuranceEnabled;
    @NotNull
    private Boolean isSalaryClient;

    @Override
    public String toString() {
        return "ScoringDataDTO{" +
                "amount=" + amount +
                ", term=" + term +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", gender=" + gender +
                ", birthday=" + birthday +
                ", passportSeries='" + passportSeries + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                ", passportIssueDate=" + passportIssueDate +
                ", passportIssueBranch='" + passportIssueBranch + '\'' +
                ", maritalStatus=" + maritalStatus +
                ", dependentAmount=" + dependentAmount +
                ", employment=" + employment +
                ", account='" + account + '\'' +
                ", isInsuranceEnabled=" + isInsuranceEnabled +
                ", isSalaryClient=" + isSalaryClient +
                '}';
    }
}
