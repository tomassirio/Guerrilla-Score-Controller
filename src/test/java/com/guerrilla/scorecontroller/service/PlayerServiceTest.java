package com.guerrilla.scorecontroller.service;

import com.guerrilla.scorecontroller.exception.PlayerNotFoundException;
import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.repository.dynamoDbImpl.PlayerDynamoDbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PlayerServiceTest {

    @Mock
    private PlayerDynamoDbRepository playerDynamoDbRepository;

    private PlayerService playerService;

    @BeforeEach
    public void setUp() {
        playerDynamoDbRepository = Mockito.mock(PlayerDynamoDbRepository.class);
        playerService = new PlayerService(playerDynamoDbRepository);
    }

    @Test
    public void testGetPlayerById() {
        UUID playerId = UUID.randomUUID();
        Player player = Player.builder()
                .playerId(playerId)
                .username("Mr Potato")
                .build();

        when(playerDynamoDbRepository.getPlayer(playerId)).thenReturn(Optional.of(player));

        Player retrievedPlayer = playerService.getPlayer(playerId);

        assertEquals(player, retrievedPlayer);
    }

    @Test
    public void testGetPlayerById_PlayerNotFoundException() {
        UUID playerId = UUID.randomUUID();

        when(playerDynamoDbRepository.getPlayer(playerId)).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class, () -> playerService.getPlayer(playerId));
    }

    @Test
    public void testGetPlayers() {
        UUID playerId = UUID.randomUUID();

        List<Player> players = List.of(
                Player.builder()
                        .playerId(playerId)
                        .username("Mr Potato")
                        .build(),
                Player.builder()
                        .playerId(playerId)
                        .username("Rex")
                        .build()
        );

        when(playerDynamoDbRepository.getPlayers()).thenReturn(players);

        List<Player> retrievedPlayers = playerService.getPlayers();

        assertEquals(players, retrievedPlayers);
    }

    @Test
    public void testCreatePlayer() {
        UUID playerId = UUID.randomUUID();
        String userName = "Mr Potato";
        Player createdPlayer = Player.builder()
                .playerId(playerId)
                .username(userName)
                .build();

        when(playerDynamoDbRepository.createPlayer(userName)).thenReturn(createdPlayer);

        Player newPlayer = playerService.createPlayer(userName);

        assertEquals(createdPlayer, newPlayer);
    }

    @Test
    public void testRenamePlayer() {
        UUID playerId = UUID.randomUUID();
        String newUserName = "Mr Potato";
        Player updatedPlayer = Player.builder()
                .playerId(playerId)
                .username(newUserName)
                .build();

        when(playerDynamoDbRepository.updatePlayer(playerId, newUserName)).thenReturn(Optional.of(updatedPlayer));

        Player renamedPlayer = playerService.renamePlayer(playerId, newUserName);

        assertEquals(updatedPlayer, renamedPlayer);
    }

    @Test
    public void testRenamePlayer_PlayerNotFoundException() {
        UUID playerId = UUID.randomUUID();
        String newUserName = "Mr Potato";

        when(playerDynamoDbRepository.updatePlayer(playerId, newUserName)).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class, () -> playerService.renamePlayer(playerId, newUserName));
    }

    @Test
    public void testDeletePlayer() {
        UUID playerId = UUID.randomUUID();

        assertDoesNotThrow(() -> playerService.deletePlayer(playerId));
    }
}