package deal.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@Entity
@ToString
@Table(name = "passports")
@NoArgsConstructor
@AllArgsConstructor
public class Passport {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "PASSPORT_ID")
    private Long PassportID;

    @Column(name = "SERIES")
    private String series;

    @Column(name = "NUMBER")
    private String number;

    @Column(name = "ISSUE_DATE")
    private LocalDate issueDate;

    @Column(name = "ISSUE_BRANCH")
    private String issueBranch;
}
