package com.guerrilla.scorecontroller.it.score.config;

import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.model.Score;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@TestConfiguration(proxyBeanMethods = false)
public class ScoreDbConfig {

    @Autowired
    LocalStackContainer localStack;

    @Bean("DynamoDbClientTest")
    public DynamoDbClient scoreDbClientTest(AwsCredentialsProvider credentialsProvider) {
        return DynamoDbClient.builder()
                .credentialsProvider(credentialsProvider)
                .endpointOverride(localStack.getEndpointOverride(DYNAMODB))
                .build();
    }

    @EventListener
    @SuppressWarnings("unchecked")
    public void onApplicationReady(ApplicationReadyEvent applicationReadyEvent) {
        ApplicationContext applicationContext = applicationReadyEvent.getApplicationContext();
        DynamoDbTable<Score> scoreTable = applicationContext.getBean("scoreTableTest", DynamoDbTable.class);
        DynamoDbTable<Player> playerTable = applicationContext.getBean("playerTableTest", DynamoDbTable.class);

        scoreTable.createTable();
        playerTable.createTable();
    }

    @Bean
    public DynamoDbTable<Score> scoreTableTest(@Qualifier("DynamoDbClientTest") DynamoDbClient dynamoDbClient, @Value("${score.table}") String scoreTableName) {
        DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        TableSchema<Score> scoreDocumentSchema = TableSchema.fromBean(Score.class);
        return dynamoDbEnhancedClient.table(scoreTableName, scoreDocumentSchema);
    }

    @Bean
    public DynamoDbTable<Player> playerTableTest(@Qualifier("DynamoDbClientTest") DynamoDbClient dynamoDbClient, @Value("${player.table}") String playerTableName) {
        DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        TableSchema<Player> playerDocumentSchema = TableSchema.fromBean(Player.class);
        return dynamoDbEnhancedClient.table(playerTableName, playerDocumentSchema);
    }
}