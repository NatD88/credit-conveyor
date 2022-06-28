package deal.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;

@SpringBootTest
class ErrorHandlerTest {

    private static ErrorHandler errorHandler;
    @Mock
    private ClientHttpResponse response;

    @Mock
    private InputStream inputStream;

    @BeforeAll
    static void initErrorHandler() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleError() {
          Assertions.assertThrows(RuntimeException.class, () -> errorHandler.handleError(response));
    }

    @Test
    void hasError() throws IOException {
        ClientHttpResponse httpResponse = new MockClientHttpResponse(inputStream, HttpStatus.OK);
        Assertions.assertFalse(errorHandler.hasError(httpResponse));
        httpResponse = new MockClientHttpResponse(inputStream, HttpStatus.BAD_REQUEST);
        Assertions.assertFalse(errorHandler.hasError(httpResponse));
        httpResponse = new MockClientHttpResponse(inputStream, HttpStatus.CONFLICT);
        Assertions.assertTrue(errorHandler.hasError(httpResponse));
    }
}