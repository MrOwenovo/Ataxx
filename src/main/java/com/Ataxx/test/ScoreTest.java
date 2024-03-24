package com.Ataxx.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScoreTest {

    private static void createMoves(Board b, String[] moves) {
        for (String s : moves) {
            b.createMove(s.charAt(0), s.charAt(1),
                    s.charAt(3), s.charAt(4));
        }
    }


    @Test
    public void testEmptyScore() {
        Board b = new Board();
        assertEquals("2 red vs 2 blue", b.getScore());
    }

    @Test
    public void testSimpleMoveScore() {
        Board b1 = new Board();
        b1.createMove("a7-a6");
        assertEquals("3 red vs 2 blue", b1.getScore());
    }

    @Test
    public void testMoreMoveScore() {
        Board b2 = new Board();
        String[] moves2 = {
                "a7-a6", "a1-c2",
                "g1-f1", "g7-e6"
        };
        createMoves(b2, moves2);
        assertEquals("4 red vs 2 blue", b2.getScore());
    }

}
