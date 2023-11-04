package com.guerrilla.scorecontroller.it.score;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guerrilla.scorecontroller.it.config.AwsConfig;
import com.guerrilla.scorecontroller.it.config.TestcontainersConfig;
import com.guerrilla.scorecontroller.it.score.config.ScoreDbConfig;
import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.model.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestcontainersConfig.class, AwsConfig.class, ScoreDbConfig.class})
public class ScoreIntegrationTest {
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateAndGetScore() throws Exception {
        int value = 69;

        MvcResult createResultPlayer = mockMvc.perform(post("/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userName", "userName")
                        .content(objectMapper.writeValueAsString(new Player())))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = createResultPlayer.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        Player createdPlayer = objectMapper.readValue(responseContent, Player.class);

        MvcResult createResultScore = mockMvc.perform(post("/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("playerId", createdPlayer.getPlayerId().toString())
                        .param("value", Integer.toString(value))
                        .content(objectMapper.writeValueAsString(new Score())))
                .andExpect(status().isOk())
                .andReturn();

//        String createResponse = createResult.getResponse().getContentAsString();
//        Score createdScore = objectMapper.readValue(createResponse, Score.class);
//
//        System.out.println("Id Created: " + createdScore.getScoreId());
//
//        MvcResult getResult = mockMvc.perform(get("/score/{id}", createdScore.getScoreId()))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String getResponse = getResult.getResponse().getContentAsString();
//        Score retrievedScore = objectMapper.readValue(getResponse, Score.class);
//
//        assertNotNull(retrievedScore);
//        assertEquals(playerId, retrievedScore.getPlayerId());
//        assertEquals(value, retrievedScore.getValue());

    }

}