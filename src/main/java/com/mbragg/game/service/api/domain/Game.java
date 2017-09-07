package com.mbragg.game.service.api.domain;

import javax.persistence.*;
import java.util.Objects;

/**
 * Represents a Game, composed of a Board and Players
 */
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Board board;

    @ManyToOne(cascade = CascadeType.ALL)
    private Player playerOne;

    @ManyToOne(cascade = CascadeType.ALL)
    private Player playerTwo;

    private GameStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(Player playerTwo) {
        this.playerTwo = playerTwo;
    }

    public GameStatus getStatus() {
        return status;
    }

    public boolean isInProgress() {
        return status != null && status.equals(GameStatus.IN_PROGRESS);
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Game game = (Game) o;
        return Objects.equals(id, game.id)
                && Objects.equals(board, game.board)
                && Objects.equals(playerOne, game.playerOne)
                && Objects.equals(playerTwo, game.playerTwo)
                && Objects.equals(status, game.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, board, playerOne, playerTwo, status);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", board=" + board +
                ", playerOne=" + playerOne +
                ", playerTwo=" + playerTwo +
                ", status=" + status +
                '}';
    }
}
