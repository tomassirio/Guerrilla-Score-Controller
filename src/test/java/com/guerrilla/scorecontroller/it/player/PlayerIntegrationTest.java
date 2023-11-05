package com.guerrilla.scorecontroller.it.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guerrilla.scorecontroller.it.config.DynamoDbConfigTest;
import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.model.Score;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
@ContextConfiguration(classes = DynamoDbConfigTest.class)
@Testcontainers
public class PlayerIntegrationTest {
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Container
    static LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.2"));

    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException {
        localStack.execInContainer("awslocal", "dynamodb", "create-table",
                "--table-name", "PlayerTableTest",
                "--attribute-definitions", "AttributeName=playerId,AttributeType=S",
                "--attribute-definitions", "AttributeName=username,AttributeType=S",
                "--key-schema", "AttributeName=playerId,KeyType=HASH",
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
    public void testCreateAndGetPlayer() throws Exception {
        String username = "Mr Potato";

        MvcResult createResultPlayer = mockMvc.perform(post("/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", username)
                        .content(objectMapper.writeValueAsString(new Player())))
                .andExpect(status().isOk())
                .andReturn();

        String responseContentPlayerCreated = createResultPlayer.getResponse().getContentAsString();

        Player createdPlayer = objectMapper.readValue(responseContentPlayerCreated, Player.class);

        MvcResult getResultPlayer = mockMvc.perform(get("/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("playerId", createdPlayer.getPlayerId().toString())
                        .content(objectMapper.writeValueAsString(new Score())))
                .andExpect(status().isOk())
                .andReturn();

        String responseContentPlayerRetrieved = getResultPlayer.getResponse().getContentAsString();

        Player retrievedPlayer = objectMapper.readValue(responseContentPlayerRetrieved, Player.class);

        Assertions.assertEquals(username, retrievedPlayer.getUsername());
        Assertions.assertEquals(createdPlayer, retrievedPlayer);
    }

    @Test
    public void testUpdatePlayerUsername() throws Exception {
        String initialUsername = "Mr Potato";
        String updatedUsername = "Rex";

        MvcResult createResultPlayer = mockMvc.perform(post("/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", initialUsername)
                        .content(objectMapper.writeValueAsString(new Player())))
                .andExpect(status().isOk())
                .andReturn();

        String responseContentPlayerCreated = createResultPlayer.getResponse().getContentAsString();
        Player createdPlayer = objectMapper.readValue(responseContentPlayerCreated, Player.class);

        MvcResult updateResultPlayer = mockMvc.perform(put("/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("playerId", createdPlayer.getPlayerId().toString())
                        .param("userName", updatedUsername)
                        .content(objectMapper.writeValueAsString(new Score())))
                .andExpect(status().isOk())
                .andReturn();

        String responseContentPlayerUpdated = updateResultPlayer.getResponse().getContentAsString();
        Player updatedPlayer = objectMapper.readValue(responseContentPlayerUpdated, Player.class);

        Assertions.assertEquals(updatedUsername, updatedPlayer.getUsername());
        Assertions.assertNotEquals(initialUsername, updatedPlayer.getUsername());
    }

    @Test
    public void testDeletePlayer() throws Exception {
        MvcResult createResultPlayer = mockMvc.perform(post("/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "Mr Potato")
                        .content(objectMapper.writeValueAsString(new Player())))
                .andExpect(status().isOk())
                .andReturn();

        String responseContentPlayerCreated = createResultPlayer.getResponse().getContentAsString();
        Player createdPlayer = objectMapper.readValue(responseContentPlayerCreated, Player.class);
        UUID playerId = createdPlayer.getPlayerId();

        mockMvc.perform(delete("/player")
                        .param("playerId", playerId.toString()))
                .andExpect(status().isOk());
    }
}