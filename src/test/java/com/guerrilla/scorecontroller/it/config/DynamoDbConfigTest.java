package com.guerrilla.scorecontroller.it.config;

import com.guerrilla.scorecontroller.controller.PlayerController;
import com.guerrilla.scorecontroller.controller.ScoreController;
import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.model.Score;
import com.guerrilla.scorecontroller.repository.dynamoDbImpl.PlayerDynamoDbRepository;
import com.guerrilla.scorecontroller.repository.dynamoDbImpl.ScoreDynamoDbRepository;
import com.guerrilla.scorecontroller.service.PlayerService;
import com.guerrilla.scorecontroller.service.ScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

import java.net.URI;

@TestConfiguration
@Profile("it")
@Slf4j
public class DynamoDbConfigTest {
    @Bean
    public MockMvc createMockMvcWithDynamoDbClient() {
        return MockMvcBuilders.standaloneSetup(
                        new PlayerController(
                                new PlayerService(
                                        new PlayerDynamoDbRepository(
                                                playerTableTest(dynamoDbClientTest(), "PlayerTable")))),
                        new ScoreController(
                                new ScoreService(
                                        new ScoreDynamoDbRepository(
                                                scoreTableTest(dynamoDbClientTest(), "ScoreTable")))))
                .build();
    }

    @Bean
    public DynamoDbClient dynamoDbClientTest() {
        return DynamoDbClient.builder()
                .region(Region.EU_WEST_2)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("access-key", "secret-key")
                ))
                .endpointOverride(URI.create("http://localhost:4566"))
                .build();
    }

    @Bean
    public DynamoDbTable<Player> playerTableTest(@Qualifier("dynamoDbClientTest") DynamoDbClient dynamoDbClient, @Value("PlayerTable") String playerTableName) {
        DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        TableSchema<Player> playerDocumentSchema = TableSchema.fromBean(Player.class);

        if (!tableExists(dynamoDbClient, playerTableName)) {
            DynamoDbTable<Player> playerTable = dynamoDbEnhancedClient.table(playerTableName, playerDocumentSchema);

            playerTable.createTable();
        }

        return dynamoDbEnhancedClient.table(playerTableName, playerDocumentSchema);
    }

    @Bean
    public DynamoDbTable<Score> scoreTableTest(@Qualifier("dynamoDbClientTest") DynamoDbClient dynamoDbClient, @Value("ScoreTable") String scoreTableName) {
        DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        TableSchema<Score> scoreDocumentSchema = TableSchema.fromBean(Score.class);

        if (!tableExists(dynamoDbClient, scoreTableName)) {
            DynamoDbTable<Score> scoreTable = dynamoDbEnhancedClient.table(scoreTableName, scoreDocumentSchema);

            scoreTable.createTable();
        }

        return dynamoDbEnhancedClient.table(scoreTableName, scoreDocumentSchema);
    }

    private boolean tableExists(DynamoDbClient dynamoDbClient, String tableName) {
        try {
            DescribeTableResponse response = dynamoDbClient.describeTable(DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build());
            return response != null && response.table().tableStatus().equals(TableStatus.ACTIVE);
        } catch (ResourceNotFoundException e) {
            log.warn("Table: " + tableName + " Doesn't exist yet");
            return false;
        }
    }
}
