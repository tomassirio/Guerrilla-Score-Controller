package com.guerrilla.scorecontroller.listener.sqs;

import com.guerrilla.scorecontroller.dto.ScoreDto;
import com.guerrilla.scorecontroller.listener.ScoreListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationMessage;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScoreSQSListener implements ScoreListener {
    @Override
    @SqsListener(value = "https://sqs.eu-west-2.amazonaws.com/765122112100/scores", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void consume(@NotificationMessage ScoreDto scoreDto) {
        log.info("Received score in listener from Player: " + scoreDto.playerId());

    }
}
