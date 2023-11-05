package com.guerrilla.scorecontroller.publisher.sns;

import com.guerrilla.scorecontroller.dto.ScoreDto;
import com.guerrilla.scorecontroller.publisher.ScoreDTOPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class ScoreDTOSNSPublisher implements ScoreDTOPublisher {
    @Autowired
    private NotificationMessagingTemplate notificationMessagingTemplate;

    @Scheduled(fixedRate = 3000)// Every 3 seconds
    public void publish() {
        ScoreDto randomScoreDto = createRandomScoreDto();
        notificationMessagingTemplate.convertAndSend("scores", randomScoreDto);
        log.info("Published ScoreDto for Player: " + randomScoreDto.playerId() + " with Score: " + randomScoreDto.value());
    }

    private ScoreDto createRandomScoreDto() {
        Random rand = new Random();
        return ScoreDto.builder()
                .playerId("e77928da-0006-45af-8eeb-1bbc861ad326")
                .value(rand.nextInt())
                .build();
    }
}
