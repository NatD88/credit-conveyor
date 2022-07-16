package deal.entity;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;

import deal.dto.Gender;
import deal.dto.MaritalStatus;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@Entity
@ToString
@Table(name = "clients")
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(
        name = "GENDER_TYPE",
        typeClass = PostgreSQLEnumType.class
)
@TypeDef(
        name = "MARITAL_STATUS",
        typeClass = PostgreSQLEnumType.class
)
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "CLIENT_ID")
    private Long clientID;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "MIDDLE_NAME")
    private String middleName;

    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    @Column(name = "EMAIL")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "GENDER")
    @Type(type = "GENDER_TYPE")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "MARITAL_STATUS")
    @Type(type = "MARITAL_STATUS")
    private MaritalStatus maritalStatus;

    @Column(name = "DEPENDENT_AMOUNT")
    private int dependentAmount;

    @OneToOne
    @JoinColumn(name = "PASSPORT_ID")
    private Passport passport;

    @ManyToOne
    @JoinColumn(name = "EMPLOYMENT_ID")
    private Employment employment;

    @Column(name = "ACCOUNT")
    private String account;
}
