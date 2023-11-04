package com.guerrilla.scorecontroller.config;

import com.guerrilla.scorecontroller.model.Score;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbConfig {
    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public DynamoDbClient dynamoDbClient() {
//        AwsCredentialsProvider credentialsProvider =
//                DefaultCredentialsProvider.builder()
//                        .profileName("pratikpoc")
//                        .build();

        return DynamoDbClient.builder()
                .region(Region.of(awsRegion))
                .build();
    }

    @Bean
    public DynamoDbTable<Score> scoreTable(DynamoDbClient dynamoDbClient, @Value("${score.table}") String scoreTableName) {
        DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        TableSchema<Score> scoreDocumentSchema = TableSchema.fromBean(Score.class);
        return dynamoDbEnhancedClient.table(scoreTableName, scoreDocumentSchema);
    }

    @Bean
    public DynamoDbTable<Score> playerTable(DynamoDbClient dynamoDbClient, @Value("${player.table}") String playerTableName) {
        DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        TableSchema<Score> scoreDocumentSchema = TableSchema.fromBean(Score.class);
        return dynamoDbEnhancedClient.table(playerTableName, scoreDocumentSchema);
    }
}