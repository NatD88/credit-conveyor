package gateway.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Employment {
    private Long employmentID;
    private EmploymentStatus employmentStatus;
    private String employer;
    private BigDecimal salary;
    private EmploymentPosition employmentPosition;
    private int workExperienceTotal;
    private int workExperienceCurrent;
}
