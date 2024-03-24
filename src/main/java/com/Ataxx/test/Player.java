package com.Ataxx.test;

/** A generic Ataxx Player. */
abstract class Player {

    /** A Player that will play myState in ataxxGame. */
    Player(Game ataxxGame, PieceState myState) {
        this.ataxxGame = ataxxGame;
        this.mySate = myState;
    }

    /** Return my pieces' color. */
    PieceState getMyState() {
        return mySate;
    }

    /** Return true iff I am automated. */
    boolean isAuto() {
        return false;
    }

    /** Return the AtaxxGame I am playing in. */
    Game getAtaxxGame() {
        return ataxxGame;
    }

    /** Return the board I am playing on. The caller should not modify this
     *  board. */
    Board getAtaxxBoard() {
        return ataxxGame.getAtaxxBoard();
    }

    /** Return a legal move or command for my side. Assumes that
     *  board.nextMove() == mySate() and that the getAtaxxGame is not over. */
    abstract String getAtaxxMove();

    /** The getAtaxxGame I am playing in. */
    private final Game ataxxGame;
    /** The state of my pieces. */
    private final PieceState mySate;
}
