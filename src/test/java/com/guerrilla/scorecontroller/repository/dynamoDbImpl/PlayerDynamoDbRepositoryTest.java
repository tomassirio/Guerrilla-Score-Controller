package com.guerrilla.scorecontroller.repository.dynamoDbImpl;

import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.model.Score;
import com.guerrilla.scorecontroller.repository.PlayerRepository;
import com.guerrilla.scorecontroller.repository.ScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PlayerDynamoDbRepositoryTest {

    @Mock
    private DynamoDbTable<Player> playerTable;

    private PlayerRepository playerRepository;

    @BeforeEach
    public void setUp() {
        playerTable = Mockito.mock(DynamoDbTable.class);
        playerRepository = new PlayerDynamoDbRepository(playerTable);
    }


    @Test
    public void testCreatePlayer() {
        UUID playerId = UUID.randomUUID();
        String username = "Mr Potato";

        Player expectedPlayer = Player.builder()
                .playerId(playerId)
                .username(username)
                .build();
        PlayerDynamoDbRepository playerRepository = new PlayerDynamoDbRepository(playerTable) {
            @Override
            protected UUID generatePlayerId() {
                return playerId;
            }
        };

        ArgumentCaptor<PutItemEnhancedRequest> putItemRequestCaptor = ArgumentCaptor.forClass(PutItemEnhancedRequest.class);

        Player newPlayer = playerRepository.createPlayer(username);
        verify(playerTable, times(1)).putItem(putItemRequestCaptor.capture());

        assertEquals(expectedPlayer, newPlayer);

        PutItemEnhancedRequest putItemRequest = putItemRequestCaptor.getValue();
        assertEquals(putItemRequest.item(), newPlayer);
    }

    @Test
    public void testGetPlayer() {
        UUID playerId = UUID.randomUUID();
        String username = "Mr Potato";

        Player player = Player.builder()
                .playerId(playerId)
                .username(username)
                .build();

        GetItemEnhancedRequest getItemRequest = GetItemEnhancedRequest.builder()
                .key(
                        Key.builder()
                                .partitionValue(player.getPlayerId().toString())
                                .build()
                )
                .build();

        when(playerTable.getItem(getItemRequest)).thenReturn(player);

        Optional<Player> retrievedPlayer = playerRepository.getPlayer(playerId);

        assertTrue(retrievedPlayer.isPresent());
        assertEquals(playerId, retrievedPlayer.get().getPlayerId());
        assertEquals(username, retrievedPlayer.get().getUsername());
    }

    @Test
    public void testGetPlayer_PlayerNotFound() {
        UUID playerId = UUID.randomUUID();
        String username = "Mr Potato";

        Player player = Player.builder()
                .playerId(playerId)
                .username(username)
                .build();

        GetItemEnhancedRequest getItemRequest = GetItemEnhancedRequest.builder()
                .key(
                        Key.builder()
                                .partitionValue(player.getPlayerId().toString())
                                .build()
                )
                .build();

        when(playerTable.getItem(getItemRequest)).thenThrow(UnsupportedOperationException.class);

        Optional<Player> retrievedPlayer = playerRepository.getPlayer(playerId);

        assertTrue(retrievedPlayer.isEmpty());
    }
}