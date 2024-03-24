package com.Ataxx.test;


import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;


/** The main program for Ataxx. */
public class Main {

    /** Run Ataxx getAtaxxGame.  Options (in ARGS0):
     *       --display: Use GUI.
     *  Trailing arguments are input files; the standard input is the
     *  default.
     */
    public static void main(String[] args0) {
        CommandArgs args =
            new CommandArgs("--display{0,1}", args0);

        Game game;
        if (args.contains("--display")) {
            GUI display = new GUI("Ataxx");
            game = new Game(display, display, display);
            display.pack();
            display.setVisible(true);
        } else {
            ArrayList<Reader> inReaders = new ArrayList<>();
            if (args.get("--").isEmpty()) {
                inReaders.add(new InputStreamReader(System.in));
            } else {
                for (String name : args.get("--")) {
                    if (name.equals("-")) {
                        inReaders.add(new InputStreamReader(System.in));
                    } else {
                        try {
                            inReaders.add(new FileReader(name));
                        } catch (IOException excp) {
                            System.err.printf("Could not open %s", name);
                            System.exit(1);
                        }
                    }
                }
            }
            game = new Game(new TextSource(inReaders),
                            (b) -> { }, new TextReporter());
        }
        System.exit(game.play());
    }
}
