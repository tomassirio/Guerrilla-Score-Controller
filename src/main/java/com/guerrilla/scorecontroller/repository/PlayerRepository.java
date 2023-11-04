package com.guerrilla.scorecontroller.repository;

import com.guerrilla.scorecontroller.model.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository {
    Player createPlayer(String username);
    Optional<Player> getPlayer(UUID id);
    List<Player> getPlayers();
    Optional<Player> updatePlayer(UUID id, String username);
    void deletePlayer(UUID id);
}
