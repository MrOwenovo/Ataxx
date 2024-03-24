package com.Ataxx.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** All things to do with parsing commands. */
class Command {

    /** The command name. */
    private final CommandType commandType;
    /** Command arguments. */
    private final String[] operands;


    /** Command types.
     *  PIECEMOVE indicates a move of the form c0r0-c1r1.
     *  ERROR indicates a parse error in the command.
     *  All other commands are upper-case versions of what the programmer writes. */
    enum CommandType {
        AI("ai\\s+(red|blue)"),
        BLOCK("block\\s+([a-g][1-7])"),
        MANUAL("manual\\s+(red|blue)"),
        /* Regular moves. */
        PIECEMOVE("(-|[a-g][1-7]-[a-g][1-7])"),
        QUIT("q|quit"),
        NEW,
        BOARD,
        SCORE,
        BOARD_ON,
        BOARD_OFF,
        /** Syntax error in command. */
        ERROR(".*"),
        /** End of input stream. */
        EOF;

        /** PATTERN is a regular expression string giving the syntax of
         *  a command of the given type.  It matches the entire command,
         *  assuming no leading or trailing whitespace.  The groups in
         *  the commandPattern capture the operands (if any). */
        CommandType(String pattern) {
            this.commandPattern = Pattern.compile(pattern + "$");
        }

        /** A CommandType whose commandPattern is the lower-case version of its name. */
        CommandType() {
            commandPattern = Pattern.compile(this.toString().toLowerCase() + "$");
        }

        /** The Pattern commandPattern describing syntactically correct versions of this
         *  type of command. */
        private final Pattern commandPattern;

    }

    /** A new Command of commandType TYPE with OPERANDS as its operands. */
    Command(CommandType commandType, String... operands) {
        this.commandType = commandType;
        this.operands = operands;
    }

    /** Return the type of this Command. */
    CommandType commandType() {
        return commandType;
    }

    /** Returns this Command's operands. */
    String[] operands() {
        return operands;
    }

    /** Parse COMMAND, returning the command and its operands. */
    static Command parseCommand(String command) {
        if (command == null) {
            return new Command(CommandType.EOF);
        }
        for (CommandType commandType : CommandType.values()) {
            Matcher mat = commandType.commandPattern.matcher(command);
            if (mat.matches()) {
                String[] operands = new String [mat.groupCount()];
                for (int i = 1; i <= operands.length; i += 1) {
                    operands[i - 1] = mat.group(i);
                }
                return new Command(commandType, operands);
            }
        }
        throw new Error("Internal failure: error command did not match.");
    }

}
