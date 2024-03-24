package com.Ataxx.test;

import java.util.Objects;

import static com.Ataxx.test.GameException.error;
import static com.Ataxx.test.PieceState.*;


/** Main logic for playing the Ataxx game.*/
class Game {
    /** A new Game that takes command/move input from INP, displays the board using VIEW,
     *  and uses REPORTER for messages to the user and error messages.
     *  SEED is intended to seed a random number generator, if one is used in an AIPlayer.
     */
    Game(CommandSource inp, View view, Reporter reporter) {
        input = inp;
        this.view = view;
        this.reporter = reporter;
        seed = (long) (Math.random() * Long.MAX_VALUE);
        ataxxBoard = new Board();
        ataxxBoard.setNotifier((b) -> this.view.update(b));
    }

    /** Returns the getAtaxxGame board.  This board is not intended to be modified
     *  by the caller. */
    Board getAtaxxBoard() {
        return ataxxBoard;
    }

    /** Return true iff the current getAtaxxGame is not over. */
    boolean gameNotOver() {
        return ataxxBoard.getWinner() == null;
    }


    /** Play a session of Ataxx.
     *  This may include multiple games, and proceeds until the user exits.
     *  Returns an exit code: 0 is normal; any positive quantity indicates an error.  */
    int play() {
        boolean winnerAnnounced;
        ataxxBoard.clear();
        setManual(RED);
        setAI(BLUE);
        exit = -1;
        winnerAnnounced = false;
        while (exit < 0) {
            if (ataxxBoard.getWinner() == null) {
                winnerAnnounced = false;
                try {
                    runCommand(getAtaxxPlayer(ataxxBoard.nextMove()).getAtaxxMove());
                } catch (GameException e) {
                    reportError(e.getMessage());
                }
            } else if (!gameNotOver()) {
                if (!winnerAnnounced) {
                    reporter.announceWinner(ataxxBoard.getWinner());
                    winnerAnnounced = true;
                }
                runCommand(getCommand("-> "));
            }
        }
        return exit;
    }

    /** Return a command from the current source, using PROMPT as a
     *  prompt, if needed. */
    String getCommand(String prompt) {
        String cmnd = input.getCommand(prompt);
        return Objects.requireNonNullElse(cmnd, "quit");
    }

    /** Perform the move denoted by MOVESTR, which must be legal. */
    void createMove(String moveStr) {
        ataxxBoard.createMove(moveStr);
        if (board_on) {
            printBoard();
        }
    }

    /** Place a block at the position PLACE (in crformat), and in its three
     *  reflected squares symmetrically. */
    void block(String place) {
        if (ataxxBoard.moveNums() > 0) {
            throw error("block-setting must precede first move.");
        }
        if (!place.matches("[a-i][1-9]")) {
            throw error("invalid square designation");
        }
        ataxxBoard.setBlock(place.charAt(0), place.charAt(1));
        if (board_on) {
            printBoard();
        }

    }

    /** Get the current number of BLUE and RED color pieces respectively in the board. */
    void getScore(){
        System.out.println(ataxxBoard.getScore());
    }


    /** Report the move MOVE by PLAYER. */
    void reportMove(Move move, PieceState player) {
        reporter.announceMove(move, player);
    }

    /** Make the player of COLOR an AIPlayer for subsequent moves. */
    private void setAI(PieceState color) {
        setAtaxxPlayer(color, new AIPlayer(this, color, seed));
        seed += 1;
    }

    /** Make the player of COLOR take manual input from the user for
     *  subsequent moves. */
    private void setManual(PieceState color) {
        setAtaxxPlayer(color, new Manual(this, color));
    }

    /** Return the Player playing COLOR. */
    private Player getAtaxxPlayer(PieceState color) {
        return ataxxPlayers[color.ordinal()];
    }

    /** Set getAtaxxPlayer(COLOR) to PLAYER. */
    private void setAtaxxPlayer(PieceState color, Player player) {
        ataxxPlayers[color.ordinal()] = player;
    }

    /** Clear the board to its initial state. */
    void clear() {
        ataxxBoard.clear();
    }

    /** Print a board with row/column numbers. */
    private void printBoard() {
        reporter.message(ataxxBoard.toString(true));
    }

    /** Execute command CMNDSTR.  Throws GameException on errors. */
    public void runCommand(String cmndStr) {
        Command cmnd = Command.parseCommand(cmndStr);
        String[] parts = cmnd.operands();
        try {
            switch (cmnd.commandType()) {
                case NEW:
                    clear();
                    break;
                case BOARD:
                    printBoard();
                    break;
                case MANUAL:
                    setManual(colorParse(parts[0]));
                    break;
                case AI:
                    setAI(colorParse(parts[0]));
                    break;
                case BLOCK:
                    block(parts[0]);
                    break;
                case PIECEMOVE:
                    createMove(parts[0]);
                    break;
                case SCORE:
                    getScore();
                    break;
                case BOARD_ON:
                    board_on = true;
                    break;
                case BOARD_OFF:
                    board_on = false;
                    break;
                case QUIT:
                    exit = 0;
                    break;
                case ERROR:
                    throw error("Unknown command.");
                default:
                    break;
            }
        } catch (NumberFormatException excp) {
            reportError("Bad number in: %s", cmnd);
        } catch (ArrayIndexOutOfBoundsException excp) {
            reportError("Argument(s) missing: %s", cmnd);
        } catch (GameException excp) {
            reportError(excp.getMessage());
        }
    }

    /** Send an error message to the user formed from arguments
     *  FORMAT and ARGS, whose meanings are as for printf. */
    void reportError(String format, Object... args) {
        reporter.error(format, args);
    }

    /** Returns command input for the current getAtaxxGame. */
    private final CommandSource input;

    /** Outlet for responses to the user. */
    private final Reporter reporter;

    /** The board on which I record all moves. */
    private final Board ataxxBoard;

    /** Displayer of boards. */
    private View view;

    /** True iff we should print the board after each move. */
    private boolean board_on;

    /** Current pseudo-random number seed.
     *  Provided as an argument to AIs that use a random element in their choices.
     *  Incremented for each AIPlayer to which it is supplied.
     */
    private long seed;

    /** When set to a non-negative value, indicates that play should terminate
     *  at the earliest possible point, returning exit.
     *  When negative, indicates that the session is not over. */
    private int exit;

    /** Current Ataxx players, indexed by color (RED, BLUE). */
    private final Player[] ataxxPlayers = new Player[PieceState.values().length];

}
