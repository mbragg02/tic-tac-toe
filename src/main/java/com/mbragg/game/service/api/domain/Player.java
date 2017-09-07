package com.mbragg.game.service.api.domain;

import javax.persistence.*;
import java.util.Objects;

/**
 * A Player represents a User that is associated with a particular Game. i.e. A single User can be playing multiple Games
 * at the same time as different Players.
 */
@Entity
public class Player {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private User user;

    private Mark mark;

    private PlayerStatus status;

    private boolean turn;

    public Player() {
        // Required no-args constructor
    }

    public Player(User user, Mark mark) {
        this.user = user;
        this.mark = mark;
    }

    public Player(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public boolean isTurn() {
        return turn;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return Objects.equals(id, player.id)
                && Objects.equals(user, player.user)
                && Objects.equals(mark, player.mark)
                && Objects.equals(status, player.status)
                && Objects.equals(turn, player.turn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, mark, status, turn);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", user=" + user +
                ", mark=" + mark +
                ", status=" + status +
                ", turn=" + turn +
                '}';
    }
}
