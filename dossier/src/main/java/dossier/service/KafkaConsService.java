package dossier.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dossier.dto.ApplicationStatus;
import dossier.dto.ClientApplication;
import dossier.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsService {
    private final ObjectMapper objectMapper;
    private final DefaultEmailService defaultEmailService;
    private final FeignServiceDeal feignServiceDeal;
    private final FilesCreatingService filesCreatingService;


    @Value("${finishReg.url}")
    String linkFinishReg;
    @Value("${sendDoc.url}")
    String linkSendDoc;
    @Value("${signDocReq.url}")
    String linkSignDocReq;
    @Value("${approveDoc.url}")
    String linkApproveDoc;


    @KafkaListener(id = "consGroup1", topics = {"finish-registration"}, containerFactory = "singleFactory")
    public void consume(EmailMessage emailMessage) throws JsonProcessingException {
        log.info("=> consumed {}", objectMapper.writeValueAsString(emailMessage));
        defaultEmailService.sendSimpleEmail(emailMessage.getAddress(), emailMessage.getThemeEmail().getTitle(),
                String.format("Hi! Your loan application №%d approved! Finish registration " +
                        "by the following link " + linkFinishReg, emailMessage.getApplicationID()));
    }

    @KafkaListener(id = "consGroup2", topics = {"create-documents"}, containerFactory = "singleFactory")
    public void consumeCreateDoc(EmailMessage emailMessage) throws JsonProcessingException {
        log.info("=> consumed {}", objectMapper.writeValueAsString(emailMessage));
        defaultEmailService.sendSimpleEmail(emailMessage.getAddress(), emailMessage.getThemeEmail().getTitle(),
                String.format("Hi! Your loan application №%d passed all checks! Send creating documents request " +
                        "by the following link  " + linkSendDoc, emailMessage.getApplicationID()));
    }

    @KafkaListener(id = "consGroup3", topics = {"send-documents"}, containerFactory = "singleFactory")
    public void consumeSendDoc(EmailMessage emailMessage) throws JsonProcessingException, MessagingException, FileNotFoundException {
        log.info("=> consumed {}", objectMapper.writeValueAsString(emailMessage));
        ClientApplication clientApplication = feignServiceDeal.getClientApplication(emailMessage.getApplicationID());
        log.info("clientApplication received from ms deal. clientApplication: {}", clientApplication);
        List<String> listFileNames = filesCreatingService.createFiles(clientApplication);
        feignServiceDeal.updateClientApplicationStatus(emailMessage.getApplicationID(), ApplicationStatus.DOCUMENT_CREATED);
        defaultEmailService.sendEmailWithAttachment(emailMessage.getAddress(), emailMessage.getThemeEmail().getTitle(),
                String.format(" Now you should send signing document request(application №%d) by the following link " + linkSignDocReq, emailMessage.getApplicationID()),
                listFileNames.get(0), listFileNames.get(1), listFileNames.get(2));
    }

    @KafkaListener(id = "consGroup4", topics = {"send-ses"}, containerFactory = "singleFactory")
    public void consumeSendSes(EmailMessage emailMessage) throws JsonProcessingException {
        log.info("=> consumed {}", objectMapper.writeValueAsString(emailMessage));
        ClientApplication clientApplication = feignServiceDeal.getClientApplication(emailMessage.getApplicationID());
        log.info("clientApplication received from ms deal. clientApplication: {}", clientApplication);
        defaultEmailService.sendSimpleEmail(emailMessage.getAddress(), emailMessage.getThemeEmail().getTitle(),
                String.format("Hello! To sign your application №%d, use code: %d. Enter code by the following link " + linkApproveDoc,
                        emailMessage.getApplicationID(), clientApplication.getSes_code()));
    }

    @KafkaListener(id = "consGroup5", topics = {"credit-issued"}, containerFactory = "singleFactory")
    public void consumeCreditIssued(EmailMessage emailMessage) throws JsonProcessingException {
        log.info("=> consumed {}", objectMapper.writeValueAsString(emailMessage));
        defaultEmailService.sendSimpleEmail(emailMessage.getAddress(), emailMessage.getThemeEmail().getTitle(),
                String.format("Hello! Congratulations!  Your credit is issued! Application №%d. Money will transfer you soon.", emailMessage.getApplicationID()));
    }

    @KafkaListener(id = "consGroup6", topics = {"application-denied"}, containerFactory = "singleFactory")
    public void consumeAppDenied(EmailMessage emailMessage) throws JsonProcessingException {
        log.info("=> consumed {}", objectMapper.writeValueAsString(emailMessage));
        defaultEmailService.sendSimpleEmail(emailMessage.getAddress(), emailMessage.getThemeEmail().getTitle(),
                String.format("Hello! Sorry, your credit denied! Application №%d", emailMessage.getApplicationID()));
    }
}
