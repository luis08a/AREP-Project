package edu.eci.arep.framework.core;

import java.io.IOException;

/**
 * Driver
 */
public class Driver {

    public static void main(String[] args) {
        try {
            ConcurrentService s = new ConcurrentService(10);
            new Thread(s).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}