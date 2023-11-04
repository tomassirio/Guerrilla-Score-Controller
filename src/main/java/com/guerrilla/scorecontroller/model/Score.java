package com.guerrilla.scorecontroller.model;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@DynamoDbBean
public class Score {

    private UUID scoreId;
    private UUID playerId;
    private Integer value;
    @DynamoDbPartitionKey
    public UUID getScoreId() {return this.scoreId;}
}