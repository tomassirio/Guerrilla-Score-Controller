package com.guerrilla.scorecontroller.repository.dynamoDbImpl.config;

import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.model.Score;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

@Slf4j
@Configuration
public class DynamoDbConfig {
    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.EU_WEST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Primary
    @Bean
    public DynamoDbTable<Score> scoreTable(DynamoDbClient dynamoDbClient, @Value("${cloud.aws.dynamo.score.table}") String scoreTableName) {
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

    @Primary
    @Bean
    public DynamoDbTable<Player> playerTable(DynamoDbClient dynamoDbClient, @Value("${cloud.aws.dynamo.player.table}") String playerTableName) {
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