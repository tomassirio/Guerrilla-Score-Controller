package com.guerrilla.scorecontroller.repository;

import com.guerrilla.scorecontroller.model.Score;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScoreRepository {
    Score createScore(Long playerId, Integer value);
    Optional<Score> getScore(UUID scoreId);
    List<Score> getScoresByPlayer(Long playerId);
    Optional<Score> updateScore(UUID scoreId, Integer value);
    void deleteScore(UUID scoreId);
}