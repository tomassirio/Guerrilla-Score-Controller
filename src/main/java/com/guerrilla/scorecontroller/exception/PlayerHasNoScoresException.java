package com.guerrilla.scorecontroller.exception;

public class PlayerHasNoScoresException extends RuntimeException{
    public PlayerHasNoScoresException(String playerId) {
        super("Player has no Scores yet: " + playerId);
    }
}
