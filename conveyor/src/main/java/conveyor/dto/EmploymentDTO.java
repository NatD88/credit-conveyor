package conveyor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmploymentDTO {

    @NotNull
    private EmploymentStatus employmentStatus;

    @NotNull
    @Pattern(regexp = "^[0-9]+$", message = "ИНН состоит только из цифр")
    @Size(max = 12, message = "Количество цифр ИНН не может быть болье 12")
    private String employerINN;

    @NotNull
    private BigDecimal salary;

    @NotNull
    private EmploymentPosition position;

    @NotNull
    @Min(value = 0, message = "Опыт работы не может быть отрицательным")
    private Integer workExperienceTotal;

    @NotNull
    @Min(value = 0, message = "Опыт работы не может быть отрицательным")
    private Integer workExperienceCurrent;

}
