package com.guerrilla.scorecontroller.service;

import com.guerrilla.scorecontroller.model.Player;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    public Optional<Player> getPlayerById(Long id) {
        return Optional.empty();
    }

    public List<Player> getPlayers() {
        return List.of();
    }

    public Player createPlayer(String userName) {
        return new Player();
    }
    public Player renamePlayer(Long id, String userName) {

        return new Player();
    }

    public void deletePlayer(Long id) {

    }

}
