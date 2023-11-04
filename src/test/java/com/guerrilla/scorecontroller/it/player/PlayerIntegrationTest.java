package com.guerrilla.scorecontroller.it.player;

import com.guerrilla.scorecontroller.ScoreControllerApplication;
import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.repository.dynamoDbImpl.PlayerDynamoDbRepository;
import com.guerrilla.scorecontroller.repository.PlayerRepository;
import com.guerrilla.scorecontroller.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ScoreControllerApplication.class)
public class PlayerIntegrationTest {

    @Autowired
    private PlayerService playerService;

    @MockBean(PlayerDynamoDbRepository.class)
    private PlayerRepository playerRepository;

    @Test
    public void testGetAllPlayers() {
        Player player1 = new Player(1L, "Mr Potato");
        Player player2 = new Player(2L, "Rex");
        List<Player> mockPlayers = List.of(player1, player2);

        when(playerRepository.getPlayers()).thenReturn(mockPlayers);

        List<Player> players = playerService.getPlayers();

        assertEquals(2, players.size());
        assertEquals(player1, players.get(0));
        assertEquals(player2, players.get(1));
    }

    @Test
    public void testGetPlayerById() {
        Player player = new Player(1L, "Mr Potato");

        when(playerRepository.getPlayer(1L)).thenReturn(Optional.of(player));

        Player retrievedPlayer = playerService.getPlayer(1L);

        assertEquals(player, retrievedPlayer);
    }

    @Test
    public void testUpdatePlayerUsername() {
        Long playerId = 1L;
        String newUsername = "Updated Mr Potato";
        Player originalPlayer = new Player(playerId, "Mr Potato");

        when(playerRepository.getPlayer(playerId)).thenReturn(Optional.of(originalPlayer));
        when(playerRepository.updatePlayer(playerId, newUsername))
                .thenReturn(Optional.of(new Player(playerId, newUsername)));

        Player updatedPlayer = playerService.renamePlayer(playerId, newUsername);

        assertEquals(newUsername, updatedPlayer.getUsername());
    }

    @Test
    public void testCreatePlayer() {
        when(playerRepository.createPlayer("Mr Potato")).thenReturn(new Player(1L, "Mr Potato"));

        Player createdPlayer = playerService.createPlayer("Mr Potato");

        assertEquals("Mr Potato", createdPlayer.getUsername());
    }

    @Test
    public void testDeletePlayer() {
        when(playerRepository.getPlayer(1L)).thenReturn(Optional.of(new Player(1L, "Mr Potato")));

        playerService.deletePlayer(1L);

        Mockito.verify(playerRepository, Mockito.times(1)).deletePlayer(1L);
    }

}