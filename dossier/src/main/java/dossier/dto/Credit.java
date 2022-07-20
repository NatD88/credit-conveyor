package dossier.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Credit {
    private Long creditID;
    private BigDecimal amount;
    private int term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    private List<PaymentScheduleElement> paymentSchedule;
    private boolean isInsuranceEnables;
    private boolean isSalaryClient;
    private CreditStatus creditStatus;
}
