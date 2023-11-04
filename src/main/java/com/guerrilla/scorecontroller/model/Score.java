package com.guerrilla.scorecontroller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Score {
    private UUID scoreId;
    private Long playerId;
    private Integer value;
    @DynamoDbPartitionKey
    public UUID getScoreId() {
        return scoreId;
    }

    @DynamoDbSortKey
    public Long getPlayerId() {
        return playerId;
    }
}