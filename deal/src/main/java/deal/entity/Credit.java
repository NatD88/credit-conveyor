package deal.entity;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import deal.dto.PaymentScheduleElement;
import deal.util.CreditStatus;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@ToString
@Table(name = "credits")
@NoArgsConstructor
@AllArgsConstructor
@TypeDefs({
@TypeDef(name = "CREDIT_STATUS_TYPE",
      typeClass = PostgreSQLEnumType.class),
@TypeDef(name = "JSON", typeClass = JsonType.class) })
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "CREDIT_ID")
    private Long creditID;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "TERM")
    private int term;

    @Column(name = "MONTHLY_PAYMENT")
    private BigDecimal monthlyPayment;

    @Column(name = "RATE")
    private BigDecimal rate;

    @Column(name = "PSK")
    private BigDecimal psk;

    @Column(name = "PAYMENT_SCHEDULE", columnDefinition = "JSONB")
    @Type(type = "JSON")
    private List<PaymentScheduleElement> paymentSchedule;

    @Column(name = "IS_INSURANCE_ENABLED")
    private boolean isInsuranceEnables;

    @Column(name = "IS_SALARY_CLIENT")
    private boolean isSalaryClient;

    @Column(name = "CREDIT_STATUS")
    @Enumerated(EnumType.STRING)
    @Type(type = "CREDIT_STATUS_TYPE")
    private CreditStatus creditStatus;
}
