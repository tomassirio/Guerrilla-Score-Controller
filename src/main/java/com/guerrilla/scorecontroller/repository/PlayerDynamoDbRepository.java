package com.guerrilla.scorecontroller.repository;

import com.guerrilla.scorecontroller.model.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Repository
@Slf4j
public class PlayerDynamoDbRepository implements PlayerRepository{
    private static final String PLAYER_TABLE = "PlayerTable";
    private static final String PLAYER_ID = "playerId";
    private static final String USER_NAME = "username";

    private final DynamoDbClient dynamoDbClient;

    @Autowired
    public PlayerDynamoDbRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public Player createPlayer(String username) {
        Player newPlayer = Player.builder()
                .playerId(generatePlayerId())
                .username(username)
                .build();
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(PLAYER_TABLE)
                .item(
                        Map.of(
                                PLAYER_ID, AttributeValue.builder()
                                        .s(newPlayer.getPlayerId().toString())
                                        .build(),
                                USER_NAME, AttributeValue.builder()
                                        .s(newPlayer.getUsername())
                                        .build()
                        )
                )
                .build();

        dynamoDbClient.putItem(putItemRequest);
        return newPlayer;
    }

    public Optional<Player> getPlayer(Long id) {
        Map<String, AttributeValue> key = Map.of(
                PLAYER_ID, AttributeValue.builder().s(id.toString()).build()
        );

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(PLAYER_TABLE)
                .key(key)
                .build();
        Map<String, AttributeValue> item = dynamoDbClient.getItem(getItemRequest).item();

        Optional<Player> optionalPlayer = Optional.empty();
        if (item != null) {
            AttributeValue playerIdAttribute = item.get(PLAYER_ID);
            AttributeValue usernameAttribute = item.get(USER_NAME);

            if (playerIdAttribute != null && usernameAttribute != null) {
                Long retrievedPlayerId = Long.valueOf(playerIdAttribute.s());
                String retrievedUsername = usernameAttribute.s();

                optionalPlayer = Optional.of(Player.builder()
                        .playerId(retrievedPlayerId)
                        .username(retrievedUsername)
                        .build());
            }
        }
        return optionalPlayer;
    }

    public List<Player> getPlayers() {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(PLAYER_TABLE)
                .build();

        List<Player> players = new ArrayList<>();
        try {
            ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
            scanResponse.items().forEach(item -> {
                Long playerId = Long.valueOf(item.get(PLAYER_ID).s());
                String username = item.get(USER_NAME).s();
                players.add(Player.builder()
                        .playerId(playerId)
                        .username(username)
                        .build());
            });
        } catch (Exception e) {
            log.error("Couldn't Scan table: " + PLAYER_TABLE);
        }
        return players;
    }

    public Optional<Player> updatePlayer(Long id, String username) {
        Map<String, AttributeValue> key = Map.of(
                PLAYER_ID, AttributeValue.builder().s(id.toString()).build()
        );

        Optional<Player> optionalPlayer = Optional.empty();

        if (playerExists(id)) {
            UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                    .tableName(PLAYER_TABLE)
                    .key(key)
                    .attributeUpdates(
                            Map.of(
                                    USER_NAME, AttributeValueUpdate.builder()
                                            .value(AttributeValue.builder().s(username).build())
                                            .action(AttributeAction.PUT)
                                            .build()
                            )
                    )
                    .returnValues(ReturnValue.ALL_NEW)
                    .build();

            Map<String, AttributeValue> attributes = dynamoDbClient.updateItem(updateItemRequest).attributes();

            if (!attributes.isEmpty()) {
                Long extractedPlayerId = Long.valueOf(attributes.get(PLAYER_ID).s());
                String extractedUserName = attributes.get(USER_NAME).s();

                optionalPlayer = Optional.of(Player.builder()
                        .playerId(extractedPlayerId)
                        .username(extractedUserName)
                        .build());
            }
        }

        return optionalPlayer;
    }

    private boolean playerExists(Long id) {
        Map<String, AttributeValue> key = Map.of(
                PLAYER_ID, AttributeValue.builder().s(id.toString()).build()
        );

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(PLAYER_TABLE)
                .key(key)
                .build();

        Map<String, AttributeValue> item = dynamoDbClient.getItem(getItemRequest).item();

        return item != null && !item.isEmpty();
    }

    public void deletePlayer(Long id) {
        Map<String, AttributeValue> key = Map.of(
                PLAYER_ID, AttributeValue.builder().s(id.toString()).build()
        );

        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .key(key)
                .tableName(PLAYER_TABLE)
                .build();

        dynamoDbClient.deleteItem(deleteItemRequest);
    }

    protected long generatePlayerId() {
        return new Random().nextLong();
    }
}