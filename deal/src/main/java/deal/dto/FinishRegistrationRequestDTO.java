package deal.dto;


import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FinishRegistrationRequestDTO {
    private Gender gender;
    private MaritalStatus maritalStatus;
    private int departmentAmount;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private EmploymentDTO employmentDTO;
    private String account;
}
