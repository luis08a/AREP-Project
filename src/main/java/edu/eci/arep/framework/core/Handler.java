package edu.eci.arep.framework.core;

/**
 * Handler
 */
public interface Handler {

    String process();
    String process(String params[]);
}