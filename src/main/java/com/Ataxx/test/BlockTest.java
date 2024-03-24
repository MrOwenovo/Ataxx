package com.Ataxx.test;

import org.junit.Test;

import static com.Ataxx.test.PieceState.*;
import static org.junit.Assert.assertEquals;

public class BlockTest {
    private static final char[] COLS = {'a', 'b', 'c', 'd', 'e', 'f', 'g'};
    private static final char[] ROWS = {'1', '2', '3', '4', '5', '6', '7'};


    void checkBoard(Board b, PieceState[][] expectedColors) {
        assertEquals(Board.ONESIDE, expectedColors.length);
        assertEquals(Board.ONESIDE, expectedColors[0].length);
        for (int r = 0; r < expectedColors.length; r++) {
            for (int c = 0; c < expectedColors[0].length; c++) {
                assertEquals("incorrect color at "
                                + COLS[c] + ROWS[ROWS.length - 1 - r],
                        expectedColors[r][c],
                        b.getContent(COLS[c], ROWS[ROWS.length - 1 - r]));
            }
        }
    }




    @Test
    public void testSettingBlocks() {
        Board b = new Board();
        b.setBlock('a', '2');
        final PieceState[][] BLOCK = {
                {RED, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, BLUE},
                {BLOCKED, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, BLOCKED},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {BLOCKED, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, BLOCKED},
                {BLUE, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, RED}
        };
        checkBoard(b, BLOCK);
    }


    @Test
    public void testSettingBlocks1() {
        Board b1 = new Board();
        b1.setBlock('c','3');
        b1.setBlock('d','6');
        final PieceState[][] BLOCK1 = {
                {RED, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, BLUE},
                {EMPTY, EMPTY, EMPTY, BLOCKED, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, BLOCKED, EMPTY, BLOCKED, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, BLOCKED, EMPTY, BLOCKED, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, BLOCKED, EMPTY, EMPTY, EMPTY},
                {BLUE, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, RED}
        };
        checkBoard(b1,BLOCK1);
    }

}
