package deal.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectScoringDealException extends RuntimeException {
    private Long applicationID;
    private String rejectMessage;

    public RejectScoringDealException(String rejectMessage, Long applicationID) {
        this.rejectMessage = rejectMessage;
        this.applicationID = applicationID;
    }
}
