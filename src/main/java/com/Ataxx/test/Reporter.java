package com.Ataxx.test;

/** An object that formats and sends messages and errors. */
interface Reporter {

    /** Display an announcement that one player with its state has won. EMPTY indicates a tie. */
    void announceWinner(PieceState state);

    /** Report move MOVE by PLAYER. */
    void announceMove(Move move, PieceState player);

    /** Display a message indicated by FORMAT and ARGS, which have
     *  the same meaning as in String.format. */
    void message(String format, Object... args);

    /** Report an error as specified by FORMAT and ARGS, which have
     *  the same meaning as in String.format. */
    void error(String format, Object... args);

}

