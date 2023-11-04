package com.guerrilla.scorecontroller.it.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guerrilla.scorecontroller.it.config.AwsConfig;
import com.guerrilla.scorecontroller.it.config.TestcontainersConfig;
import com.guerrilla.scorecontroller.it.config.DbConfig;
import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.model.Score;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestcontainersConfig.class, AwsConfig.class, DbConfig.class})
public class PlayerIntegrationTest {
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("PlayerTableTest")
    DynamoDbTable<Player> playerTableTest;

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

        Assert.assertEquals(username, retrievedPlayer.getUsername());
        Assert.assertEquals(createdPlayer, retrievedPlayer);
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

        Assert.assertEquals(updatedUsername, updatedPlayer.getUsername());
        Assert.assertNotEquals(initialUsername, updatedPlayer.getUsername());
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