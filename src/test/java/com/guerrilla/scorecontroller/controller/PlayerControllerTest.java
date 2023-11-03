package com.guerrilla.scorecontroller.controller;

import com.guerrilla.scorecontroller.exception.PlayerNotFoundException;
import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PlayerControllerTest {

    @Mock
    private PlayerService playerService;

    private PlayerController playerController;

    @BeforeEach
    public void setUp() {
        playerService = Mockito.mock(PlayerService.class);
        playerController = new PlayerController(playerService);
    }

    @Test
    public void testGetPlayerById() {
        Long playerId = 1L;

        Player player = Player.builder()
                .playerId(playerId)
                .username("Mr Potato")
                .build();

        when(playerService.getPlayer(playerId)).thenReturn(player);

        ResponseEntity<Player> response = playerController.getPlayerById(playerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(player, response.getBody());
    }

    @Test
    public void testGetPlayers() {
        List<Player> players = List.of(
                Player.builder()
                        .playerId(1L)
                        .username("Mr Potato")
                        .build(),
                Player.builder()
                        .playerId(2L)
                        .username("Rex")
                        .build()
        );

        when(playerService.getPlayers()).thenReturn(players);

        ResponseEntity<List<Player>> response = playerController.getPlayers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(players, response.getBody());
    }

    @Test
    public void testCreatePlayer() {
        String userName = "Mr Potato";
        Player createdPlayer = Player.builder()
                .playerId(1L)
                .username(userName)
                .build();

        when(playerService.createPlayer(userName)).thenReturn(createdPlayer);

        ResponseEntity<Player> response = playerController.createPlayer(userName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createdPlayer, response.getBody());
    }

    @Test
    public void testChangePlayerAlias() {
        Long playerId = 1L;
        String newUserName = "Mr Potato";
        Player updatedPlayer = Player.builder()
                .playerId(playerId)
                .username(newUserName)
                .build();

        when(playerService.renamePlayer(playerId, newUserName)).thenReturn(updatedPlayer);

        ResponseEntity<Player> response = playerController.changePlayerAlias(playerId, newUserName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedPlayer, response.getBody());
    }

    @Test
    public void testDeletePlayer() {
        Long playerId = 1L;
        Player player = Player.builder()
                .playerId(playerId)
                .username("Mr Potato")
                .build();

        when(playerService.getPlayer(playerId)).thenReturn(player);

        playerController.deletePlayer(playerId);

        Mockito.verify(playerService, Mockito.times(1)).deletePlayer(playerId);
    }

    @Test
    public void testHandlePlayerNotFoundException() {
        PlayerNotFoundException exception = new PlayerNotFoundException(1L);

        ResponseEntity<String> response = playerController.handlePlayerNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetPlayerById_PlayerNotFoundException() {
        Long playerId = 1L;

        when(playerService.getPlayer(playerId)).thenThrow(new PlayerNotFoundException(playerId));

        assertThrows(PlayerNotFoundException.class, () -> playerController.getPlayerById(playerId));
    }

    @Test
    public void testChangePlayerAlias_PlayerNotFoundException() {
        Long playerId = 1L;
        String newUserName = "Mr Potato";

        when(playerService.renamePlayer(playerId, newUserName)).thenThrow(new PlayerNotFoundException(playerId));

        assertThrows(PlayerNotFoundException.class, () -> playerController.changePlayerAlias(playerId, newUserName));
    }

    @Test
    public void testDeletePlayer_PlayerNotFoundException() {
        Long playerId = 1L;

        when(playerService.getPlayer(playerId)).thenThrow(new PlayerNotFoundException(playerId));

        assertThrows(PlayerNotFoundException.class, () -> playerController.deletePlayer(playerId));
    }
}