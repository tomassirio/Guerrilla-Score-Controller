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
public class PlayerRepository {
    private static final String PLAYER_TABLE = "PlayerTable";
    private static final String PLAYER_ID = "playerId";
    private static final String USER_NAME = "username";

    private final DynamoDbClient dynamoDbClient;

    @Autowired
    public PlayerRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public Player createPlayer(String username) {
        Random rd = new Random();
        Player newPlayer = new Player(rd.nextLong(), username);
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(PLAYER_TABLE)
                .item(
                        Map.of(
                                PLAYER_ID, AttributeValue.builder().s(newPlayer.getPlayerId().toString()).build(),
                                USER_NAME, AttributeValue.builder().s(newPlayer.getUsername()).build()
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
            Long retrievedPlayerId = Long.valueOf(item.get(PLAYER_ID).s());
            String retrievedUsername = item.get(USER_NAME).s();

            optionalPlayer = Optional.of(new Player(retrievedPlayerId, retrievedUsername));
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
                players.add(new Player(playerId, username));
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
                .build();

        Map<String, AttributeValue> attributes = dynamoDbClient.updateItem(updateItemRequest).attributes();

        Optional<Player> optionalPlayer = Optional.empty();
        if (!attributes.isEmpty()) {
            Long extractedPlayerId = Long.valueOf(attributes.get(PLAYER_ID).s());
            String extractedUserName = attributes.get(USER_NAME).s();

            optionalPlayer = Optional.of(new Player(extractedPlayerId, extractedUserName));
        }
        return optionalPlayer;
    }

    public void deletePlayer(Long id) {
        Map<String, AttributeValue> key = Map.of(
                PLAYER_ID, AttributeValue.builder().s(id.toString()).build()
        );

        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .key(key)
                .build();

        dynamoDbClient.deleteItem(deleteItemRequest);
    }
}