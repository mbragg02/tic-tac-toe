package com.mbragg.game.service.api.dto;

import com.mbragg.game.service.api.domain.GameStatus;

import java.util.List;

/**
 * Summary of a Game
 */
public class GameSummary {

    private Long gameId;

    private List<Long> userIds;

    private GameStatus gameStatus;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }
}
