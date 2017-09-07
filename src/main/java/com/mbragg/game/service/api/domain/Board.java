package com.mbragg.game.service.api.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.springframework.util.Assert.isTrue;

/**
 * Represents a Board to be used when playing a Game.
 */
@Entity
public class Board {

    private static Logger logger = LoggerFactory.getLogger(Board.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<BoardRow> rows = new ArrayList<>();

    private int size;

    public Board() {
        // Required no-args constructor
    }

    public Board(int size) {
        isTrue(size >= 3, "The minimum board size is 3.");
        this.size = size;
        initiate();
    }

    /**
     * Generates a two-dimensional array representation of the game board.
     *
     * @return A Character[][] representation of the game board.
     */
    public Character[][] to2dArray() {
        return this.getRows().stream()
                .map(BoardRow::getCharacters)
                .map(row -> row.toArray(new Character[0]))
                .toArray(Character[][]::new);
    }

    /**
     * Performing a 'Move' on the board. Updates a given row/col coordinate with the given 'Mark'
     *
     * @param row    The row number where to add the mark to.
     * @param column The column number where to add the mark to.
     * @param mark   The Mark to place at the specific row/col coordinate.
     */
    public void addMove(int row, int column, Mark mark) {
        isTrue(row >= 0 && row < size, "Row must be within the bound's of the board.");
        isTrue(column >= 0 && column < size, "Column must be within the bound's of the board.");
        isTrue(boardPositionIsEmpty(getCharacterAt(row, column)), "Row/Column position must be empty.");

        logger.debug("Adding mark {} to row: {} / column: {}", mark, row, column);

        rows.get(row).getCharacters().set(column, mark.getName());
    }

    /**
     * Checks whether the board is 'full', i.e The are no empty row/col coordinate where a new mark could be placed.
     *
     * @return boolean representing if the board is full (true) or not (false).
     */
    public boolean isBoardFull() {
        boolean boardFull = true;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (Mark.INITIAL.getName().equals(getCharacterAt(i, j))) {
                    boardFull = false;
                }
            }
        }

        logger.debug("Is board full: {}", boardFull);

        return boardFull;
    }

    /**
     * Checks the board if there is a 'win', i.e. All of the marks in a row, col or diagonal are of the same type.
     *
     * @return boolean representing if a win is found (true) or not (false).
     */
    public boolean checkBoardForWin() {
        return (checkColumnsForWin() || checkRowsForWin() || checkDiagonalsForWin());
    }

    private void addRow(BoardRow boardRow) {
        this.rows.add(boardRow);
    }

    private void initiate() {

        for (int i = 0; i < size; i++) {
            BoardRow boardRow = new BoardRow();
            List<Character> characters = new ArrayList<>();

            for (int j = 0; j < size; j++) {
                characters.add(Mark.INITIAL.getName());
            }

            boardRow.setCharacters(characters);
            this.addRow(boardRow);
        }

        logger.debug("Board of size: {} initialized with: '{}'", size, Mark.INITIAL.getName());
    }

    private static boolean boardPositionIsEmpty(Character character) {
        return character.equals(Mark.INITIAL.getName());
    }

    private Character getCharacterAt(int row, int column) {
        return rows.get(row).getCharacters().get(column);
    }

    private boolean checkRowsForWin() {
        return rows.stream()
                .map(row -> checkCharactersForWin(row.getCharacters().toArray(new Character[0])))
                .anyMatch(rowCheckResult -> rowCheckResult.equals(true));
    }

    private boolean checkColumnsForWin() {
        Character[] characters = new Character[size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                characters[j] = getCharacterAt(j, i);
            }

            if (checkCharactersForWin(characters)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkDiagonalsForWin() {
        Character[] characters = new Character[size];

        for (int i = 0; i < size; i++) {
            characters[i] = getCharacterAt(i, i);
        }

        if (checkCharactersForWin(characters)) {
            return true;
        }

        int y = size - 1;
        for (int i = 0; i < size; i++) {
            characters[i] = getCharacterAt(i, y);
            y--;
        }

        return checkCharactersForWin(characters);
    }

    private boolean checkCharactersForWin(Character[] characters) {
        isTrue(characters.length > 0, "Character sequence length must be greater than zero.");
        Character firstCharacter = characters[0];

        return !Mark.INITIAL.getName().equals(firstCharacter) && Stream.of(characters).allMatch(firstCharacter::equals);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }


    public List<BoardRow> getRows() {
        return rows;
    }

    public void setRows(List<BoardRow> rows) {
        this.rows = rows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Board board = (Board) o;
        return Objects.equals(id, board.id)
                && Objects.equals(rows, board.rows)
                && Objects.equals(size, board.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rows, size);
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", rows=" + rows +
                ", size=" + size +
                '}';
    }
}
