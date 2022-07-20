package dossier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.FileNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultEmailService {

    public final JavaMailSender emailSender;

    public void sendSimpleEmail(String toAddress, String subject, String message) {
        log.info("method sendSimpleEmail started in JavaMailSender. email subject: {}", subject);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        emailSender.send(simpleMailMessage);
        log.info("email successfully send");
    }

    public void sendEmailWithAttachment(String toAddress, String subject, String message, String creditApplication, String creditContract, String payments)
            throws FileNotFoundException, MessagingException {

        log.info("method sendEmailWithAttachment started in JavaMailSender. email subject: {}", subject);
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setTo(toAddress);
        messageHelper.setSubject(subject);
        messageHelper.setText(message);
        FileSystemResource fileApplication = new FileSystemResource(ResourceUtils.getFile(creditApplication));
        messageHelper.addAttachment("Credit application.txt", fileApplication);
        FileSystemResource fileCredit = new FileSystemResource(ResourceUtils.getFile(creditContract));
        messageHelper.addAttachment("Credit contract.txt", fileCredit);
        FileSystemResource filePayments = new FileSystemResource(ResourceUtils.getFile(payments));
        messageHelper.addAttachment("Payments.txt", filePayments);
        log.info("files attached");
        emailSender.send(mimeMessage);
        log.info("email successfully send");
    }
}
