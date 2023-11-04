package com.guerrilla.scorecontroller.repository.dynamoDbImpl;

import com.guerrilla.scorecontroller.model.Score;
import com.guerrilla.scorecontroller.repository.ScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ScoreDynamoDbRepositoryTest {

    @Mock
    private DynamoDbTable<Score> scoreTable;

    private ScoreRepository scoreRepository;

    @BeforeEach
    public void setUp() {
        scoreTable = Mockito.mock(DynamoDbTable.class);
        scoreRepository = new ScoreDynamoDbRepository(scoreTable);
    }

    @Test
    public void testCreateScore() {
        Score score = Score.builder()
                .scoreId(UUID.randomUUID())
                .playerId(1L)
                .value(69)
                .build();

        ScoreDynamoDbRepository scoreRepository = new ScoreDynamoDbRepository(scoreTable) {
            @Override
            protected UUID generateScoreId() {
                return score.getScoreId();
            }
        };

        ArgumentCaptor<PutItemEnhancedRequest> putItemRequestCaptor = ArgumentCaptor.forClass(PutItemEnhancedRequest.class);

        Score newScore = scoreRepository.createScore(score.getPlayerId(), score.getValue());
        verify(scoreTable).putItem(putItemRequestCaptor.capture());


        PutItemEnhancedRequest putItemRequest = putItemRequestCaptor.getValue();
        assertEquals(putItemRequest.item(), newScore);
    }

    @Test
    public void testGetScore() {
        Score score = Score.builder()
                .scoreId(UUID.randomUUID())
                .playerId(1L)
                .value(69)
                .build();

        GetItemEnhancedRequest getItemRequest = GetItemEnhancedRequest.builder()
                .key(
                        Key.builder()
                                .partitionValue(score.getScoreId().toString())
                                .build()
                )
                .build();

        when(scoreTable.getItem(getItemRequest)).thenReturn(score);

        Optional<Score> retrievedScore = scoreRepository.getScore(score.getScoreId());

        assertTrue(retrievedScore.isPresent());
        assertEquals(score.getScoreId(), retrievedScore.get().getScoreId());
        assertEquals(score.getPlayerId(), retrievedScore.get().getPlayerId());
        assertEquals(score.getValue(), retrievedScore.get().getValue());
    }

    @Test
    public void testGetScoreNotFound() {
        UUID scoreId = UUID.randomUUID();

        GetItemEnhancedRequest getItemEnhancedRequest = GetItemEnhancedRequest.builder()
                .key(
                        Key.builder()
                                .partitionValue(scoreId.toString())
                                .build())
                .build();

        when(scoreTable.getItem(getItemEnhancedRequest)).thenThrow(UnsupportedOperationException.class);

        Optional<Score> retrievedScore = scoreRepository.getScore(scoreId);

        assertTrue(retrievedScore.isEmpty());
    }

    @Test
    public void testUpdateScore() {
        UUID scoreId = UUID.randomUUID();
        Long playerId = 1L;
        Integer initialValue = 69;
        Integer updatedValue = 420;
        ScoreDynamoDbRepository scoreRepository = new ScoreDynamoDbRepository(scoreTable) {
            @Override
            protected UUID generateScoreId() {
                return scoreId;
            }
        };

        Score newScore = scoreRepository.createScore(playerId, initialValue);

        Score expectedScore = Score.builder()
                .scoreId(scoreId)
                .playerId(playerId)
                .value(initialValue)
                .build();

        assertEquals(expectedScore, newScore);

        expectedScore.setValue(updatedValue);

        UpdateItemEnhancedRequest expectedUpdateItemRequest = UpdateItemEnhancedRequest.builder(Score.class)
                .item(expectedScore)
                .build();

        when(scoreTable.getItem(any(GetItemEnhancedRequest.class))).thenReturn(expectedScore);
        when(scoreTable.updateItem(expectedUpdateItemRequest)).thenReturn(expectedScore);

        Optional<Score> updatedScore = scoreRepository.updateScore(scoreId, updatedValue);

        assertTrue(updatedScore.isPresent());
        assertEquals(scoreId, updatedScore.get().getScoreId());
        assertEquals(1L, updatedScore.get().getPlayerId());
        assertEquals(updatedValue, updatedScore.get().getValue());
    }

    @Test
    public void testUpdateScoreNotFound() {
        when(scoreTable.getItem(any(GetItemEnhancedRequest.class))).thenThrow(UnsupportedOperationException.class);

        UUID nonExistentScoreId = UUID.randomUUID();
        int updatedValue = 200;

        Optional<Score> updatedScore = scoreRepository.updateScore(nonExistentScoreId, updatedValue);

        assertFalse(updatedScore.isPresent());
    }

    @Test
    public void testDeleteScore() {
        UUID scoreId = UUID.randomUUID();

        scoreRepository.deleteScore(scoreId);

        verify(scoreTable, times(1)).deleteItem(any(DeleteItemEnhancedRequest.class));
    }
}
