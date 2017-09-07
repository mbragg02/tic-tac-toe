package com.mbragg.game.service.api.dto;

import com.mbragg.game.service.api.domain.GameStatus;
import com.mbragg.game.service.api.domain.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Response data for representing a Game and Players
 */
public class GameResponse {

    private Long gameId;

    private Character[][] board;

    private List<Player> players = new ArrayList<>();

    private GameStatus gameStatus;

    public GameResponse() {
    }

    private GameResponse(Builder builder) {
        setGameId(builder.gameId);
        setBoard(builder.board);
        setPlayers(builder.players);
        setGameStatus(builder.gameStatus);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Character[][] getBoard() {
        return board;
    }

    public void setBoard(Character[][] board) {
        this.board = board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }


    public static final class Builder {
        private Long gameId;
        private Character[][] board;
        private List<Player> players;
        private GameStatus gameStatus;

        private Builder() {
        }

        public Builder withGameId(Long val) {
            gameId = val;
            return this;
        }

        public Builder withBoard(Character[][] val) {
            board = val;
            return this;
        }

        public Builder withPlayers(List<Player> val) {
            players = val;
            return this;
        }

        public Builder withGameStatus(GameStatus val) {
            gameStatus = val;
            return this;
        }

        public GameResponse build() {
            return new GameResponse(this);
        }
    }
}
