package com.Ataxx.test;


import static com.Ataxx.test.GameException.error;

/** Describes the classes of Piece on an Ataxx board. */
enum PieceState {

    /** EMPTY: no piece.
     *  BLOCKED: square contains a block.
     *  RED, BLUE: piece colors. */
    EMPTY, BLOCKED,
    RED {
        @Override
        PieceState opposite() {
            return BLUE;
        }

        @Override
        boolean isPiece() {
            return true;
        }
    },
    BLUE {
        @Override
        PieceState opposite() {
            return RED;
        }

        @Override
        boolean isPiece() {
            return true;
        }
    };

    /** Return the piece color of my opponent, if defined. */
    PieceState opposite() {
        throw new UnsupportedOperationException();
    }

    /** Return true iff I denote a piece rather than an empty square or
     *  block. */
    boolean isPiece() {
        return false;
    }

    @Override
    public String toString() {
        return capitalize(super.toString().toLowerCase());
    }

    /** Return WORD with first letter capitalized. */
    static String capitalize(String word) {
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    /** Return the PieceState denoted by COLOR. */
    static PieceState colorParse(String color) {
        switch (color.toLowerCase()) {
        case "red": case "r":
            return RED;
        case "blue": case "b":
            return BLUE;
        default:
            throw error("invalid piece color: %s", color);
        }
    }


}
