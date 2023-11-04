package com.guerrilla.scorecontroller.exception;

public class PlayerNotFoundException extends RuntimeException{
    public PlayerNotFoundException(String id){
        super("Player not found with playerId: " + id);
    }
}
