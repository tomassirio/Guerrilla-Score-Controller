package com.guerrilla.scorecontroller.controller;

import com.guerrilla.scorecontroller.model.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/player")
@Slf4j
public class PlayerController {


    @GetMapping("/{id}")
    public Optional<Player> getPlayerById(@RequestParam(name = "id") Long id) {

        return Optional.empty();
    }

    @GetMapping("/")
    public List<Player> getPlayers(){
        return List.of();
    }

    @PostMapping("/")
    public Player createPlayer(@RequestParam(name = "userName") String userName) {
        log.info("Player Created");
        return new Player();
    }

    @PutMapping("/{id}")
    public Player changePlayerAlias(@RequestParam(name = "id") Long id, @RequestParam("userName") String userName) {

        log.info("Player Changed User Name");
        return new Player();
    }

    @DeleteMapping("/{id}")
    public void deletePlayer(@RequestParam(name = "id") Long id) {

        log.info("Player Deleted");
    }
}
