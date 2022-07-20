package dossier.dto;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private Long clientID;
    private String lastName;
    private String firstName;
    private String middleName;
    private LocalDate birthDate;
    private String email;
    private Gender gender;
    private MaritalStatus maritalStatus;
    private int dependentAmount;
    private Passport passport;
    private Employment employment;
    private String account;
}
