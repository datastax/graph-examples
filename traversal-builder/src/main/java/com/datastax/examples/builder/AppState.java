package com.datastax.examples.builder;

/**
 * @author Daniel Kuppitz (http://gremlin.guru)
 */
public enum AppState {
    MAIN,
    GRAPH,
    CONNECT,
    DISCONNECT,
    TRAVERSAL,
    RESULT,
    TRAVERSAL_RESULT,
    ERROR
}
