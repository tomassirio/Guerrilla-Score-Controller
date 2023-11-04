package com.guerrilla.scorecontroller.it.config;

import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.model.Score;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@TestConfiguration(proxyBeanMethods = false)
@Profile("test")
@Slf4j
public class DbConfig {

    @Autowired
    LocalStackContainer localStack;

    @Bean("DynamoDbClientLocal")
    public DynamoDbClient dynamoDbClientLocal(AwsCredentialsProvider credentialsProvider) {
        return DynamoDbClient.builder()
                .credentialsProvider(credentialsProvider)
                .endpointOverride(localStack.getEndpointOverride(DYNAMODB))
                .build();
    }
    @Bean("ScoreTableTest")
    public DynamoDbTable<Score> scoreTableLocal(@Qualifier("DynamoDbClientLocal") DynamoDbClient dynamoDbClient, @Value("${score.table}") String scoreTableName) {
        DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        TableSchema<Score> scoreDocumentSchema = TableSchema.fromBean(Score.class);

        if (!doesTableExist(dynamoDbClient, scoreTableName)) {
            DynamoDbTable<Score> scoreTable = dynamoDbEnhancedClient.table(scoreTableName, scoreDocumentSchema);

            scoreTable.createTable();
        }

        return dynamoDbEnhancedClient.table(scoreTableName, scoreDocumentSchema);
    }

    @Bean("PlayerTableTest")
    public DynamoDbTable<Player> playerTableTest(@Qualifier("DynamoDbClientLocal") DynamoDbClient dynamoDbClient, @Value("${player.table}") String playerTableName) {
        DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        TableSchema<Player> playerDocumentSchema = TableSchema.fromBean(Player.class);

        if (!doesTableExist(dynamoDbClient, playerTableName)) {
            DynamoDbTable<Player> playerTable = dynamoDbEnhancedClient.table(playerTableName, playerDocumentSchema);

            playerTable.createTable();
        }

        return dynamoDbEnhancedClient.table(playerTableName, playerDocumentSchema);
    }


    private boolean doesTableExist(DynamoDbClient dynamoDbClient, String tableName) {
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