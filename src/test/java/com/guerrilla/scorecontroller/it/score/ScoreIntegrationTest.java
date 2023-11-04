package com.guerrilla.scorecontroller.it.score;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guerrilla.scorecontroller.it.config.AwsConfig;
import com.guerrilla.scorecontroller.it.config.TestcontainersConfig;
import com.guerrilla.scorecontroller.it.config.DbConfig;
import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.model.Score;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestcontainersConfig.class, AwsConfig.class, DbConfig.class})
public class ScoreIntegrationTest {
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("ScoreTableTest")
    DynamoDbTable<Score> scoreTableTest;

    @Autowired
    @Qualifier("PlayerTableTest")
    DynamoDbTable<Player> playerTableTest;

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

        assertEquals("username", createdPlayer.getUsername());
        assertEquals(createdPlayer.getPlayerId(), createdScore.getPlayerId());
        assertEquals(Integer.valueOf(value), createdScore.getValue());

    }

    @Test
    public void testUpdateScoreValue() throws Exception {
        Integer initialValue = 50;
        Integer updatedValue = 75;

        MvcResult createResultPlayer = mockMvc.perform(post("/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "TestPlayer")
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

        assertEquals(updatedValue, updatedScore.getValue());
        assertNotEquals(initialValue, updatedScore.getValue());
    }

    @Test
    public void testDeleteScore() throws Exception {
        // Create a player
        MvcResult createResultPlayer = mockMvc.perform(post("/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "TestPlayer")
                        .content(objectMapper.writeValueAsString(new Player())))
                .andExpect(status().isOk())
                .andReturn();

        String responseContentPlayer = createResultPlayer.getResponse().getContentAsString();
        Player createdPlayer = objectMapper.readValue(responseContentPlayer, Player.class);

        MvcResult createResultScore = mockMvc.perform(post("/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("playerId", createdPlayer.getPlayerId().toString())
                        .param("value", Integer.toString(100))
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