package com.guerrilla.scorecontroller.model;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Player {
    private Long playerId;
    private String username;
}