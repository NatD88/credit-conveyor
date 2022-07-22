package gateway.util;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class BadRequestException extends RuntimeException {
    private Map<Object, Object> responseBody;
    private Long applicationID;

    public BadRequestException(Map<Object, Object> responseBody) {
        this.responseBody = responseBody;
    }

    public BadRequestException(Map<Object, Object> responseBody, Long applicationID) {
        this.responseBody = responseBody;
        this.applicationID = applicationID;
    }
}
