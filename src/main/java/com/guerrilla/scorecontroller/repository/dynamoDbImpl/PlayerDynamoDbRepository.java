package com.guerrilla.scorecontroller.repository.dynamoDbImpl;

import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.model.Score;
import com.guerrilla.scorecontroller.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.*;

@Repository
@Slf4j
public class PlayerDynamoDbRepository implements PlayerRepository {
    private final DynamoDbTable<Player> playerTable;

    @Autowired
    public PlayerDynamoDbRepository(DynamoDbTable<Player> playerTable) {
        this.playerTable = playerTable;
    }

    public Player createPlayer(String username) {
        Player newPlayer = Player.builder()
                .playerId(generatePlayerId())
                .username(username)
                .build();

        PutItemEnhancedRequest<Player> putItemEnhancedRequest = PutItemEnhancedRequest.builder(Player.class).item(newPlayer).build();

        playerTable.putItem(putItemEnhancedRequest);
        return newPlayer;
    }

    public Optional<Player> getPlayer(UUID playerId) {
        log.info("Fetching player for playerId: " + playerId);

        GetItemEnhancedRequest getItemEnhancedRequest = GetItemEnhancedRequest.builder()
                .key(
                        Key.builder()
                                .partitionValue(playerId.toString())
                                .build())
                .build();

        Optional<Player> optionalPlayer = Optional.empty();
        try {
            optionalPlayer = Optional.of(playerTable.getItem(getItemEnhancedRequest));
            log.info("Player retrieved: " + optionalPlayer);
        } catch (UnsupportedOperationException exception) {
            log.error(exception.getLocalizedMessage());
            return optionalPlayer;
        }
        return optionalPlayer;
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        PageIterable<Player> playerPage = playerTable.scan(ScanEnhancedRequest.builder().build());
        playerPage.items().forEach(players::add);
        return players;
    }

    public Optional<Player> updatePlayer(UUID id, String username) {
        Optional<Player> optionalPlayer = getPlayer(id);

        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            player.setUsername(username);

            playerTable.updateItem(player);
        }

        return optionalPlayer;
    }

    public void deletePlayer(UUID id) {
        playerTable.deleteItem(Key.builder().partitionValue(id.toString()).build());
        log.info("Player Deleted: " + id);
    }

    protected UUID generatePlayerId() {
        return UUID.randomUUID();
    }
}