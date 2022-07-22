package dossier.service;

import dossier.dto.ApplicationStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DefaultEmailServiceTest {

    @MockBean
    JavaMailSender emailSender;

    @Test
    void sendSimpleEmail() {
        DefaultEmailService defaultEmailService = new DefaultEmailService(emailSender);
        Assertions.assertDoesNotThrow(() -> defaultEmailService.sendSimpleEmail("sdf@fd", "Greeting", "Hi!!"));
    }

    @Test
    void sendEmailWithAttachment() {
        DefaultEmailService defaultEmailService = new DefaultEmailService(emailSender);
        Assertions.assertThrows(Exception.class, () -> defaultEmailService.sendEmailWithAttachment("sdf@fd", "Greeting", "Hi!!", "doesntExistFile", "doesntExistFile", "doesntExistFile"));

        JavaMailSender emailSenderNotMock = new JavaMailSenderImpl();
        MimeMessage mimeMessage = emailSenderNotMock.createMimeMessage();
        Mockito.when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        Assertions.assertDoesNotThrow(() -> defaultEmailService.sendEmailWithAttachment("sdf@fd", "Greeting", "Hi!!", "clientApp.txt",  "clientAppl.txt", "creditContract.txt"));
    }
}