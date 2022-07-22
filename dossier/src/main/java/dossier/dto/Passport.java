package dossier.dto;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Passport {
    private Long PassportID;
    private String series;
    private String number;
    private LocalDate issueDate;
    private String issueBranch;
}
