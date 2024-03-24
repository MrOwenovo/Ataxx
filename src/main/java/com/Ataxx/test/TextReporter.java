package com.Ataxx.test;


import static com.Ataxx.test.PieceState.EMPTY;

/** An object that formats and sends messages and errors by printing them. */
class TextReporter implements Reporter {

    @Override
    public void announceWinner(PieceState state) {
        if (state == EMPTY) {
            message("* Draw!");
        } else {
            message("* %s wins!", state.toString());
        }
    }

    @Override
    public void announceMove(Move move, PieceState player) {
        message("* %s moves %s.", player, move);
    }

    @Override
    public void message(String format, Object... args) {
        System.out.printf(format, args);
        System.out.println();
    }

    @Override
    public void error(String format, Object... args) {
        System.err.printf(format, args);
        System.err.println();
    }

}
