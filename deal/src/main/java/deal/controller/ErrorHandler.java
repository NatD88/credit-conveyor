package deal.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Slf4j
@Component
public class ErrorHandler  implements ResponseErrorHandler {
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        log.info("ErrorHandler catch error in method \"handleError\" ");
        throw  new RuntimeException("error response from ms conveyor");
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return  response.getStatusCode() != HttpStatus.OK &&
                response.getStatusCode() != HttpStatus.BAD_REQUEST;
    }
}
