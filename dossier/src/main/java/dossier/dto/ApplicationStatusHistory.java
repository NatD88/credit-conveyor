package dossier.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApplicationStatusHistory {
    private ApplicationStatus status;
    private LocalDateTime time;
    private ChangeType changeType;
}
