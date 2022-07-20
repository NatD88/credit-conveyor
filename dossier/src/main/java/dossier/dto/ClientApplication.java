package dossier.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class ClientApplication {
    private Long applicationID;
    private Client client;
    private Credit credit;
    private ApplicationStatus applicationStatus;
    private LocalDate creationDate;
    private LoanOfferDTO appliedOffer;
    private LocalDate signDate;
    private int ses_code;
    private List<ApplicationStatusHistory> statusHistoryList;
}
