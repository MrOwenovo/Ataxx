package com.Ataxx.test;

/** A Player that receives its moves from its Ataxx Game's getMoveCmnd method. */
class Manual extends Player {

    /** A Player that will play myState on ataxxGame, taking its moves from ataxxGame. */
    Manual(Game ataxxGame, PieceState myState) {
        super(ataxxGame, myState);
        prompt = myState + "> ";
    }

    @Override
    String getAtaxxMove() {
        Game ataxxGame = getAtaxxGame();
        while (true) {
            return ataxxGame.getCommand(prompt);
        }
    }

    /** The User serving as a source of input commands. */
    private String prompt;
}

