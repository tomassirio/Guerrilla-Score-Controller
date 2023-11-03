package com.guerrilla.scorecontroller.repository;

import com.guerrilla.scorecontroller.model.Player;

import java.util.List;
import java.util.Optional;

interface PlayerRepository {
    Player createPlayer(String username);
    Optional<Player> getPlayer(Long id);
    List<Player> getPlayers();
    Optional<Player> updatePlayer(Long id, String username);
    void deletePlayer(Long id);
}
