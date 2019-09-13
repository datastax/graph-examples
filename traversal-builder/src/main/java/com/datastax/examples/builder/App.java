package com.datastax.examples.builder;

import com.datastax.dse.driver.api.core.DseSession;
import com.datastax.dse.driver.api.core.config.DseDriverOption;
import com.datastax.dse.driver.api.core.graph.DseGraph;
import com.datastax.dse.driver.api.core.graph.ScriptGraphStatement;
import groovy.util.Eval;
import org.apache.tinkerpop.gremlin.groovy.jsr223.GroovyTranslator;
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.jline.builtins.Completers;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Daniel Kuppitz (http://gremlin.guru)
 */
public class App implements Runnable, Closeable {

    private final static String APP_NAME = "Shortest Path Traversal Builder";

    private DseSession session;
    private GraphTraversalSource g, tg;

    private AppState state = AppState.MAIN;
    private GraphTraversal traversal;
    private String host;
    private String graphName;
    private String title;
    private String errorMessage;

    private App() throws IOException {
        g = tg = TinkerGraph.open().traversal();
        Eval.me("g", g, resource("graph.groovy"));
    }

    public static void main(String... args) {
        try (App app = new App()) {
            app.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            Terminal terminal = TerminalBuilder.builder()
                    .signalHandler(Terminal.SignalHandler.SIG_IGN)
                    .name(APP_NAME + " Terminal")
                    .build();

            Object[] N = IntStream.range(1, ShortestPathTraversals.size() + 1).mapToObj(Integer::toString).toArray();
            Completer completer = new Completers.TreeCompleter(
                    Completers.TreeCompleter.node("graph"),
                    Completers.TreeCompleter.node("connect"),
                    Completers.TreeCompleter.node("disconnect"),
                    Completers.TreeCompleter.node("show", Completers.TreeCompleter.node(N)),
                    Completers.TreeCompleter.node("run", Completers.TreeCompleter.node(N)),
                    Completers.TreeCompleter.node("exit"));

            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .appName(APP_NAME + " Test Application")
                    .completer(completer)
                    .build();

            while (true) {

                Graph graph = g.getGraph();
                String prompt = String.format("%s $ ", graph instanceof EmptyGraph
                        ? String.format("DSE Graph @ %s [%s]", host, graphName)
                        : graph);

                terminal.puts(InfoCmp.Capability.clear_screen);

                printHeader(terminal);
                printCommandsAndTraversals(terminal);

                switch (state) {
                    case GRAPH:
                        printGraphVisualization(terminal);
                        break;
                    case DISCONNECT:
                        disconnect();
                        state = AppState.MAIN;
                        continue;
                    case TRAVERSAL:
                        printTraversal(terminal, title, traversal);
                        break;
                    case RESULT:
                        printTraversalResult(terminal, title, traversal);
                        break;
                    case TRAVERSAL_RESULT:
                        printTraversal(terminal, title, traversal);
                        printTraversalResult(terminal, null, traversal);
                        break;
                    case ERROR:
                        printErrorMessage(terminal, errorMessage);
                        break;
                }

                terminal.flush();

                state = AppState.MAIN;

                String line;
                try {
                    line = reader.readLine(prompt).trim();
                } catch (UserInterruptException e) {
                    continue;
                } catch (EndOfFileException e) {
                    return;
                }

                Matcher m = null;
                ShortestPathTraversals.ShortestPathTraversal spt;

                if (Commands.GRAPH.matcher(line).find()) {
                    state = AppState.GRAPH;
                } else if (Commands.CONNECT.matcher(line).find()) {
                    try {
                        terminal.writer().println();
                        connect(reader);
                    }
                    catch (Exception ex) {
                        g = tg;
                        errorMessage = ex.getMessage();
                        state = AppState.ERROR;
                    }
                    continue;
                } else if (Commands.DISCONNECT.matcher(line).find()) {
                    state = AppState.DISCONNECT;
                } else if ((m = Commands.SHOW.matcher(line)).find()) {
                    state = AppState.TRAVERSAL;
                } else if ((m = Commands.RUN.matcher(line)).find()) {
                    state = AppState.RESULT;
                } else if (Commands.EXIT.matcher(line).find()) {
                    System.out.println();
                    break;
                } else if ((m = Pattern.compile("^(([0-9]+))$").matcher(line)).find()) {
                    state = AppState.TRAVERSAL_RESULT;
                } else if (!line.isEmpty()) {
                    errorMessage = "Invalid command: " + line;
                    state = AppState.ERROR;
                }

                if (Arrays.asList(AppState.TRAVERSAL, AppState.RESULT, AppState.TRAVERSAL_RESULT).contains(state)
                        && m != null) {
                    spt = ShortestPathTraversals.get(Integer.parseInt(m.group(2)) - 1);;
                    if (spt != null) {
                        title = spt.description;
                        traversal = spt.traversalBuilder.apply(g);
                    } else {
                        errorMessage = String.format("Invalid traversal index %d. Pick a number between 1 and %d.",
                                Integer.parseInt(m.group(2)), ShortestPathTraversals.size());
                        state = AppState.ERROR;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect(LineReader reader) throws IOException {
        host =          reader.readLine("Host (default: localhost)    : ").trim();
        String port =   reader.readLine("Port (default: 9042)         : ").trim();
        String dcName = reader.readLine("DC name (default: Graph)     : ").trim();
        graphName =     reader.readLine("Graph name (default: modern) : ").trim();
        if (host.isEmpty()) host = "localhost";
        if (port.isEmpty()) port = "9042";
        if (dcName.isEmpty()) dcName = "Graph";
        if (graphName.isEmpty()) graphName = "modern";
        disconnect();
        InetSocketAddress contactPoint = new InetSocketAddress(host, Integer.parseInt(port));
        session = DseSession.builder()
                .addContactPoint(contactPoint)
                .withLocalDatacenter(dcName).build();
        session.execute(ScriptGraphStatement
                .builder("system.graph(graphName).ifNotExists().create()")
                .setQueryParam("graphName", graphName).build());
        session.execute(ScriptGraphStatement.newInstance(resource("schema.groovy")).setGraphName(graphName));
        session.execute(ScriptGraphStatement.newInstance(resource("graph.groovy")).setGraphName(graphName));
        g = AnonymousTraversalSource.traversal().withRemote(DseGraph.remoteConnectionBuilder(session)
                .withExecutionProfile(session.getContext().getConfig().getDefaultProfile()
                        .withString(DseDriverOption.GRAPH_NAME, graphName)).build());
    }

    private void disconnect() {
        if (session != null) {
            session.closeAsync();
            session = null;
        }
        g = tg;
    }

    private static void printHeader(Terminal terminal) {
        terminal.writer().println(new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW).bold())
                .append("Shortest Path Traversal Builder Examples\n")
                .style(AttributedStyle.DEFAULT).toAnsi());
    }

    private void printCommandsAndTraversals(Terminal terminal) {
        Iterator<Commands.Command> commandIterator = Commands.get().iterator();
        Iterator<ShortestPathTraversals.ShortestPathTraversal> traversalIterator = ShortestPathTraversals.get().iterator();
        AttributedStringBuilder asb = new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE)).append("   Command  ")
                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE)).append("Description")
                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE)).append(String.format("%50s", "N  "))
                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE)).append("Traversal Description\n\n");

        int i = 0;
        while (commandIterator.hasNext() || traversalIterator.hasNext()) {
            int padding = 71;
            if (commandIterator.hasNext()) {
                Commands.Command command = commandIterator.next();
                if (Commands.DISCONNECT.matcher(command.command).find()
                        && (session == null || session.isClosed())) {
                    continue;
                }
                asb
                        .style(AttributedStyle.DEFAULT.bold()).append(String.format("%10s  ", command.command))
                        .style(AttributedStyle.DEFAULT).append(command.description);
                padding -= 12 + command.description.length();
            }
            if (traversalIterator.hasNext()) {
                ShortestPathTraversals.ShortestPathTraversal traversal = traversalIterator.next();
                String fmt = String.format("%%%dd  ", padding);
                asb
                        .style(AttributedStyle.DEFAULT.bold()).append(String.format(fmt, ++i))
                        .style(AttributedStyle.DEFAULT).append(traversal.description);
            }
            asb.append("\n");
        }
        asb.styleMatches(Pattern.compile("(\\b((g(?=raph  ))|(d(?=isconnect  ))|(c(?=onnect  )|(s(?=how N))|(r(?=un N))))|((?<=e)x(?=it)))"),
                AttributedStyle.DEFAULT.bold().underline());
        terminal.writer().println(asb.toAnsi());
        terminal.writer().flush();
    }

    private static void printGraphVisualization(Terminal terminal) throws IOException {
        for (String line : resource("graph.txt").split("\n")) {
            terminal.writer().println("  " + line);
        }
        terminal.writer().println();
    }

    private static void printTraversal(Terminal terminal, String title, GraphTraversal traversal) {
        terminal.writer().println(new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))
                .append(title).append("\n")
                .style(AttributedStyle.DEFAULT).toAnsi());
        terminal.writer().println(formatTraversal(GroovyTranslator.of("g").translate(traversal.asAdmin().getBytecode())));
    }

    private void printTraversalResult(Terminal terminal, String title, GraphTraversal traversal) {
        if (title != null) {
            terminal.writer().println(new AttributedStringBuilder()
                    .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))
                    .append(title).append("\n")
                    .style(AttributedStyle.DEFAULT).toAnsi());
        }
        traversal.forEachRemaining(r -> terminal.writer().println(formatResult(r)));
        terminal.writer().println();
    }

    private static void printErrorMessage(Terminal terminal, String message) {
        terminal.writer().println(new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
                .append(message).append("\n")
                .style(AttributedStyle.DEFAULT).toAnsi());
    }

    private static String resource(String filename) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(filename)) {
            if (is != null) {
                try (InputStreamReader isr = new InputStreamReader(is);
                     BufferedReader reader = new BufferedReader(isr)) {
                    return reader.lines().collect(Collectors.joining("\n"));
                }
            }
        }
        return "";
    }

    private static String formatTraversal(String traversal) {
        return highlightTraversal(traversal
                .replaceFirst("g\\.", "  g.")
                .replaceAll("repeat", "\n    repeat")
                .replaceAll("\\b(Column|Operator|Pop|Scope|T|P)\\.", "")
                .replaceAll("\\(int\\) ", "")
                .replaceAll("until", "\n      until")
                .replaceAll("emit", "\n      emit")
                .replaceAll("as\\(\"sp.end\"\\).", "$0\n    ")
                .replaceAll("(?<!__\\.)(?<!\")path", "\n    path")
                .replaceAll("\\)\\.group", ").\n    group")
                .replaceAll("order", "\n    order")
                .replaceAll("project", "\n    project")
                .replaceAll("(?<!__\\.)limit", "\n    limit")
                .replaceAll("index\\(", "\n    index(")
                .replaceAll("(?<!__\\.)(?<! )select", "\n    select")
                .replaceAll("\"~tinkerpop.index.indexer\",1", "WithOptions.indexer,WithOptions.map"))
                + "\n";
    }

    private static String highlightTraversal(String traversal) {
        return new AttributedStringBuilder()
                .append(traversal)
                .styleMatches(
                        Pattern.compile("\"[^\"]*\""),
                        AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN).faint())
                .styleMatches(
                        Pattern.compile("\\b[0-9]+(L|(\\.[0-9]+))?\\b"),
                        AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN).faint())
                .styleMatches(
                        Pattern.compile("\\b(assign|gt|id|keys|local|lt|sum|values)\\b"),
                        AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA).faint())
                .styleMatches(
                        Pattern.compile("\\b(as|by|emit|from)\\b"),
                        AttributedStyle.DEFAULT.italic())
                .styleMatches(
                        Pattern.compile("__\\."),
                        AttributedStyle.DEFAULT.foreground(AttributedStyle.BLACK | AttributedStyle.BRIGHT))
                .toAnsi();
    }

    private static String formatResult(Object result) {
        return "  " + highlightResult(result.toString());
    }

    private static String highlightResult(String result) {
        return new AttributedStringBuilder()
                .append(result)
                .styleMatches(
                        Pattern.compile("[a-z]+\\b(?![\\[=])"),
                        AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN).faint())
                .styleMatches(
                        Pattern.compile("\\b[0-9]+(L|(\\.[0-9]+))?\\b"),
                        AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN))
                .styleMatches(
                        Pattern.compile("[a-z]+(?==)"),
                        AttributedStyle.DEFAULT)
                .styleMatches(
                        Pattern.compile("[a-z]+\\b(?=\\[)"),
                        AttributedStyle.DEFAULT.faint())
                .styleMatches(
                        Pattern.compile("[a-z]+\\b(?==)"),
                        AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA).faint())
                .toAnsi();
    }

    @Override
    public void close() {
        disconnect();
    }
}