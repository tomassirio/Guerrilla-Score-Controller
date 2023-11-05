package com.guerrilla.scorecontroller.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guerrilla.scorecontroller.listener.dto.ScoreDto;
import com.guerrilla.scorecontroller.service.ScoreService;
import io.awspring.cloud.messaging.config.annotation.NotificationMessage;
import io.awspring.cloud.messaging.config.annotation.NotificationSubject;
import io.awspring.cloud.messaging.endpoint.NotificationStatus;
import io.awspring.cloud.messaging.endpoint.annotation.NotificationMessageMapping;
import io.awspring.cloud.messaging.endpoint.annotation.NotificationSubscriptionMapping;
import io.awspring.cloud.messaging.endpoint.annotation.NotificationUnsubscribeConfirmationMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@Slf4j
//@RequestMapping("${aws.sns.topic.name}")
public class ScoresSnsHandler {
    private final ObjectMapper objectMapper;
    private final ScoreService scoreService;

    @Autowired
    public ScoresSnsHandler(ObjectMapper objectMapper, ScoreService scoreService) {
        this.objectMapper = objectMapper;
        this.scoreService = scoreService;
    }

    @NotificationSubscriptionMapping
    public void confirmUnsubscribeMessage(
            NotificationStatus notificationStatus) {
        notificationStatus.confirmSubscription();
    }

    @NotificationMessageMapping
    public void receiveNotification(@NotificationMessage String message,
                                    @NotificationSubject String subject) {
        try {
            ScoreDto scoreDto = objectMapper.readValue(message, ScoreDto.class);
            scoreService.createScore(UUID.fromString(scoreDto.playerId), scoreDto.value);
        } catch (IllegalArgumentException | JsonProcessingException e) {
            log.error(e.toString());
        }
    }

    @NotificationUnsubscribeConfirmationMapping
    public void confirmSubscriptionMessage(
            NotificationStatus notificationStatus) {
        notificationStatus.confirmSubscription();
    }
}
