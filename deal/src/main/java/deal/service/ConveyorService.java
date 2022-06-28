package deal.service;
import conveyor.dto.CreditDTO;
import conveyor.dto.LoanApplicationRequestDTO;
import conveyor.dto.LoanOfferDTO;
import conveyor.dto.ScoringDataDTO;
import deal.controller.ErrorHandler;
import deal.util.BadRequestException;
import deal.util.RejectScoringDealException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ConveyorService {
    public final RestTemplate restTemplate;

    public ResponseEntity<List<LoanOfferDTO>> getLoanOfferDTOList(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("Service for RestTemplate started. loanApplicationRequestDTO entered");

        if (loanApplicationRequestDTO == null) {
            throw new RuntimeException("loanApplicationRequestDTO is null!");
        }

        restTemplate.setErrorHandler(new ErrorHandler());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity = new HttpEntity<Object>(loanApplicationRequestDTO, headers);
        ResponseEntity<List<LoanOfferDTO>> responseEntity;
        ResponseEntity<Object> objectResponseEntity;

        try {
            log.info("requestEntity with loanApplicationRequestDTO will be send");
            responseEntity = restTemplate.exchange(
                    "http://localhost:8080/conveyor/offers",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<List<LoanOfferDTO>>() {
                    });

            log.info("ResponseEntity<List<LoanOfferDTO>> successfully received");
        } catch (Exception exception) {
            log.warn("error during restTemplate with ParameterizedTypeReference. Error caught ");
            try {
                log.info("requestEntity with response like Object will be send");
                objectResponseEntity = restTemplate.postForEntity(
                        "http://localhost:8080/conveyor/offers",
                        loanApplicationRequestDTO,
                        Object.class);
                log.info("objectResponseEntity successfully received ");
            } catch (Exception ex) {
                log.warn("error during restTemplate with type Object ");
                throw new RuntimeException("error response from ms conveyor - conveyor/offers");
            }
            log.warn("objectResponseEntity will be hand over to BadRequestException");
            throw new BadRequestException(objectResponseEntity.getBody());
        }
        return responseEntity;
    }

    public ResponseEntity<CreditDTO> getCreditDTO(ScoringDataDTO scoringDataDTO, Long applicationID) {
        log.info("Service for RestTemplate started. ScoringDataDTO entered");
        if (scoringDataDTO == null) {
            throw new RuntimeException("scoringDataDTO is null");
        }

        restTemplate.setErrorHandler(new ErrorHandler());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity = new HttpEntity<Object>(scoringDataDTO, headers);

        ResponseEntity<String> responseEntityString;
        ResponseEntity<Object> objectResponseEntity = null;

        try {
            log.info("requestEntity with scoringDataDTO will be send");
            ResponseEntity<CreditDTO> responseEntity = restTemplate.exchange(
                    "http://localhost:8080/conveyor/calculation",
                    HttpMethod.POST,
                    requestEntity,
                    CreditDTO.class);
            log.info("ResponseEntity<CreditDTO> successfully received");
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return new ResponseEntity<CreditDTO>(responseEntity.getBody(), HttpStatus.OK);
            } else {
                log.info("requestEntity with response like Object will be send");
                objectResponseEntity = restTemplate.postForEntity(
                        "http://localhost:8080/conveyor/calculation",
                        scoringDataDTO,
                        Object.class);
                log.info("objectResponseEntity with validation errors successfully received");
                throw new BadRequestException(objectResponseEntity.getBody(), applicationID);
            }

        } catch (Exception exception) {
            log.warn("error during restTemplate in getCreditDTO. Error caught ");
            if (exception instanceof BadRequestException) {
                throw new BadRequestException(objectResponseEntity.getBody(), applicationID);
            }
            log.info("requestEntity with response like String will be send");
            responseEntityString = restTemplate.exchange(
                    "http://localhost:8080/conveyor/calculation",
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
            log.info("requestEntity with response like String successfully received. RejectScoringDealException will be thrown");
            throw new RejectScoringDealException(responseEntityString.getBody(), applicationID);
        }
    }
}
