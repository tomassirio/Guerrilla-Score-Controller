package com.guerrilla.scorecontroller.repository;

import com.guerrilla.scorecontroller.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PlayerDynamoDbRepositoryTest {

    @Mock
    private DynamoDbClient dynamoDbClient;

    private PlayerRepository playerRepository;

    @BeforeEach
    public void setUp() {
        dynamoDbClient = Mockito.mock(DynamoDbClient.class);
        playerRepository = new PlayerDynamoDbRepository(dynamoDbClient);
    }

    @Test
    public void testCreatePlayer() {
        Long playerId = 1L;
        String username = "Mr Potato";
        PlayerDynamoDbRepository playerRepository = new PlayerDynamoDbRepository(dynamoDbClient) {
            @Override
            protected long generatePlayerId() {
                return playerId;
            }
        };

        Player expectedPlayer = Player.builder()
                .playerId(playerId)
                .username(username)
                .build();

        ArgumentCaptor<PutItemRequest> putItemRequestCaptor = ArgumentCaptor.forClass(PutItemRequest.class);

        when(dynamoDbClient.putItem(putItemRequestCaptor.capture())).thenReturn(PutItemResponse.builder().build());

        Player newPlayer = playerRepository.createPlayer(username);

        assertEquals(expectedPlayer, newPlayer);

        PutItemRequest putItemRequest = putItemRequestCaptor.getValue();
        assertEquals(playerId.toString(), putItemRequest.item().get("playerId").s());
        assertEquals(username, putItemRequest.item().get("username").s());
    }

    @Test
    public void testGetPlayer() {
        Long playerId = 1L;
        Map<String, AttributeValue> item = Map.of(
                "playerId", AttributeValue.builder().s("1").build(),
                "username", AttributeValue.builder().s("Mr Potato").build()
        );

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName("PlayerTable")
                .key(Collections.singletonMap("playerId", AttributeValue.builder().s("1").build()))
                .build();

        when(dynamoDbClient.getItem(getItemRequest)).thenReturn(GetItemResponse.builder().item(item).build());

        Optional<Player> retrievedPlayer = playerRepository.getPlayer(playerId);

        assertTrue(retrievedPlayer.isPresent());
        assertEquals(1L, retrievedPlayer.get().getPlayerId());
        assertEquals("Mr Potato", retrievedPlayer.get().getUsername());
    }

    @Test
    public void testGetPlayer_PlayerNotFound() {
        Long playerId = 1L;

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName("PlayerTable")
                .key(Collections.singletonMap("playerId", AttributeValue.builder().s("1").build()))
                .build();

        when(dynamoDbClient.getItem(getItemRequest)).thenReturn(GetItemResponse.builder().build());

        Optional<Player> retrievedPlayer = playerRepository.getPlayer(playerId);

        assertFalse(retrievedPlayer.isPresent());
    }
}