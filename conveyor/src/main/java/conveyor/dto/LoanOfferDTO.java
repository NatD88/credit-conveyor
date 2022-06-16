package conveyor.dto;

import conveyor.service.CreationLoanOffersService;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LoanOfferDTO {

    private Long applicationId;
    private BigDecimal requestedAmount;
    private BigDecimal totalAmount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;

    @Override
    public String toString() {
        return "LoanOfferDTO{" +
                "applicationId=" + applicationId +
                ", requestedAmount=" + requestedAmount +
                ", totalAmount=" + totalAmount +
                ", term=" + term +
                ", monthlyPayment=" + monthlyPayment +
                ", rate=" + rate +
                ", isInsuranceEnabled=" + isInsuranceEnabled +
                ", isSalaryClient=" + isSalaryClient +
                '}';
    }
}
