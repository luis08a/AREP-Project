package edu.eci.arep.framework.core;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

/**
 * Service
 */
public class Service {
    private Map<String, Handler> URLHandler = new HashMap<String, Handler>();

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; // returns default port if heroku-port isn't set (i.e.on localhost)
    }

    public void listen() throws IOException {
        int port = getPort();
        while (true) {

            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                System.err.println("Could not listen on port: " + port + ".");
                System.exit(1);
            }
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            while (!clientSocket.isClosed()) {
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine, outputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received: " + inputLine);
                    if (inputLine.contains("GET")) {
                        String path = inputLine.split(" ")[1];
                        if (path.contains("apps") && path.contains("?")) {
                            int i = path.indexOf("?");
                            String[] param = path.substring(i + 1).split("=");
                            String s = path.substring(path.indexOf("apps/"), i);
                            if (URLHandler.containsKey(s)) {
                                String response = URLHandler.get(s).process(new String[] { param[1] });
                                handleGetRequest("202 OK", "text/html", out, response);
                            } else {
                                handleGetRequest("404 Not Found", "text/html", out, "Not Found");
                            }
                        } else if (path.contains("apps") && !path.contains("?")) {
                            String s = path.substring(path.indexOf("apps/"));
                            if (URLHandler.containsKey(s)) {
                                String response = URLHandler.get(s).process();
                                handleGetRequest("202 OK", "text/html", out, response);
                            } else {
                                handleGetRequest("404 Not Found", "text/html", out, "Not Found");
                            }
                        } else {
                            if (path.contains(".")) {
                                handleFile(out, path, clientSocket);
                            } else {
                                outputLine = "<!DOCTYPE html>" + "<html>" + "<head>" + "<metacharset=\"UTF-8\">"
                                        + "<title>Title of the document</title>\n" + "</head>" + "<body>"
                                        + "My Web Framework" + "</body>" + "</html>";
                                handleGetRequest("200 OK", "text/html", out, outputLine);
                            }

                        }
                    }
                    out.close();
                    if (!in.ready()) {
                        break;
                    }
                }
                in.close();

            }
            clientSocket.close();
            serverSocket.close();
        }

    }

    public void initialize() {
        Reflections reflections = new Reflections("edu.eci.arep.framework.webServices", new SubTypesScanner(false));

        Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
        for (Class<?> c : allClasses) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Web.class)) {
                    URLHandler.put("apps/" + m.getAnnotation(Web.class).value(), new StaticMethodHandler(m));
                }
            }
        }
    }

    private void handleFile(PrintWriter out, String source, Socket socket) {
        String path = System.getProperty("user.dir") + source;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
        } catch (Exception e) {
            error(out);
        }
        try {
            if (path.contains(".html")) {
                String content = "", temp;
                while ((temp = br.readLine()) != null)
                    content += temp;
                br.close();
                handleGetRequest("200 OK", "text/html", out, content);
            } else if (path.contains(".jpg")) {
                BufferedImage image = ImageIO.read(new File(System.getProperty("user.dir") + source));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", bos);
                byte[] byteOSArray = bos.toByteArray();
                DataOutputStream outImg = new DataOutputStream(socket.getOutputStream());
                outImg.writeBytes("HTTP/1.1 200 OK \r\n");
                outImg.writeBytes("Content-Type: image/jpg\r\n");
                outImg.writeBytes("Content-Length: " + byteOSArray.length);
                outImg.writeBytes("\r\n\r\n");
                outImg.write(byteOSArray);
                outImg.close();
                out.println(outImg.toString());
            }
        } catch (Exception e) {
            error(out);
        }
    }

    private void handleGetRequest(String code, String mymeType, PrintWriter out, String content) {
        
        out.write("HTTP/1.1 " + code + "\r\n");
        out.write("Content-Type: " + mymeType + "\r\n");
        out.write("\r\n");
        out.write(content);
    }

    private void error(PrintWriter out) {
        out.println("HTTP/1.1 404 Not Found\r");
        out.println("Content-Type: text/html\r");
        out.println("\r");
    }
}