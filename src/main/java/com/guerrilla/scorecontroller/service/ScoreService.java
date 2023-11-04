package com.guerrilla.scorecontroller.service;

import com.guerrilla.scorecontroller.exception.ScoreNotFoundException;
import com.guerrilla.scorecontroller.model.Score;
import com.guerrilla.scorecontroller.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScoreService {
    private final ScoreRepository scoreRepository;

    @Autowired
    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public Score createScore(UUID playerId, Integer value) {
        return scoreRepository.createScore(playerId, value);
    }

    public Score getScore(UUID scoreId) {
        Optional<Score> score = scoreRepository.getScore(scoreId);

        if (score.isPresent()) {
            return score.get();
        } else {
            throw new ScoreNotFoundException(scoreId.toString());
        }
    }

    public List<Score> getScoresByPlayer(UUID playerId) {
        return scoreRepository.getScoresByPlayer(playerId);
    }

    public Score updateScore(UUID scoreId, Integer value) {
        Optional<Score> updatedScore = scoreRepository.updateScore(scoreId, value);

        if (updatedScore.isPresent()) {
            return updatedScore.get();
        } else {
            throw new ScoreNotFoundException(scoreId.toString());
        }
    }

    public void deleteScore(UUID scoreId) {
        scoreRepository.deleteScore(scoreId);
    }
}