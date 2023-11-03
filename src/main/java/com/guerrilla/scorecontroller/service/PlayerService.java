package com.guerrilla.scorecontroller.service;

import com.guerrilla.scorecontroller.exception.PlayerNotFoundException;
import com.guerrilla.scorecontroller.model.Player;
import com.guerrilla.scorecontroller.repository.PlayerDynamoDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerDynamoDbRepository playerDynamoDbRepository;
    @Autowired
    public PlayerService(PlayerDynamoDbRepository playerDynamoDbRepository) {
        this.playerDynamoDbRepository = playerDynamoDbRepository;
    }

    public Player getPlayer(Long id) {
        Optional<Player> player = playerDynamoDbRepository.getPlayer(id);

        if (player.isPresent()) {
            return player.get();
        } else {
            throw new PlayerNotFoundException(id);
        }
    }

    public List<Player> getPlayers() {
        return playerDynamoDbRepository.getPlayers();
    }

    public Player createPlayer(String userName) {
        return playerDynamoDbRepository.createPlayer(userName);
    }
    public Player renamePlayer(Long id, String userName) {
        Optional<Player> player = playerDynamoDbRepository.updatePlayer(id, userName);

        if (player.isPresent()) {
            return player.get();
        } else {
            throw new PlayerNotFoundException(id);
        }
    }

    public void deletePlayer(Long id) {
        playerDynamoDbRepository.deletePlayer(id);
    }

}
