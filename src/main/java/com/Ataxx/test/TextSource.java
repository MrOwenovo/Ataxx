package com.Ataxx.test;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** A CommandSource that takes commands from a Reader. */
class TextSource implements CommandSource {

    /** A source of commands read from the concatenation of the content of
     *  READERS. */
    TextSource(List<Reader> readers) {
        if (readers.isEmpty()) {
            throw new IllegalArgumentException("must be at least one reader");
        }
        this.readers = new ArrayList<>(readers);
        input = new Scanner(readers.remove(0));
    }

    @Override
    public String getCommand(String prompt) {
        if (prompt != null) {
            System.out.print(prompt);
            System.out.flush();
        }
        if (input.hasNextLine()) {
            return input.nextLine();
        } else if (!readers.isEmpty()) {
            input = new Scanner(readers.remove(0));
            return getCommand(prompt);
        } else {
            return null;
        }
    }

    /** Source of command input. */
    private Scanner input;
    /** Readers to use after the first. */
    private ArrayList<Reader> readers;
}
