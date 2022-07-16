package deal.entity;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import deal.dto.LoanOfferDTO;
import deal.util.ApplicationStatus;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@ToString
@Table(name = "applications")
@NoArgsConstructor
@AllArgsConstructor
@TypeDefs({
        @TypeDef(name = "APPLICATION_STATUS_TYPE",
                typeClass = PostgreSQLEnumType.class),
        @TypeDef(name = "JSON", typeClass = JsonType.class)
})
public class ClientApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "APPLICATION_ID")
    private Long applicationID;

    @ManyToOne
    @JoinColumn(name = "CLIENT_ID")
    private Client client;

    @OneToOne
    @JoinColumn(name = "CREDIT_ID")
    private Credit credit;

    @Enumerated(EnumType.STRING)
    @Type(type = "APPLICATION_STATUS_TYPE")
    private ApplicationStatus applicationStatus;

    @Column(name = "CREATION_DATE")
    private LocalDate creationDate;

    @Type(type = "JSON")
    @Column(name = "APPLIED_OFFER", columnDefinition = "JSONB")
    private LoanOfferDTO appliedOffer;

    @Column(name = "SIGN_DATE")
    private LocalDate signDate;

    @Column(name = "SES_CODE")
    private int ses_code;

    @Type(type = "JSON")
    @Column(name = "STATUS_HISTORY", columnDefinition = "JSONB")
    private List<ApplicationStatusHistory> statusHistoryList;
}
