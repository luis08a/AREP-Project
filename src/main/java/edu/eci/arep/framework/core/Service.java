package edu.eci.arep.framework.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

/**
 * Service
 */
public class Service {
    private Map<String, Handler> URLHandler;

    public void listen() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String inputLine, outputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            if (!in.ready()) {
                break;
            }
        }

        outputLine = "<!DOCTYPE html>" + "<html>" + "<head>" + "<meta charset=\"UTF-8\">"
                + "<title>Title of the document</title>\n" + "</head>" + "<body>" + "My Web Site" + "</body>"
                + "</html>" + inputLine;
        out.write("HTTP/1.1 200 OK\r\n");
        out.write("Content-Type: text/html\r\n");
        out.write("\r\n");
        out.write(outputLine);
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }

    public void initialize() {
        Reflections reflections = new Reflections("edu.eci.arep.framework.webServices");

        Set<Class<? extends Object>> allClasses = 
            reflections.getSubTypesOf(Object.class);
        for (Class c : allClasses) {
            for (Method m : c.getDeclaredMethods()) {
                if(m.isAnnotationPresent(Web.class)){
                    URLHandler.put("apps/"+m.getAnnotation(Web.class).value(), new StaticMethodHandler(m));
                }
            }
        }
    }
}