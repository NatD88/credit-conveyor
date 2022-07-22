package deal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import deal.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaSendService {
    private final KafkaTemplate<Long, EmailMessage> kafkaTemplate;

    public void send(EmailMessage dto) {
        log.info("send method started in KafkaSendService.ThemeEmail: {}", dto.getThemeEmail());
        kafkaTemplate.send(dto.getThemeEmail().getTitle(), dto);
        log.info("EmailMessage successfully send to kafka");
    }

}
