package com.guerrilla.scorecontroller.service;

import com.guerrilla.scorecontroller.exception.PlayerNotFoundException;
import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player getPlayer(Long id) {
        Optional<Player> player = playerRepository.getPlayer(id);

        if (player.isPresent()) {
            return player.get();
        } else {
            throw new PlayerNotFoundException(id);
        }
    }

    public List<Player> getPlayers() {
        return playerRepository.getPlayers();
    }

    public Player createPlayer(String userName) {
        return playerRepository.createPlayer(userName);
    }
    public Player renamePlayer(Long id, String userName) {
        Optional<Player> player = playerRepository.updatePlayer(id, userName);

        if (player.isPresent()) {
            return player.get();
        } else {
            throw new PlayerNotFoundException(id);
        }
    }

    public void deletePlayer(Long id) {
        playerRepository.deletePlayer(id);
    }

}
