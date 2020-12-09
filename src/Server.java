import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

import com.sun.net.httpserver.*;

public class Server {
    private static final String QUERY_TEMPLATE = "{\"items\":[%s]}";

    public static void main(String[] args) throws FileNotFoundException, IOException {
        /*
        // Step 0: Initialize data for the algorithm
        Random random = new Random(1 + 0x43);
        // Create an HttpServer instance on port 8000 accepting up to 100 concurrent connections
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 100);
        // Return the index.html file when the browser asks for the web app
        server.createContext("/", (HttpExchange t) -> {
            String html = Files.readString(Paths.get("index.html"));
            send(t, "text/html; charset=utf-8", html);
        });
        // Return a list of suggestions for the given query string, s
        server.createContext("/query", (HttpExchange t) -> {
            String s = parse("s", t.getRequestURI().getQuery().split("&"));
            if (s.equals("")) {
                send(t, "application/json", String.format(QUERY_TEMPLATE, ""));
                return;
            }
            // Step 1: Return 10 randomly-generated strings
            send(t, "application/json", String.format(QUERY_TEMPLATE, json(result)));
        });
        */
        server.createContext("/random", (HttpExchange t) -> {
            // Step 2: Return a random non-terminal from the dataset
            send(t, "application/json", "{\"s\":\"" + result + "\"}");
        });
        server.setExecutor(null);
        server.start();
    }

    private static String parse(String key, String... params) {
        for (String param : params) {
            String[] pair = param.split("=");
            if (pair.length == 2 && pair[0].equals(key)) {
                return pair[1];
            }
        }
        return "";
    }

    private static void send(HttpExchange t, String contentType, String data)
            throws IOException, UnsupportedEncodingException {
        t.getResponseHeaders().set("Content-Type", contentType);
        byte[] response = data.getBytes("UTF-8");
        t.sendResponseHeaders(200, response.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(response);
        }
    }

    private static String json(Iterable<String> matches) {
        StringBuilder results = new StringBuilder();
        for (String s : matches) {
            if (results.length() > 0) {
                results.append(',');
            }
            results.append('"').append(s).append('"');
        }
        return results.toString();
    }

/*
    private static <E> E randomChoice(Collection<E> data, Random random) {
        Iterator<E> iter = data.iterator();
        for (int index = random.nextInt(data.size()); index > 0; index -= 1) {
            iter.next();
        }
        return iter.next();
    }
    */
}
