package com.guerrilla.scorecontroller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record ScoreDto(String playerId, Integer value) {
}


