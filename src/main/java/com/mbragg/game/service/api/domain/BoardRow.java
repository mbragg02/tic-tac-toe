package com.mbragg.game.service.api.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * Represents a row of characters, that makes up a full board.
 */
@Entity
public class BoardRow {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ElementCollection
    private List<Character> characters;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BoardRow boardRow = (BoardRow) o;
        return Objects.equals(id, boardRow.id)
                && Objects.equals(characters, boardRow.characters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, characters);
    }

    @Override
    public String toString() {
        return "BoardRow{" +
                "id=" + id +
                ", characters=" + characters +
                '}';
    }
}
