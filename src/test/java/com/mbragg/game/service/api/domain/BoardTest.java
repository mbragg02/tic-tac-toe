package com.mbragg.game.service.api.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.springframework.util.Assert.*;

public class BoardTest {

    private static final int EXPECTED_BOARD_SIZE = 3;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testBoardInitialization() {
        Board board = new Board(EXPECTED_BOARD_SIZE);

        assertEquals(EXPECTED_BOARD_SIZE, board.getSize());
        assertEquals(EXPECTED_BOARD_SIZE, board.getRows().size());

        for (int i = 0; i < EXPECTED_BOARD_SIZE; i++) {
            BoardRow boardRow = board.getRows().get(i);

            assertEquals(EXPECTED_BOARD_SIZE, boardRow.getCharacters().size());
            for (int j = 0; j < EXPECTED_BOARD_SIZE; j++) {
                assertEquals('-', boardRow.getCharacters().get(j).charValue());
            }
        }
    }

    @Test
    public void testBoardInitializationSize() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The minimum board size is 3.");
        new Board(2);
    }

    @Test
    public void test2dArrayGeneration() {
        Board board = new Board(EXPECTED_BOARD_SIZE);
        Character[][] characters = board.to2dArray();

        assertEquals(EXPECTED_BOARD_SIZE, characters.length);

        for (int i = 0; i < EXPECTED_BOARD_SIZE; i++) {
            assertEquals(EXPECTED_BOARD_SIZE, characters[i].length);

            for (int j = 0; j < EXPECTED_BOARD_SIZE; j++) {
                assertEquals('-', characters[i][j].charValue());
            }
        }
    }

    @Test
    public void testAddMove() {
        Board board = new Board(EXPECTED_BOARD_SIZE);

        Character character = board.getRows().get(0).getCharacters().get(0);

        assertEquals('-', character.charValue());

        board.addMove(0, 0, Mark.CIRCLE);
        character = board.getRows().get(0).getCharacters().get(0);

        assertEquals(Mark.CIRCLE.getName(), character);
    }

    @Test
    public void testAddMoveToPreviouslyPopulatedPosition() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Row/Column position must be empty.");

        Board board = new Board(EXPECTED_BOARD_SIZE);
        board.addMove(0, 0, Mark.CIRCLE);
        assertEquals(Mark.CIRCLE.getName(), board.getRows().get(0).getCharacters().get(0));

        board.addMove(0, 0, Mark.CROSS);
    }

    @Test
    public void testAddMoveToInvalidColumn() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Column must be within the bound's of the board.");

        Board board = new Board(EXPECTED_BOARD_SIZE);
        board.addMove(0, 3, Mark.CIRCLE);
    }

    @Test
    public void testAddMoveToInvalidRow() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Row must be within the bound's of the board.");

        Board board = new Board(EXPECTED_BOARD_SIZE);
        board.addMove(3, 0, Mark.CIRCLE);
    }

    @Test
    public void testIsBoardFull() {
        Board board = new Board(EXPECTED_BOARD_SIZE);
        isTrue(!board.isBoardFull(), "Board is initially not full.");

        int totalPositions = EXPECTED_BOARD_SIZE * EXPECTED_BOARD_SIZE;
        int count = 0;

        for (int i = 0; i < EXPECTED_BOARD_SIZE; i++) {
            for (int j = 0; j < EXPECTED_BOARD_SIZE; j++) {
                board.addMove(i, j, Mark.CIRCLE);
                if (count < totalPositions - 1) {
                    isTrue(!board.isBoardFull(), "Board should not be full.");
                }
                count++;
            }
        }

        isTrue(board.isBoardFull(), "Board should be full.");
    }

    @Test
    public void testCheckRowForWin() {
        Board board = new Board(EXPECTED_BOARD_SIZE);

        for (int i = 0; i < EXPECTED_BOARD_SIZE; i++) {
            board.addMove(0, i , Mark.CIRCLE);
            if (i < EXPECTED_BOARD_SIZE - 1) {
                isTrue(!board.checkBoardForWin(), "Game should not be won.");
            }

        }
        isTrue(board.checkBoardForWin(), "Game should be won.");
    }

    @Test
    public void testCheckColumnForWin() {
        Board board = new Board(EXPECTED_BOARD_SIZE);

        for (int i = 0; i < EXPECTED_BOARD_SIZE; i++) {
            board.addMove(i, 0 , Mark.CIRCLE);
            if (i < EXPECTED_BOARD_SIZE - 1) {
                isTrue(!board.checkBoardForWin(), "Game should not be won.");
            }

        }
        isTrue(board.checkBoardForWin(), "Game should be won.");
    }

    @Test
    public void testCheckForwardDiagonalForWin() {
        Board board = new Board(EXPECTED_BOARD_SIZE);

        for (int i = 0; i < EXPECTED_BOARD_SIZE; i++) {
            board.addMove(i, i , Mark.CIRCLE);
            if (i < EXPECTED_BOARD_SIZE - 1) {
                isTrue(!board.checkBoardForWin(), "Game should not be won.");
            }
        }
        isTrue(board.checkBoardForWin(), "Game should be won.");
    }

    @Test
    public void testCheckBackwardDiagonalForWin() {
        Board board = new Board(EXPECTED_BOARD_SIZE);

        int counter = EXPECTED_BOARD_SIZE - 1;

        for (int i = 0; i < EXPECTED_BOARD_SIZE; i++) {
            board.addMove(counter, i , Mark.CIRCLE);
            if (i < EXPECTED_BOARD_SIZE - 1) {
                isTrue(!board.checkBoardForWin(), "Game should not be won.");
            }
            counter--;

        }
        isTrue(board.checkBoardForWin(), "Game should be won.");
    }
}
