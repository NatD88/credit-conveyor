package deal.util;

public class RejectScoringDealException extends RuntimeException {
    public Long applicationID;

    public RejectScoringDealException(String message, Long applicationID) {
        super(message);
        this.applicationID = applicationID;
    }
}
