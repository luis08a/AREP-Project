package edu.eci.arep.framework.core;

import java.io.IOException;

/**
 * Driver
 */
public class Driver {

    public static void main(String[] args) {
        Service s = new Service();
        s.initialize();
        try {
            s.listen();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}