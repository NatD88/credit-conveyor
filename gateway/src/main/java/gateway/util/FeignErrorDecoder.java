package gateway.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {

        log.info("in decode");

        switch (response.status()) {
            case 400: {
                log.info("in decode - switch - case 400");
                Map<Object, Object> errorMessages;
                try (InputStream bodyIs = response.body()
                        .asInputStream()) {
                    ObjectMapper mapper = new ObjectMapper();
                    errorMessages = mapper.readValue(bodyIs, new TypeReference<Map<Object, Object>>() {
                    });
                    log.info("errorMessages: {}", errorMessages);
                } catch (IOException e) {
                    log.info("in catch");
                    return new Exception(e.getMessage());
                }
                return new BadRequestException(errorMessages);
            }
            case 404: {
                log.info("in decode - switch - case 404");
                String errorMessage;
                try (InputStream bodyIs = response.body().asInputStream()) {
                    byte[] bytes = bodyIs.readAllBytes();
                    errorMessage = new String(bytes, StandardCharsets.UTF_8);
                    log.info("errorMessage: {}", errorMessage);
                } catch (IOException e) {
                    log.info("in catch");
                    return new Exception(e.getMessage());
                }
                return new ApplicationNotFoundException(errorMessage);
            }
            default:
                return new Exception("Generic error, ms-conveyor returned status not 200");
        }
    }
}
