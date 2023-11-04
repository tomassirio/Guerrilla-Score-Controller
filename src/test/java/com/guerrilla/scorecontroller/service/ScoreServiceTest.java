package com.guerrilla.scorecontroller.service;

import com.guerrilla.scorecontroller.exception.ScoreNotFoundException;
import com.guerrilla.scorecontroller.model.Score;
import com.guerrilla.scorecontroller.repository.ScoreRepository;
import com.guerrilla.scorecontroller.repository.dynamoDbImpl.ScoreDynamoDbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ScoreServiceTest {
    @Mock
    private ScoreRepository scoreRepository;

    private ScoreService scoreService;

    @BeforeEach
    public void setUp() {
        scoreRepository = Mockito.mock(ScoreDynamoDbRepository.class);
        scoreService = new ScoreService(scoreRepository);
    }

    @Test
    public void testCreateScore() {
        Score score = Score.builder()
                .scoreId(UUID.randomUUID())
                .playerId(UUID.randomUUID())
                .value(69)
                .build();

        when(scoreRepository.createScore(score.getPlayerId(), score.getValue())).thenReturn(score);

        Score createdScore = scoreService.createScore(score.getPlayerId(), score.getValue());

        assertNotNull(createdScore);
        assertEquals(score.getScoreId(), createdScore.getScoreId());
        assertEquals(score.getValue(), createdScore.getValue());
    }

    @Test
    public void testGetScore() {
        Score score = Score.builder()
                .scoreId(UUID.randomUUID())
                .playerId(UUID.randomUUID())
                .value(69)
                .build();

        when(scoreRepository.getScore(score.getScoreId())).thenReturn(Optional.of(score));

        Score retrievedScore = scoreService.getScore(score.getScoreId());

        assertNotNull(retrievedScore);
        assertEquals(score.getScoreId(), retrievedScore.getScoreId());
        assertEquals(score.getValue(), retrievedScore.getValue());
    }

    @Test
    public void testGetScore_ScoreNotFoundException() {
        UUID scoreId = UUID.randomUUID();
        when(scoreRepository.getScore(scoreId)).thenReturn(Optional.empty());

        assertThrows(ScoreNotFoundException.class, () -> scoreService.getScore(scoreId));
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

        when(scoreRepository.getScoresByPlayer(playerId)).thenReturn(scores);

        List<Score> retrievedScores = scoreService.getScoresByPlayer(playerId);

        assertNotNull(retrievedScores);
        assertEquals(2, retrievedScores.size());
    }

    @Test
    public void testUpdateScore() {
        Integer newValue = 420;
        Score score = Score.builder()
                .scoreId(UUID.randomUUID())
                .playerId(UUID.randomUUID())
                .value(69)
                .build();

        when(scoreRepository.getScore(score.getScoreId())).thenReturn(Optional.of(score));

        score.setValue(newValue);

        when(scoreRepository.updateScore(score.getScoreId(), newValue)).thenReturn(Optional.of(score));

        Score result = scoreService.updateScore(score.getScoreId(), newValue);

        assertNotNull(result);
        assertEquals(score, result);
    }

    @Test
    public void testUpdateScoreScoreNotFoundException() {
        UUID scoreId = UUID.randomUUID();
        when(scoreRepository.updateScore(scoreId, 69)).thenReturn(Optional.empty());

        assertThrows(ScoreNotFoundException.class, () -> scoreService.updateScore(scoreId, 69));
    }

    @Test
    public void testDeleteScore() {
        UUID scoreId = UUID.randomUUID();
        scoreService.deleteScore(scoreId);

        verify(scoreRepository, times(1)).deleteScore(scoreId);
    }

}