package com.guerrilla.scorecontroller.exception;

public class ScoreNotFoundException extends RuntimeException{
    public ScoreNotFoundException(String scoreId) {
        super("Score not Found: " + scoreId);
    }
}
