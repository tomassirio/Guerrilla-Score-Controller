package com.guerrilla.scorecontroller.listener;

import com.guerrilla.scorecontroller.dto.ScoreDto;

public interface ScoreListener {
    void consume(ScoreDto scoreDto);
}
