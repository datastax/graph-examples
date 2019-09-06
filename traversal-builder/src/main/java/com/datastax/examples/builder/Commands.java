package com.datastax.examples.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Daniel Kuppitz (http://gremlin.guru)
 */
class Commands {

    static Pattern GRAPH = Pattern.compile("^(g|graph)$");
    static Pattern CONNECT = Pattern.compile("^(c|connect)$");
    static Pattern DISCONNECT = Pattern.compile("^(d|disconnect)$");
    static Pattern SHOW = Pattern.compile("^(s|show)[ ]+([0-9]+)$");
    static Pattern RUN = Pattern.compile("^(r|run)[ ]+([0-9]+)$");
    static Pattern EXIT = Pattern.compile("^(x|exit)$");

    private static List<Command> COMMANDS = new ArrayList<>();

    /*
     * Initialize application commands.
     */
    static {
        COMMANDS.add(new Command("graph", "Visualize the sample graph being used"));
        COMMANDS.add(new Command("connect", "Connect to a DSE cluster"));
        COMMANDS.add(new Command("disconnect", "Disconnect from DSE cluster"));
        COMMANDS.add(new Command("show N", "Show the generated traversal for use-case N"));
        COMMANDS.add(new Command("run N", "Execute the generated traversal for use-case N"));
        COMMANDS.add(new Command("exit", "Exit the application"));
    }

    static Iterable<Command> get() {
        return COMMANDS;
    }

    static class Command {

        final String command;
        final String description;

        Command(String command, String description) {
            this.command = command;
            this.description = description;
        }
    }
}
