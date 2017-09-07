package com.mbragg.game.service.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of Game summaries
 */
public class GamesSummaryResponse {

    private List<GameSummary> gameSummaries = new ArrayList<>();

    public List<GameSummary> getGameSummaries() {
        return gameSummaries;
    }

    public void addGameSummary(GameSummary gameSummary) {
        this.gameSummaries.add(gameSummary);
    }

    public void setGameSummaries(List<GameSummary> gameSummaries) {
        this.gameSummaries = gameSummaries;
    }
}
