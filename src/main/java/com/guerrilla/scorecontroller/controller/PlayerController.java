package com.guerrilla.scorecontroller.controller;

import com.guerrilla.scorecontroller.exception.PlayerNotFoundException;
import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/player")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }


    @GetMapping
    public ResponseEntity<Player> getPlayerById(@RequestParam String playerId) {
        Player player = playerService.getPlayer(UUID.fromString(playerId));

        return ResponseEntity.ok(player);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Player>> getPlayers() {
        List<Player> players = playerService.getPlayers();

        return ResponseEntity.ok(players);
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestParam(name = "username") String userName) {
        Player player = playerService.createPlayer(userName);

        log.info("Player Created: " + player.getPlayerId() + "; " + player.getUsername());
        return ResponseEntity.ok(player);
    }

    @PutMapping
    public ResponseEntity<Player> changePlayerUsername(@RequestParam(name = "playerId") String playerId, @RequestParam("userName") String userName) {
        Player player = playerService.renamePlayer(UUID.fromString(playerId), userName);

        log.info("Player Changed User Name: " + player.getPlayerId() + "; " + player.getUsername());
        return ResponseEntity.ok(player);
    }

    @DeleteMapping
    public void deletePlayer(@RequestParam(name = "playerId") String playerId) {
        playerService.deletePlayer(UUID.fromString(playerId));
    }

    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<String> handlePlayerNotFoundException(PlayerNotFoundException exception) {
        log.error(exception.getLocalizedMessage());
        return ResponseEntity.notFound().build();
    }
}
