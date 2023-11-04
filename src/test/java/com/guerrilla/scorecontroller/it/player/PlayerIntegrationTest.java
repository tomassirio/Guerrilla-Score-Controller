package com.guerrilla.scorecontroller.it.player;

import com.guerrilla.scorecontroller.ScoreControllerApplication;
import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.repository.PlayerRepository;
import com.guerrilla.scorecontroller.repository.dynamoDbImpl.PlayerDynamoDbRepository;
import com.guerrilla.scorecontroller.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        Player player1 = Player.builder()
                .playerId(UUID.randomUUID())
                .username("Mr Potato")
                .build();
        Player player2 = Player.builder()
                .playerId(UUID.randomUUID())
                .username("Rex")
                .build();
        List<Player> mockPlayers = List.of(player1, player2);

        when(playerRepository.getPlayers()).thenReturn(mockPlayers);

        List<Player> players = playerService.getPlayers();

        assertEquals(2, players.size());
        assertEquals(player1, players.get(0));
        assertEquals(player2, players.get(1));
    }

    @Test
    public void testGetPlayerById() {
        Player player = Player.builder()
                .playerId(UUID.randomUUID())
                .username("Mr Potato")
                .build();

        when(playerRepository.getPlayer(player.getPlayerId())).thenReturn(Optional.of(player));

        Player retrievedPlayer = playerService.getPlayer(player.getPlayerId());

        assertEquals(player, retrievedPlayer);
    }

    @Test
    public void testUpdatePlayerUsername() {
        UUID playerId = UUID.randomUUID();
        String newUsername = "Updated Mr Potato";
        Player originalPlayer = Player.builder()
                .playerId(playerId)
                .username("Mr Potato")
                .build();

        when(playerRepository.getPlayer(playerId)).thenReturn(Optional.of(originalPlayer));
        when(playerRepository.updatePlayer(playerId, newUsername))
                .thenReturn(Optional.of(Player.builder()
                        .playerId(playerId)
                        .username(newUsername)
                        .build()));

        Player updatedPlayer = playerService.renamePlayer(playerId, newUsername);

        assertEquals(newUsername, updatedPlayer.getUsername());
    }

    @Test
    public void testCreatePlayer() {
        UUID playerId = UUID.randomUUID();

        when(playerRepository.createPlayer("Mr Potato")).thenReturn(Player.builder()
                .username("Mr Potato")
                .playerId(playerId)
                .build());

        Player createdPlayer = playerService.createPlayer("Mr Potato");

        assertEquals("Mr Potato", createdPlayer.getUsername());
        assertEquals(playerId, createdPlayer.getPlayerId());
    }

    @Test
    public void testDeletePlayer() {
        UUID playerId = UUID.randomUUID();

        when(playerRepository.getPlayer(playerId))
                .thenReturn(
                        Optional.of(
                                Player.builder()
                                        .username("Mr Potato")
                                        .playerId(playerId)
                                        .build()));

        playerService.deletePlayer(playerId);

        Mockito.verify(playerRepository, Mockito.times(1)).deletePlayer(playerId);
    }

}