package com.guerrilla.scorecontroller.listener.sqs;

import com.guerrilla.scorecontroller.dto.ScoreDto;
import com.guerrilla.scorecontroller.listener.ScoreListener;
import com.guerrilla.scorecontroller.model.Score;
import com.guerrilla.scorecontroller.service.PlayerService;
import com.guerrilla.scorecontroller.service.ScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationMessage;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class ScoreSQSListener implements ScoreListener {

    private final ScoreService scoreService;

    @Autowired
    public ScoreSQSListener(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @Override
    @SqsListener(value = "https://sqs.eu-west-2.amazonaws.com/765122112100/scores", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void consume(@NotificationMessage ScoreDto scoreDto) {
        Score score = scoreService.createScore(UUID.fromString(scoreDto.playerId()), scoreDto.value());
        log.info("Score Created through Listener: " + score.getScoreId());
    }
}
