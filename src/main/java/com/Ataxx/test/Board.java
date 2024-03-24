package com.Ataxx.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

import static com.Ataxx.test.GameException.error;
import static com.Ataxx.test.PieceState.*;


/** An Ataxx board.
 *
 *  The squares are labeled by column (a char value between
 *  'a' - 2 and 'g' + 2) and row (a char value between '1' - 2 and '7'
 *  + 2).
 *
 *  Values of the column outside 'a' and 'g' and of the row outside '1' to '7' denote
 *  two layers of border squares, which are always blocked.
 *
 *  This artificial border (which is never actually printed) is a common
 *  trick that allows one to avoid testing for edge conditions.
 *
 *  For example, to look at all the possible moves from a square, sq,
 *  on the normal board (i.e., not in the border region), one can simply
 *  look at all squares within two rows and columns of sq without worrying
 *  about going off the board. Since squares in the border region are
 *  blocked, the normal logic that prevents moving to a blocked square
 *  will apply.
 *
 *  Moves on this board are denoted by Moves.*/
class Board {

    /** A new, cleared board in the initial configuration. */
    Board() {
        ataxxBoard = new PieceState[DEEPER_ONESIDE * DEEPER_ONESIDE];
        setNotifier(NOP);
        clear();
    }

    /** A board whose initial contents are copied from BOARD0,
     *  but whose notifier does nothing. */
    Board(Board board0) {
        ataxxBoard = board0.ataxxBoard.clone();
        nextMove = board0.nextMove();
        totalMoves = new ArrayList<>();
        colorNum = board0.colorNum.clone();
        consecJumpNum = board0.getConsecJumpNums();
        unblockedNum = board0.unblockedNum();
        winner = board0.winner;
        setNotifier(NOP);
    }

    /** Return the linearized index of the square that is DC columns and DR
     *  rows away from the square with index SQ. */
    static int getNeighbor(int sq, int dc, int dr) {
        return sq + dc + dr * DEEPER_ONESIDE;
    }

    /** Clear me to my starting state, with pieces in their initial
     *  positions and no blocks. */
    void clear() {
        nextMove = RED;
        totalMoves = new ArrayList<>();
        unblockedNum = ONESIDE * ONESIDE;
        consecJumpNum = 0;
        colorNum[RED.ordinal()] = 2;
        colorNum[BLUE.ordinal()] = 2;
        for (int i = 0; i < ataxxBoard.length; i++) {
            if (i == DEEPER_ONESIDE * 2 + 8
                    || i == DEEPER_ONESIDE * 8 + 2) {
                setContent(i, RED);
            } else if (i == DEEPER_ONESIDE * 8 + 8
                    || i == DEEPER_ONESIDE * 2 + 2) {
                setContent(i, BLUE);
            } else if (i % DEEPER_ONESIDE == 0
                    || i % DEEPER_ONESIDE == DEEPER_ONESIDE - 1
                    || i < DEEPER_ONESIDE
                    || i > DEEPER_ONESIDE * (DEEPER_ONESIDE - 1)) {
                setContent(i, BLOCKED);
            } else {
                setContent(i, EMPTY);
            }
        }
        winner = null;
        announce();
    }

    /** Return true iff MOVE is legal on the current board. */
    boolean moveLegal(Move move) {
        if (move == null) {
            return false;
        } else {
            if (move.isPass()) {
                return !couldMove(nextMove());
            } else if (move.col1() < 'a'
                    || move.col1() > 'g'
                    || move.row1() < '1'
                    || move.row1() > '7') {
                return false;
            }
            PieceState curState = getContent(move.fromIndex());
            PieceState destState = getContent(move.toIndex());
            if (curState != nextMove()) {
                return false;
            }
            return destState == EMPTY;
        }
    }

    /** Return true iff player WHO could move, ignoring whether it is
     *  that player's move and whether the getAtaxxGame is over. */
    boolean couldMove(PieceState who) {
        for (char r = '7'; r >= '1'; r--) {
            for (char c = 'a'; c <= 'g'; c++) {
                if (getContent(c, r) == who) {
                    for (int i = -2; i <= 2; i++) {
                        for (int j = -2; j <= 2; j++) {
                            if (i != 0 || j != 0) {
                                char c2 = (char) (c + i);
                                char r2 = (char) (r + j);
                                if (c2 >= 'a'
                                        && c2 <= 'g'
                                        && r2 >= '1'
                                        && r2 <= '7'
                                        && getContent(c2, r2) == EMPTY) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /** Assuming MOVE has the format "-" or "C0R0-C1R1", make the denoted
     *  move ("-" means "pass"). */
    void createMove(String move) {
        createMove(Move.move(move));
    }

    /** Perform the move C0R0-C1R1, or pass if C0 is '-'.  For moves
     *  other than pass, assumes that moveLegal(C0, R0, C1, R1). */
    void createMove(char c0, char r0, char c1, char r1) {
        if (c0 == '-') {
            createMove(Move.pass());
        } else {
            createMove(Move.move(c0, r0, c1, r1));
        }
    }

    /** Make the MOVE on this Board, assuming it is legal. */
    void createMove(Move move) {
        if (!moveLegal(move) || getWinner() != null) {
            return;
        }
        if (move.isPass()) {
            pass();
            getWinner();
            return;
        }
        totalMoves.add(move);
        PieceState opponent = nextMove().opposite();
        if (move.isJump()) {
            setContent(move.fromIndex(), EMPTY);
            setContent(move.toIndex(), nextMove());
            convertColor(move, nextMove());
            consecJumpNum++;
        } else if (move.isClone()) {
            setContent(move.toIndex(), nextMove());
            convertColor(move, nextMove());
            consecJumpNum = 0;
            incrColorPieces(nextMove(), 1);
        }
        nextMove = opponent;
        getWinner();
        announce();
    }

    /** Changes the color of surrounding pieces within 1
     *  by Move.
     *  @param move The move that is used for converting
     *             the color around its destination.
     *  @param self The color of the player's piece. */
    private void convertColor(Move move, PieceState self) {
        convertColor(move.toIndex(), self);
    }

    /** Changes the color of surrounding pieces within 1
     *  by index.
     *  @param index The index that is used for converting
     *             the color.
     *  @param self The color of the player's piece. */
    private void convertColor(int index, PieceState self) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (getContent(getNeighbor(index, i, j)) == self.opposite()) {
                    setContent(getNeighbor(index, i, j), self);
                    incrColorPieces(self, 1);
                    incrColorPieces(self.opposite(), -1);
                }
            }
        }
    }

    /** Update to indicate that the current player passes, assuming it
     *  is legal to do so.  */
    void pass() {
        nextMove = nextMove.opposite();
        announce();
    }

    /** Return true iff it is legal to place a block at C R. */
    boolean blockLegal(char c, char r) {
        if (moveNums() != 0) {
            return false;
        }
        char col = (char) ('g' - (c - 'a'));
        char row = (char) ('7' - (r - '1'));
        return !(getContent(col, row) != EMPTY
                && getContent(c, row) != EMPTY
                && getContent(col, r) != EMPTY
                && getContent(c, r) != EMPTY);
    }
	
	

	// Final Project Part A.1.2 Setting a Block

    /** Set a block on the square c r and its reflections across the middle row and/or column,
     *      if that square is unoccupied.
	 *  If the square has been occupied by a piece or a block, an error will be thrown.
     */
    void setBlock(char c, char r) {
		
        // Please do not change the following codes
        if (!blockLegal(c, r)) {
            throw error("illegal block placement");
        }
        char col = (char) ('g' - (c - 'a'));
        char row = (char) ('7' - (r - '1'));

        // Complete the code
        // Hints: Consider using the method setContent and the variable unblockedNum
        setContent(c,r,BLOCKED);
        setContent(c,row,BLOCKED);
        setContent(col,r,BLOCKED);
        setContent(col,row,BLOCKED);
        if(c != col && r != row){
            if(c == col || r == row){
                unblockedNum -=2;
            }else {
                unblockedNum -=4;
            }
        }else {
            unblockedNum -=1;
        }





        // Please do not change the following codes
        if (!couldMove(RED) && !couldMove(BLUE)) {
            winner = EMPTY;
        }
        announce();
    }
	

    /** Return total number of unblocked squares. */
    int unblockedNum() {
        return unblockedNum;
    }


    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Board other)) {
            return false;
        }
        return Arrays.equals(ataxxBoard, other.ataxxBoard);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(ataxxBoard);
    }

    /** Return a text depiction of the board.
     *  If LEGEND, supply row and column numbers around the edges. */
    String toString(boolean legend) {
        String sentences = "";
        for (char r = '7'; r >= '1'; r -= 1) {
            if (legend) {
                sentences = sentences + r;
            }
            sentences = sentences + " ";
            for (char c = 'a'; c <= 'g'; c += 1) {
                switch (getContent(c, r)) {
                    case RED -> sentences = sentences + " r";
                    case BLUE -> sentences = sentences + " b";
                    case BLOCKED -> sentences = sentences + " X";
                    case EMPTY -> sentences = sentences + " -";
                    default -> {
                    }
                }
            }
            sentences = sentences + "\n";
        }
        if (legend) {
            sentences = sentences + "   a b c d e f g";
        }
        return sentences;
    }

    /** Set my notifier to NOTIFY. */
    public void setNotifier(Consumer<Board> notify) {
        notifier = notify;
        announce();
    }

    /** Take any action that has been setContent for a change in my state. */
    private void announce() {
        notifier.accept(this);
    }

    /** Return the color of the player who has the next move.  The
     *  value is arbitrary if the getAtaxxGame is over. */
    PieceState nextMove() {
        return nextMove;
    }

    /** Return the linearized index of square COL ROW. */
    static int index(char col, char row) {
        return (row - '1' + 2) * DEEPER_ONESIDE + (col - 'a' + 2);
    }
	
	
	// Final Project Part A.1.4 Getting the Winner

    /** The method to find the winner of the game.
	  * It also stores the result in instance variable winner.
	  * @return null if the game is not finished.
	  * @return RED or BLUE if the game is finished and there is a winner of that color.
	  * @return EMPTY if the game is finished but there is not winner / a tie.
      */
    PieceState getWinner() {
        // complete the code 
        // Hints: Consider using couldMove, getColorNums, getConsecJumpNums

      if(getConsecJumpNums() == 25 || !(couldMove(BLUE) && couldMove(RED)) || getColorNums(RED)==0 || getColorNums(BLUE)==0){
          if(getColorNums(RED) < getColorNums(BLUE)){
              winner = BLUE;
          } else if (getColorNums(RED) > getColorNums(BLUE)){
              winner = RED;
          }else {
              winner = EMPTY;
          }
      }else {
          winner = null;
      }

		// Please do not change the return statement below
        return winner;
    }
	

    /** Increment getColorNums(COLOR) by K. */
    private void incrColorPieces(PieceState color, int k) {
        colorNum[color.ordinal()] += k;
    }

    /** The current contents of square CR, where 'a'-2 <= C <= 'g'+2, and
     *  '1'-2 <= R <= '7'+2.  Squares outside the range a1-g7 are all
     *  BLOCKED.  Returns the same value as getContent(index(C, R)). */
    PieceState getContent(char c, char r) {
        return ataxxBoard[index(c, r)];
    }

    /** Return the current contents of square with linearized index SQ. */
    PieceState getContent(int sq) {
        return ataxxBoard[sq];
    }

    /** Set square at C R to V.
     *  This is used for changing contents of the board. */
    public void setContent(char c, char r, PieceState v) {
        ataxxBoard[index(c, r)] = v;
    }

    /** Set square at linearized index SQ to V.
     * This is used for changing contents of the board. */
    private void setContent(int sq, PieceState v) {
        ataxxBoard[sq] = v;
    }

    /** Return total number of moves and passes since the last
     *  clear or the creation of the board. */
    int moveNums() {
        return totalMoves.size();
    }

    /** Return number of non-pass moves made in the current getAtaxxGame since the
     *  last clone move added a piece to the board (or since the
     *  start of the getAtaxxGame). Used to detect end-of-getAtaxxGame. */
    int getConsecJumpNums() {
        return consecJumpNum;
    }


	// Final Project Part A.1.1 Getting the Number of Colors
	
    /** Return number of color pieces on the board.
     *  This method will be used in the method getScore().
	 *  @param color represents the color of the piece, either RED or BLUE.
	 *  @return the number of pieces having the corresponding color.
	 */
    int getColorNums(PieceState color) {
        // complete the code

        return colorNum[color.ordinal()];
    }

    // Please do not change code of the following method.
    public String getScore(){
        return getColorNums(RED)+ " red vs " + getColorNums(BLUE) + " blue";
    }


    /** A notifier that does nothing. */
    private static final Consumer<Board> NOP = (s) -> { };

    /** Use notifier.accept(this) to announce changes to this board. */
    private Consumer<Board> notifier;

    /** For reasons of efficiency in copying the board,
     *  we use a 1D array to represent it, using the usual access
     *  algorithm: row r, column c => index(r, c).
     *
     *  Next, instead of using a 7x7 board, we use an 11x11 board in
     *  which the outer two rows and columns are blocks, and
     *  row 2, column 2 actually represents row 0, column 0
     *  of the real board.  As a result of this trick, there is no
     *  need to special-case being near the edge: we don't move
     *  off the edge because it looks blocked.
     *
     *  Using characters as indices, it follows that if 'a' <= c <= 'g'
     *  and '1' <= r <= '7', then row r, column c of the board corresponds
     *  to ataxxBoard[(c -'a' + 2) + 11 (r - '1' + 2) ]. */
    public PieceState[] ataxxBoard;

    /** Player that is next to move. */
    private PieceState nextMove;

    /** Number of consecutive non-cloning moves since the
     *  last clear or the beginning of the getAtaxxGame. */
    private int consecJumpNum;

    /** Total number of unblocked squares. */
    private int unblockedNum = ONESIDE * ONESIDE;

    /** Number of blue and red pieces, indexed by the ordinal positions of
     *  enumerals BLUE and RED. */
    private int[] colorNum = new int[BLUE.ordinal() + 1];

    /** Set to winner when getAtaxxGame ends (EMPTY if tie).  Otherwise, it is null. */
    private PieceState winner;

    /** List of all moves since the last clear or beginning of
     *  the getAtaxxGame. */
    private ArrayList<Move> totalMoves;

    /** Number of squares on a side of the board. */
    static final int ONESIDE = Move.ONESIDE;

    /** Length of a side + an artificial 2-deep border region.
     * This is unrelated to a move that is a "clone". */
    static final int DEEPER_ONESIDE = Move.DEEPER_ONESIDE;

    /** Number of consecutive non-cloning moves before getAtaxxGame ends. */
    static final int CONSEC_JUMP_LIMIT = 25;
}
