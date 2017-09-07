package com.mbragg.game.service.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of Game summaries
 */
public class GamesSummaryResponse {

    private List<GameSummary> games = new ArrayList<>();

    public List<GameSummary> getGames() {
        return games;
    }

    public void addGameSummary(GameSummary gameSummary) {
        this.games.add(gameSummary);
    }

    public void setGames(List<GameSummary> games) {
        this.games = games;
    }
}
