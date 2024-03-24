package com.Ataxx.test;

import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

// Final Project Part A.2 Ataxx AI Player (A group project)

/** A Player that computes its own moves. */
class AIPlayer extends Player {


    /** A new AIPlayer for GAME that will play MYCOLOR.
     *  SEED is used to initialize a random-number generator,
     *  increase the value of SEED would make the AIPlayer move automatically.
     *  Identical seeds produce identical behaviour. */
    AIPlayer(Game game, PieceState myColor, long seed) {
        super(game, myColor);
    }

    @Override
    boolean isAuto() {
        return true;
    }

    @Override
    String getAtaxxMove() {
        Move move = findMove();
        getAtaxxGame().reportMove(move, getMyState());
        return move.toString();
    }

    private static final int MAX_DEPTH = 4;
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    private static final int INFTY = Integer.MAX_VALUE;

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(getAtaxxBoard());
        lastFoundMove = null;


        //Set the depth to 4 can force AI to search for 4 moves ahead and get the best move
        negaMax(b, MAX_DEPTH, true, -INFTY, INFTY);

        // Please do not change the codes below
        if (lastFoundMove == null) {
            lastFoundMove = Move.pass();
        }
        return lastFoundMove;
    }



    /** Return the scores of possible moves for the color of the current player.
     *  This method will be used in the method findMove().
     *  This is the algorithm to find the best move within 4 moves.
     *  The key is to try to maximize the score that AI can get
     *  and minimize the score that the opponent would like AI can get
     *  @param board represents the board of the current game.
     *  @param depth represents reverse value of current depth that the algorithm is searching.
     *  @param saveMove represents whether the algorithm should save the move or not.
     *  @param alpha represents what AI can make the best move in order to get a higher score.
     *  @param beta represents what the opponent can make the best move in order to let AI have a lower score.
     *  @return the number of pieces having the corresponding color.
     */
    private int negaMax(Board board, int depth, boolean saveMove, int alpha, int beta) {

        //Return the winning value if the game is over and return the static score if the depth is 0
        if(depth == 0 || board.getWinner() != null){
            return staticScore(board, WINNING_VALUE + depth);
        }

        int bestValue;
        bestValue = -INFTY;
        ArrayList<Move> listOfMoves =
                possibleMoves(board, board.nextMove());
        //Iterate through all possible moves and find the best move
        for (Move move : listOfMoves) {
            Board copyBoard = new Board(board);
            copyBoard.createMove(move);
            //the score of the move that is iterated
            int possible
                    = -(negaMax(copyBoard, depth - 1, false, -beta, -max(alpha, bestValue)));
            if (saveMove && possible > bestValue) {
                lastFoundMove = move;
            }
            //Check if the score is better than the current best value of the best move
            bestValue = max(bestValue, possible);
            if (beta <= bestValue) {
                break;
            }
        }
        if (bestValue == -INFTY) {
            return 0;
        }
        return bestValue;
    }




    /** Return the scores of possible moves for the color of the current player.
     *  This method will be used in the method negaMax().
     *  @param board represents the board of the current game.
     *  @param winningValue represents a winning value marked with the depth the algorithm is searching.
     *  @return the scores of possible moves for the color of the current player or a winning score if the game is over.
     */
    private int staticScore(Board board, int winningValue) {
        PieceState winner = board.getWinner();

        //The point here is if current color has more pieces than the opponent, then the score should be positive,
        //otherwise, the score should be negative.
        //It does not matter who represents the current color,
        //the point is to maximize the score of the current color that is played by one side so that the other side will not get a better score
        //Our algorithm did not just take into account what AI can make the best move,
        //but also what the opponent can make the best move in order to let AI have a lower score.
        int myColor = board.getColorNums(board.nextMove());
        int oppColor = board.getColorNums(board.nextMove().opposite());
        if (winner != null) {
            if(myColor > oppColor){
                return winningValue;
            }
            else if(myColor < oppColor){
                return -winningValue;
            }
        }
        return myColor - oppColor;
    }



    /** The move found by the last call to the findMove method above. */
    private Move lastFoundMove;


    /** Return all possible moves for a color.
     * @param board the current board.
     * @param myColor the specified color.
     * @return an ArrayList of all possible moves for the specified color. */
    private ArrayList<Move> possibleMoves(Board board, PieceState myColor) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        for (char row = '7'; row >= '1'; row--) {
            for (char col = 'a'; col <= 'g'; col++) {
                int index = Board.index(col, row);
                if (board.getContent(index) == myColor) {
                    ArrayList<Move> addMoves
                            = assistPossibleMoves(board, row, col);
                    possibleMoves.addAll(addMoves);
                }
            }
        }
        return possibleMoves;
    }

    /** Returns an Arraylist of legal moves.
     * @param board the board for testing
     * @param row the row coordinate of the center
     * @param col the col coordinate of the center */
    private ArrayList<Move>
    assistPossibleMoves(Board board, char row, char col) {
        ArrayList<Move> assistPossibleMoves = new ArrayList<>();
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if (i != 0 || j != 0) {
                    char row2 = (char) (row + j);
                    char col2 = (char) (col + i);
                    Move currMove = Move.move(col, row, col2, row2);
                    if (board.moveLegal(currMove)) {
                        assistPossibleMoves.add(currMove);
                    }
                }
            }
        }
        return assistPossibleMoves;
    }
}
