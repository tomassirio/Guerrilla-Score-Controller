package com.guerrilla.scorecontroller.controller;

import com.guerrilla.scorecontroller.exception.PlayerNotFoundException;
import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/player")
@Slf4j
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable(name = "id") Long id) {
        Player player = playerService.getPlayer(id);

        return ResponseEntity.ok(player);
    }

    @GetMapping("/")
    public ResponseEntity<List<Player>> getPlayers(){
        List<Player> players = playerService.getPlayers();

        return ResponseEntity.ok(players);
    }

    @PostMapping("/")
    public ResponseEntity<Player> createPlayer(@RequestParam(name = "userName") String userName) {
        Player player = playerService.createPlayer(userName);

        log.info("Player Created: "+ player.getPlayerId() + "; " + player.getUsername());
        return ResponseEntity.ok(player);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Player> changePlayerAlias(@PathVariable(name = "id") Long id, @RequestParam("userName") String userName) {
        Player player = playerService.renamePlayer(id, userName);

        log.info("Player Changed User Name: "+ player.getPlayerId() + "; " + player.getUsername());
        return ResponseEntity.ok(player);
    }

    @DeleteMapping("/{id}")
    public void deletePlayer(@PathVariable(name = "id") Long id) {
        Player player = playerService.getPlayer(id);
        log.info("Player Deleted: "+ player.getPlayerId() + "; " + player.getUsername());
        playerService.deletePlayer(player.getPlayerId());
    }

    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<String> handlePlayerNotFoundException(PlayerNotFoundException exception) {
        log.error(exception.getLocalizedMessage());
        return ResponseEntity.notFound().build();
    }
}
