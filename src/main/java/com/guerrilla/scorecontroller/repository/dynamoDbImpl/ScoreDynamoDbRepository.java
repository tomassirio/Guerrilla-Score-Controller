package com.guerrilla.scorecontroller.repository.dynamoDbImpl;

import com.guerrilla.scorecontroller.model.Score;
import com.guerrilla.scorecontroller.repository.ScoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.*;

@Slf4j
@Repository
public class ScoreDynamoDbRepository implements ScoreRepository {
    private final DynamoDbTable<Score> scoreTable;

    @Autowired
    public ScoreDynamoDbRepository(@Qualifier("ScoreTable") DynamoDbTable<Score> scoreTable) {
        this.scoreTable = scoreTable;
    }

    public Score createScore(UUID playerId, Integer value) {
        Score newScore = Score.builder()
                .scoreId(generateScoreId())
                .playerId(playerId)
                .value(value)
                .build();

        PutItemEnhancedRequest<Score> putItemEnhancedRequest = PutItemEnhancedRequest.builder(Score.class).item(newScore).build();

        scoreTable.putItem(putItemEnhancedRequest);
        return newScore;
    }

    public Optional<Score> getScore(UUID scoreId) {
        log.info("Fetching score for scoreId: " + scoreId);
        GetItemEnhancedRequest getItemEnhancedRequest = GetItemEnhancedRequest.builder()
                .key(
                        Key.builder()
                                .partitionValue(scoreId.toString())
                                .build())
                .build();

        Optional<Score> optionalScore = Optional.empty();
        try {
            optionalScore = Optional.of(scoreTable.getItem(getItemEnhancedRequest));
            log.info("Score retrieved: " + optionalScore);
        } catch (UnsupportedOperationException exception) {
            log.error(exception.getLocalizedMessage());
            return optionalScore;
        }
        return optionalScore;
    }

    public List<Score> getScoresByPlayer(UUID playerId) {
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(
                        QueryConditional.keyEqualTo(Key.builder()
                                .sortValue(playerId.toString())
                                .build()))
                .build();

        List<Score> scores = new ArrayList<>();
        try {
            PageIterable<Score> queryResponse = scoreTable.query(queryRequest);

            if (queryResponse != null) {
                queryResponse.items().forEach(scores::add);
            } else {
                log.warn("Query response is null for player: " + playerId);
            }
        } catch (UnsupportedOperationException e) {
            log.error("Couldn't query table: " + scoreTable.tableName(), e);
        }
        return scores;
    }

    public Optional<Score> updateScore(UUID scoreId, Integer value) {
        Optional<Score> optionalScore = getScore(scoreId);

        if (optionalScore.isPresent()) {
            Score score = optionalScore.get();
            score.setValue(value);

            UpdateItemEnhancedRequest<Score> updateItemEnhancedRequest = UpdateItemEnhancedRequest.builder(Score.class)
                    .item(score)
                    .build();

            optionalScore = Optional.of(scoreTable.updateItem(updateItemEnhancedRequest));
        }

        return optionalScore;
    }

    public void deleteScore(UUID scoreId) {
        DeleteItemEnhancedRequest deleteItemEnhancedRequest = DeleteItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue(scoreId.toString())
                        .build())
                .build();


        scoreTable.deleteItem(deleteItemEnhancedRequest);
    }

    protected UUID generateScoreId() {
        return UUID.randomUUID();
    }
}