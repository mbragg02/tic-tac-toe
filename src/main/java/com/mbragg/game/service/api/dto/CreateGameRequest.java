package com.mbragg.game.service.api.dto;

import java.util.List;

/**
 * Request data for creating a new Game.
 */
public class CreateGameRequest {

    private List<Long> userIds;

    private int boardSize;

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }
}
