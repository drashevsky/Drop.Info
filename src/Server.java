import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

import com.sun.net.httpserver.*;

public class Server {
	
	private static final int cardIdLength = 8;
	
    public static void main(String[] args) throws FileNotFoundException, IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 100);
        
        server.createContext("/", (HttpExchange t) -> {
        	try {
        		String[] path = t.getRequestURI().getPath().split("/");
            	String lastPart = (path.length > 0) ? path[path.length - 1] : "";
            	
        		if (lastPart.length() == cardIdLength && lastPart.matches("[A-Za-z0-9]+")) {
            		String html = Files.readString(Paths.get("views/card.html"));
            		send(t, "text/html; charset=utf-8", html);
            	
        		} else if (lastPart.length() == 0 && !t.getRequestURI().toString().contains("//")) {
            		String html = Files.readString(Paths.get("views/cardform.html"));
            		send(t, "text/html; charset=utf-8", html);
            	
        		} else {
            		sendError(t, 404, "Error: page not found.");
            	}
        		
        	} catch (Exception e) {
        		System.out.println(e);
        		sendError(t, 500, "Error: server error.");
        	}
        });
        
        
        server.createContext("/static/", (HttpExchange t) -> {
        	try {
        		String path = t.getRequestURI().getPath();
        		String[] splitPath = path.split("\\.");
        		String contentType = (splitPath.length > 0) ? splitPath[splitPath.length - 1].toLowerCase() : "";
        		
        		if (contentType.equals("css")) {
        			send(t, "text/css", Files.readString(Paths.get("." + path)));
        		} else if (contentType.equals("js")) {
        			send(t, "text/javascript", Files.readString(Paths.get("." + path)));
        		} else {
        			sendError(t, 404, "Error: resource not found.");
        		}
        		
        	} catch (InvalidPathException e) {
        		System.out.println(e);
        		sendError(t, 404, "Error: resource not found.");
        	
        	} catch (NoSuchFileException e) {
        		System.out.println(e);
        		sendError(t, 404, "Error: resource not found.");
        		
        	} catch (FileNotFoundException e) {
        		System.out.println(e);
        		sendError(t, 404, "Error: resource not found.");
        		
        	} catch (AccessDeniedException e) {
        		System.out.println(e);
        		sendError(t, 403, "Error: access denied.");
        		
        	} catch (Exception e) {
        		System.out.println(e);
        		sendError(t, 500, "Error: server error.");
        	}
        });
        
        server.createContext("/share/", (HttpExchange t) -> {
        	try {
        		String[] path = t.getRequestURI().getPath().split("/");
            	String lastPart = (path.length > 0) ? path[path.length - 1] : "";
            	
        		if (lastPart.length() == cardIdLength && lastPart.matches("[A-Za-z0-9]+")) {
            		String html = Files.readString(Paths.get("views/share.html"));
            		send(t, "text/html; charset=utf-8", html);
            		
        		} else {
            		sendError(t, 404, "Error: page not found.");
            	}
        		
        	} catch (Exception e) {
        		System.out.println(e);
        		sendError(t, 500, "Error: server error.");
        	}
        });
        
        server.createContext("/img/", (HttpExchange t) -> {
        	send(t, "text/html; charset=utf-8", "oops");
        });
       
        
        server.createContext("/data/", (HttpExchange t) -> {
        	send(t, "application/json", "");
        });
        
        server.setExecutor(null);
        server.start();
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
    
    private static void sendError(HttpExchange t, int errorCode, String message) 
    		throws IOException, UnsupportedEncodingException, FileNotFoundException {
    	
    	String html = Files.readString(Paths.get("views/error.html"));
    	String result = String.format(html, errorCode, message);
    	
        t.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        byte[] response = result.getBytes("UTF-8");
        t.sendResponseHeaders(errorCode, response.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(response);
        }
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
}
