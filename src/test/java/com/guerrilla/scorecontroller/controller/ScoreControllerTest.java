package com.guerrilla.scorecontroller.controller;

import com.guerrilla.scorecontroller.exception.ScoreNotFoundException;
import com.guerrilla.scorecontroller.model.Score;
import com.guerrilla.scorecontroller.service.ScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ScoreControllerTest {
    @Mock
    private ScoreService scoreService;

    @InjectMocks
    private ScoreController scoreController;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateScore() {
        Score score = Score.builder()
                .scoreId(UUID.randomUUID())
                .playerId(UUID.randomUUID())
                .value(69)
                .build();

        when(scoreService.createScore(score.getPlayerId(), score.getValue())).thenReturn(score);

        ResponseEntity<Score> responseEntity = scoreController.createScore(score.getPlayerId().toString(), score.getValue());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(score, responseEntity.getBody());
    }

    @Test
    public void testGetScoreById() {
        Score score = Score.builder()
                .scoreId(UUID.randomUUID())
                .playerId(UUID.randomUUID())
                .value(69).build();

        when(scoreService.getScore(score.getScoreId())).thenReturn(score);

        ResponseEntity<Score> responseEntity = scoreController.getScoreById(score.getScoreId().toString());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(score, responseEntity.getBody());
    }

    @Test
    public void testGetScoresByPlayer() {
        UUID playerId = UUID.randomUUID();

        List<Score> scores = List.of(
                Score.builder()
                        .scoreId(UUID.randomUUID())
                        .playerId(playerId)
                        .value(69)
                        .build(),
                Score.builder()
                        .scoreId(UUID.randomUUID())
                        .playerId(playerId)
                        .value(420)
                        .build()
        );

        when(scoreService.getScoresByPlayer(playerId)).thenReturn(scores);

        ResponseEntity<List<Score>> responseEntity = scoreController.getScoresByPlayer(playerId.toString());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(scores, responseEntity.getBody());
    }

    @Test
    public void testUpdateScore() {
        Integer newValue = 420;
        Score score = Score.builder()
                .scoreId(UUID.randomUUID())
                .playerId(UUID.randomUUID())
                .value(69)
                .build();

        when(scoreService.getScore(score.getScoreId())).thenReturn(score);

        score.setValue(newValue);

        when(scoreService.updateScore(score.getScoreId(), newValue)).thenReturn(score);

        ResponseEntity<Score> responseEntity = scoreController.updateScore(score.getScoreId().toString(), newValue);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(score, responseEntity.getBody());
    }

    @Test
    public void testDeleteScore() {
        Score score = Score.builder()
                .scoreId(UUID.randomUUID())
                .playerId(UUID.randomUUID())
                .value(69).build();

        when(scoreService.getScore(score.getScoreId())).thenReturn(score);

        scoreController.deleteScore(score.getScoreId().toString());

        verify(scoreService, times(1)).deleteScore(score.getScoreId());
    }

    @Test
    public void testHandleScoreNotFoundException() {
        ScoreNotFoundException exception = new ScoreNotFoundException(UUID.randomUUID().toString());

        ResponseEntity<String> responseEntity = scoreController.handleScoreNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}