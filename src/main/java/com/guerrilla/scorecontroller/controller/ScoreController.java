package com.guerrilla.scorecontroller.controller;

import com.guerrilla.scorecontroller.exception.ScoreNotFoundException;
import com.guerrilla.scorecontroller.model.Score;
import com.guerrilla.scorecontroller.service.ScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/score")
public class ScoreController {
    private final ScoreService scoreService;

    @Autowired
    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping
    public ResponseEntity<Score> createScore(@RequestParam String playerId, @RequestParam Integer value) {
        Score score = scoreService.createScore(UUID.fromString(playerId), value);

        log.info("Score Created: " + score.getScoreId() + "; PlayerId: " + score.getPlayerId() + "; Value: " + score.getValue());
        return ResponseEntity.ok(score);
    }

    @GetMapping
    public ResponseEntity<Score> getScoreById(@RequestParam String scoreId) {
        Score score = scoreService.getScore(UUID.fromString(scoreId));

        return ResponseEntity.ok(score);
    }

    @GetMapping("/player")
    public ResponseEntity<List<Score>> getScoresByPlayer(@RequestParam String playerId) {
        List<Score> scores = scoreService.getScoresByPlayer(UUID.fromString(playerId));

        return ResponseEntity.ok(scores);
    }

    @GetMapping("/player/highest")
    public ResponseEntity<Score> getHighestScore(@RequestParam String playerId) {
        Score score = scoreService.getHighestScore(UUID.fromString(playerId));

        return ResponseEntity.ok(score);
    }

    @PutMapping()
    public ResponseEntity<Score> updateScore(@RequestParam String scoreId, @RequestParam Integer value) {
        Score score = scoreService.updateScore(UUID.fromString(scoreId), value);

        log.info("Score Updated: " + score.getScoreId() + "; PlayerId: " + score.getPlayerId() + "; New Value: " + score.getValue());
        return ResponseEntity.ok(score);
    }

    @DeleteMapping
    public void deleteScore(@RequestParam String scoreId) {
        Score score = scoreService.getScore(UUID.fromString(scoreId));
        log.info("Score Deleted: " + score.getScoreId());
        scoreService.deleteScore(score.getScoreId());
    }

    @ExceptionHandler(ScoreNotFoundException.class)
    public ResponseEntity<String> handleScoreNotFoundException(ScoreNotFoundException exception) {
        log.error(exception.getLocalizedMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error(exception.getLocalizedMessage());
        return ResponseEntity.badRequest().build();
    }
}
