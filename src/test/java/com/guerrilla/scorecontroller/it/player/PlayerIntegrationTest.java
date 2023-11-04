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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}