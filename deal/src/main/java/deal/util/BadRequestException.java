package deal.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter

public class BadRequestException extends RuntimeException {
    private Object responseBody;
    private Long applicationID;

    public BadRequestException(Object responseBody) {
        this.responseBody = responseBody;
    }

    public BadRequestException(Object responseBody, Long applicationID) {
        this.responseBody = responseBody;
        this.applicationID = applicationID;
    }
}
