package deal.entity;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import conveyor.dto.EmploymentPosition;
import conveyor.dto.EmploymentStatus;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@Entity
@ToString
@Table(name = "employments")
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "EMPLOYMENT_POSITION_TYPE",
        typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "EMPLOYMENT_STATUS_TYPE",
        typeClass = PostgreSQLEnumType.class)
public class Employment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "EMPLOYMENT_ID")
    private Long employmentID;

    @Enumerated(value = EnumType.STRING)
    @Type(type = "EMPLOYMENT_STATUS_TYPE" )
    @Column(name = "EMPLOYMENT_STATUS")
    private EmploymentStatus employmentStatus;

    @Column(name = "EMPLOYER")
    private String employer;

    @Column(name = "SALARY")
    private BigDecimal salary;

    @Column(name = "EMPLOYMENT_POSITION")
    @Enumerated(value = EnumType.STRING)
    @Type(type = "EMPLOYMENT_POSITION_TYPE")
    private EmploymentPosition employmentPosition;

    @Column(name = "WORK_EXPERIENCE_TOTAL")
    private int workExperienceTotal;

    @Column(name = "WORK_EXPERIENCE_CURRENT")
    private int workExperienceCurrent;
}
