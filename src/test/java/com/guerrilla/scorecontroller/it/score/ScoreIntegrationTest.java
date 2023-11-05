package com.guerrilla.scorecontroller.it.score;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guerrilla.scorecontroller.it.config.DynamoDbConfigTest;
import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.model.Score;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@SpringBootTest
@ActiveProfiles("it")
@ContextConfiguration(classes = DynamoDbConfigTest.class)
@Testcontainers
public class ScoreIntegrationTest {
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Container
    static LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.2"));

    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException {
        localStack.execInContainer("awslocal", "dynamodb", "create-table",
                "--table-name", "PlayerTable",
                "--attribute-definitions", "AttributeName=playerId,AttributeType=S",
                "--attribute-definitions", "AttributeName=username,AttributeType=S",
                "--key-schema", "AttributeName=playerId,KeyType=HASH",
                "--provisioned-throughput", "ReadCapacityUnits=5,WriteCapacityUnits=5",
                "awslocal", "dynamodb", "create-table",
                "--table-name", "ScoreTable",
                "--attribute-definitions", "AttributeName=scoreId,AttributeType=S",
                "--attribute-definitions", "AttributeName=playerId,AttributeType=S",
                "--attribute-definitions", "AttributeName=value,AttributeType=S",
                "--key-schema", "AttributeName=scoreId,KeyType=HASH",
                "--provisioned-throughput", "ReadCapacityUnits=5,WriteCapacityUnits=5"
        );
    }

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.aws.dynamodb.endpoint", () -> localStack.getEndpointOverride(DYNAMODB));
        registry.add("spring.cloud.aws.credentials.access-key", () -> localStack.getAccessKey());
        registry.add("spring.cloud.aws.credentials.secret-key", () -> localStack.getSecretKey());
        registry.add("spring.cloud.aws.region.static", () -> localStack.getRegion());
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateAndGetScore() throws Exception {
        int value = 69;

        MvcResult createResultPlayer = mockMvc.perform(post("/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "username")
                        .content(objectMapper.writeValueAsString(new Player())))
                .andExpect(status().isOk())
                .andReturn();

        String responseContentPlayer = createResultPlayer.getResponse().getContentAsString();

        Player createdPlayer = objectMapper.readValue(responseContentPlayer, Player.class);

        MvcResult createResultScore = mockMvc.perform(post("/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("playerId", createdPlayer.getPlayerId().toString())
                        .param("value", Integer.toString(value))
                        .content(objectMapper.writeValueAsString(new Score())))
                .andExpect(status().isOk())
                .andReturn();

        String responseContentScore = createResultScore.getResponse().getContentAsString();

        Score createdScore = objectMapper.readValue(responseContentScore, Score.class);

        Assertions.assertEquals("username", createdPlayer.getUsername());
        Assertions.assertEquals(createdPlayer.getPlayerId(), createdScore.getPlayerId());
        Assertions.assertEquals(Integer.valueOf(value), createdScore.getValue());

    }

    @Test
    public void testUpdateScoreValue() throws Exception {
        Integer initialValue = 69;
        Integer updatedValue = 420;

        MvcResult createResultPlayer = mockMvc.perform(post("/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "Mr Potato")
                        .content(objectMapper.writeValueAsString(new Player())))
                .andExpect(status().isOk())
                .andReturn();

        String responseContentPlayer = createResultPlayer.getResponse().getContentAsString();
        Player createdPlayer = objectMapper.readValue(responseContentPlayer, Player.class);

        MvcResult createResultScore = mockMvc.perform(post("/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("playerId", createdPlayer.getPlayerId().toString())
                        .param("value", Integer.toString(initialValue))
                        .content(objectMapper.writeValueAsString(new Score())))
                .andExpect(status().isOk())
                .andReturn();

        String responseContentScore = createResultScore.getResponse().getContentAsString();
        Score createdScore = objectMapper.readValue(responseContentScore, Score.class);

        MvcResult updateResultScore = mockMvc.perform(put("/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("scoreId", createdScore.getScoreId().toString())
                        .param("value", Integer.toString(updatedValue))
                        .content(objectMapper.writeValueAsString(new Score())))
                .andExpect(status().isOk())
                .andReturn();

        String responseContentUpdatedScore = updateResultScore.getResponse().getContentAsString();
        Score updatedScore = objectMapper.readValue(responseContentUpdatedScore, Score.class);

        Assertions.assertEquals(updatedValue, updatedScore.getValue());
        Assertions.assertNotEquals(initialValue, updatedScore.getValue());
    }

    @Test
    public void testDeleteScore() throws Exception {
        MvcResult createResultPlayer = mockMvc.perform(post("/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "Mr Potato")
                        .content(objectMapper.writeValueAsString(new Player())))
                .andExpect(status().isOk())
                .andReturn();

        String responseContentPlayer = createResultPlayer.getResponse().getContentAsString();
        Player createdPlayer = objectMapper.readValue(responseContentPlayer, Player.class);

        MvcResult createResultScore = mockMvc.perform(post("/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("playerId", createdPlayer.getPlayerId().toString())
                        .param("value", Integer.toString(69))
                        .content(objectMapper.writeValueAsString(new Score())))
                .andExpect(status().isOk())
                .andReturn();

        String responseContentScore = createResultScore.getResponse().getContentAsString();
        Score createdScore = objectMapper.readValue(responseContentScore, Score.class);

        mockMvc.perform(delete("/score")
                        .param("scoreId", createdScore.getScoreId().toString()))
                .andExpect(status().isOk());
    }

}