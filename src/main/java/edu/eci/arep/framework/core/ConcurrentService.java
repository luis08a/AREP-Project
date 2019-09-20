package edu.eci.arep.framework.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ConcurrentService implements Runnable {
    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; // returns default port if heroku-port isn't set (i.e.on localhost)
    }

    public ConcurrentService(int poolSize) throws IOException {
        int port = getPort();
        serverSocket = new ServerSocket(port);
        pool = Executors.newFixedThreadPool(poolSize);
        Service.initialize();
    }

    public void run() { // run the service
        try {
            for (;;) {
                pool.execute(new Service(serverSocket.accept()));
            }
        } catch (IOException ex) {
            pool.shutdown();
        }
    }
}