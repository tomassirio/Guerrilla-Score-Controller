package com.guerrilla.scorecontroller.repository;

import com.guerrilla.scorecontroller.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;
import java.util.Random;

@Repository
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
                .tableName(PLAYER_TABLE) // Replace with your DynamoDB table name
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
}