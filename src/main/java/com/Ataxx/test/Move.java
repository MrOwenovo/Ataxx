package com.Ataxx.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** Represents an Ataxx move. There is one Move object created for
 *  each distinct Move. */
class Move {

    /* Moves getContent generated often during the calculations of an AIPlayer,
     * so it's a good idea to make that operation efficient.  Instead of
     * relying on a Move constructor, which does a memory allocation with
     * each use of 'new', we use a "Move factory": a static method that
     * returns a Move, but not necessarily a new one. Moves themselves
     * are immutable, and for any possible move, there is exactly one
     * object of type Move. */

    /** The move COL0 ROW0 - COL1 ROW1.  This must be a legal move. */
    private Move(int col0, int row0, int col1, int row1) {
        this.col0 = (char) (col0 + 'a' - 2);
        this.row0 = (char) (row0 + '1' - 2);
        this.col1 = (char) (col1 + 'a' - 2);
        this.row1 = (char) (row1 + '1' - 2);
        fromIndex = row0 * DEEPER_ONESIDE + col0;
        toIndex = row1 * DEEPER_ONESIDE + col1;
        String location0 = String.valueOf(this.col0) + this.row0;
        String location1 = String.valueOf(this.col1) + this.row1;
        isClone = isClone(location0, location1);
        isJump = isJump(location0, location1);
    }

    /** A pass. */
    private Move() {
        col0 = col1 = row0 = row1 = 0;
        fromIndex = toIndex = -1;
        isJump = isClone = false;
    }

    /** A factory method that returns a Move from COL0 ROW0 to COL1 ROW1,
     *  assuming the column and row designations are valid. Returns null
     *  if no such move is ever possible because it is more than 2 squares
     *  in some direction.  The moves are on the extended board (i.e., they
     *  may go into the border layers). */
    static Move move(char col0, char row0, char col1, char row1) {
        return
            OVERALL_MOVES[col0 - 'a' + 2][row0 - '1' + 2]
            [col1 - 'a' + 2][row1 - '1' + 2];
    }

    /** Return the Move denoted by STR, or null if STR is not a syntactically
     *  valid move designation. */
    static Move move(String str) {
        Matcher mat = MOVE_ROUTE.matcher(str);
        if (!mat.matches()) {
            return null;
        } else if (mat.group(1) != null) {
            String moveStr = mat.group(1);
            return move(moveStr.charAt(0), moveStr.charAt(1),
                        moveStr.charAt(3), moveStr.charAt(4));
        } else {
            return pass();
        }
    }


	// Final Project Part A.1.3 Clone or Jump?

    /** 
	  * Decide whether the move is a "clone".
      * @return true if it is a clone move. 
	  */
    static boolean isClone(String location0, String location1) {
        // complete the code
        if (location1.charAt(0)==location0.charAt(0)&&location1.charAt(1)==location0.charAt(1)){
            return false;
        }
		if(location1.charAt(0)==location0.charAt(0) || location1.charAt(0)==location0.charAt(0)-1|| location1.charAt(0)==location0.charAt(0)+1){
            if(location1.charAt(1)==location0.charAt(1) || location1.charAt(1)==location0.charAt(1)-1|| location1.charAt(1)==location0.charAt(1)+1){
                return true;
            }
        }
		
        return false;
    }

    /** 
	  * Decide whether the move is a "jump".
      * @return true if it is a jump move. 
	  */
    static boolean isJump(String location0, String location1) {
        // complete the code
        if (location1.charAt(0)==location0.charAt(0)&&location1.charAt(1)==location0.charAt(1)){
            return false;
        }
        if(location1.charAt(0)==location0.charAt(0) || location1.charAt(0)==location0.charAt(0)-1|| location1.charAt(0)==location0.charAt(0)+1){
            if(location1.charAt(1)==location0.charAt(1) || location1.charAt(1)==location0.charAt(1)-1|| location1.charAt(1)==location0.charAt(1)+1){
                return false;
            }
        }
        if(location1.charAt(0)>location0.charAt(0)+2 || location1.charAt(0)<location0.charAt(0)-2 || location1.charAt(1)>location0.charAt(1)+2 || location1.charAt(1)<location0.charAt(1)-2){
            return false;
        }

        return true;
    }


    /** Returns a pass. */
    static Move pass() {
        return PASS;
    }

    /** Return true iff I am a pass. */
    boolean isPass() {
        return this == PASS;
    }

    /** Return true if this is an extension (move to adjacent square). */
    boolean isClone() {
        return isClone;
    }

    /** Return true if this is a jump (move to adjacent square). */
    boolean isJump() {
        return isJump;
    }

    /** Returns from column.  Undefined if a pass. */
    char col0() {
        return col0;
    }

    /** Returns from row.  Undefined if a pass. */
    char row0() {
        return row0;
    }

    /** Returns to column.  Undefined if a pass. */
    char col1() {
        return col1;
    }

    /** Returns to row.  Undefined if a pass. */
    char row1() {
        return row1;
    }

    /** Return the linearized index of my 'from' square,
     *  or -1 if I am a pass. */
    int fromIndex() {
        return fromIndex;
    }

    /** Return The linearized index of my 'to' square,
     *  or -1 if I am a pass. */
    int toIndex() {
        return toIndex;
    }

    @Override
    public String toString() {
        String message = "";
        if (isPass()) {
            message = "-";
        } else {
            message += col0();
            message += row0();
            message += '-';
            message += col1();
            message += row1();
        }
        return message;
    }

    /** Syntax of a move.  Groups capture row and column. */
    private static final Pattern MOVE_ROUTE =
        Pattern.compile("([a-z][1-9a-f]-[a-z][1-9a-f])|(-)");

    /** Size of a side of the board. */
    static final int ONESIDE = 7;

    /** Size of side of a board plus 2-deep boundary. */
    static final int DEEPER_ONESIDE = ONESIDE + 4;

    /** The pass. */
    static final Move PASS = new Move();

    /** Linearized indices. */
    private final int fromIndex, toIndex;

    /** Move characteristics, indicating whether move is clone or jump. */
    private boolean isClone, isJump;

    /** From and two squares, or 0s if a pass. */
    private char col0, row0, col1, row1;

    /** The set of all Moves other than pass, indexed by from and to column and
     *  row positions. */
    private static final Move[][][][] OVERALL_MOVES =
        new Move[DEEPER_ONESIDE][DEEPER_ONESIDE][DEEPER_ONESIDE][DEEPER_ONESIDE];

    static {
        for (int c = 2; c < ONESIDE + 2; c += 1) {
            for (int r = 2; r < ONESIDE + 2; r += 1) {
                for (int dc = -2; dc <= 2; dc += 1) {
                    for (int dr = -2; dr <= 2; dr += 1) {
                        if (dc != 0 || dr != 0) {
                            OVERALL_MOVES[c][r][c + dc][r + dr] =
                                new Move(c, r, c + dc, r + dr);
                        }
                    }
                }
            }
        }
    }
}
