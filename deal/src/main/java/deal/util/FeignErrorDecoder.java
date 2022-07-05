package deal.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
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
            default:
                return new Exception("Generic error, ms-conveyor returned status not 200");
        }
    }
}


