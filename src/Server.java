// Kavi Gill, Daniel Rashevsky, Joel Renish
// CSE 143 Final Project
// Server that hosts Drop.Info webapp and API.

import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.lang.*;

import com.sun.net.httpserver.*;
import org.apache.commons.io.*;
import org.apache.commons.lang3.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Server {
	
	private static final int[] CONTENTLENGTHS = {900, 1500, 1600, 2800};
	private static final int PORT = 80;
	private static final int NUMCONNECTIONS = 100;
	
    public static void main(String[] args) throws FileNotFoundException, IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), NUMCONNECTIONS);
        
        //API endpoint which returns either the HTML card creation form or page for a 
        //specific card, given the request URI is valid. Replies with error pages for
        //a server failure, or if the URI is invalid or doesn't exist.
        server.createContext("/", (HttpExchange t) -> {
        	try {
        		
        		//Get the last part of the URI -> drop.info/(lastPart)
        		String[] path = t.getRequestURI().getPath().split("/");
            	String lastPart = (path.length > 0) ? path[path.length - 1] : "";
            	
            	//Return page of specific card
        		if (Profile.exists(lastPart) && !t.getRequestURI().toString().contains("//")) {
            		String html = Files.readString(Paths.get("views/card.html"));
            		send(t, "text/html; charset=utf-8", html);
            	
            	//Return card creation form
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
        
        //API endpoint which accepts a post request containing JSON with a new card, before parsing
        //and saving the card. A valid JSON request body consists of a title string between 0 - 100 
        //characters, a content string with variable length depending on the other items, a base64 
        //image dataurl, and a string containing a text display setting boolean. Replies with error 
        //messages if the request or URI is invalid, or if there was a server failure.
        server.createContext("/create", (HttpExchange t) -> {
        	try {
        		
        		//Verify URI
        		if (t.getRequestURI().getPath().equals("/create") && !t.getRequestURI().toString().contains("//")) {
        			
        			//Parse JSON
        			String json = receiveBody(t.getRequestBody(), StandardCharsets.UTF_8);
        			JSONObject jo = (JSONObject) new JSONParser().parse(new StringReader(json));
        			
        			String title = (String) jo.get("title");
        			String content = (String) jo.get("content");
        			String imageData = (String) jo.get("image");
        			boolean isTextConstrained = (boolean) jo.get("isTextConstrained");
        			
        			//Decode image from data URL
        			String data = "", fileType = "";
        			byte[] image = new byte[0];
        			
        			if (imageData.length() > 0) {
        				data = imageData.substring(imageData.indexOf(",") + 1);
        				fileType = imageData.substring(imageData.indexOf("/") + 1, imageData.indexOf(";"));
        				image = Base64.getDecoder().decode(data);
        				
        				//Verify if filetype is supported
        				if (!(fileType.equals("jpeg") || fileType.equals("png") || fileType.equals("gif"))) {
            				sendData(t, 400, "Error: unsupported image filetype.");
            				return;
        				}
        			}
        			
        			//Verify title and content
        			if (title.length() > 100 || title.length() <= 0) {
        				sendData(t, 400, "Error: no title or title too long.");
        			} else if (content.length() <= 0) {
        				sendData(t, 400, "Error: no content.");
        			} else if (isTextConstrained && data.length() > 0 && content.length() > CONTENTLENGTHS[0]) {
        				sendData(t, 400, "Error: content too long.");
        			} else if (isTextConstrained && !(data.length() > 0) && content.length() > CONTENTLENGTHS[1]) {
        				sendData(t, 400, "Error: content too long.");
        			} else if (!isTextConstrained && data.length() > 0 && content.length() > CONTENTLENGTHS[2]) {
        				sendData(t, 400, "Error: content too long.");
        			} else if (!isTextConstrained && !(data.length() > 0) && content.length() > CONTENTLENGTHS[3]) {
        				sendData(t, 400, "Error: content too long.");
        			
        			//Save new card
        			} else {
        				title = StringEscapeUtils.escapeHtml4(title);
        				content = StringEscapeUtils.escapeHtml4(content);
        				
        				Profile p = new Profile(title, content, null, (isTextConstrained) ? "true" : "false");
        				if (image.length > 0) {
        					p.saveImage(image, fileType);
        				}
        				p.saveProfileToJSON();
        				
        				sendData(t, 200, p.getProfileId());
        			}
        			
        		} else {
        			sendError(t, 404, "Error: page not found.");
        		}
        		
        	} catch (Exception e) {
        		System.out.println(e);
        		sendData(t, 500, "Error: server error.");
        	}
        });
        
        //API endpoint which returns JSON data given a string card id (profileId), if the request URI is valid. 
        //Replies with error pages for a server failure, or if the card URI is invalid or doesn't exist.
        server.createContext("/data/", (HttpExchange t) -> {
        	try {
        		
        		//Get the last part of the URI -> drop.info/(profileId)
        		String[] path = t.getRequestURI().getPath().split("/");
            	String lastPart = (path.length > 0) ? path[path.length - 1] : "";
            	
            	//Verify if card exists and send JSON response
        		if (Profile.exists(lastPart) && !t.getRequestURI().toString().contains("//")) {
            		send(t, "application/json", Profile.readProfileJSONString(lastPart));
        		} else {
            		sendError(t, 404, "Error: page not found.");
            	}
        		
        	} catch (Exception e) {
        		System.out.println(e);
        		sendError(t, 500, "Error: server error.");
        	}
        });
        
        //API endpoint which returns a static css or js file, if the request URI is valid. Replies 
        //with error pages for a server failure, or if the request URI is invalid or doesn't exist.
        server.createContext("/static/", (HttpExchange t) -> {
        	try {
        		
        		//Get file URI
        		String path = t.getRequestURI().getPath();
        		String[] splitPath = path.split("\\.");
        		String contentType = (splitPath.length > 0) ? splitPath[splitPath.length - 1].toLowerCase() : "";
        		
        		//Check content type and send file
        		if (contentType.equals("css")) {
        			send(t, "text/css", Files.readString(Paths.get("." + path)));
        		} else if (contentType.equals("js")) {
        			send(t, "text/javascript", Files.readString(Paths.get("." + path)));
        		} else {
        			sendError(t, 404, "Error: page not found.");
        		}
        	
        	} catch (AccessDeniedException e) {
        		System.out.println(e);
        		sendError(t, 403, "Error: access denied.");
        	
        	} catch (InvalidPathException | IOException e) {
        		System.out.println(e);
        		sendError(t, 404, "Error: page not found.");
        		
        	} catch (Exception e) {
        		System.out.println(e);
        		sendError(t, 500, "Error: server error.");
        	}
        });
        
        //API endpoint which returns a HTML page allowing a certain card to be shared, if the 
        //request URI is valid. Replies with error pages for a server failure, or if the card 
        //URI is invalid or doesn't exist.
        server.createContext("/share/", (HttpExchange t) -> {
        	try {
        		
        		//Get the last part of the URI -> drop.info/(profileId)
        		String[] path = t.getRequestURI().getPath().split("/");
            	String lastPart = (path.length > 0) ? path[path.length - 1] : "";
            	
            	//Verify URI and send share page
        		if (Profile.exists(lastPart) && !t.getRequestURI().toString().contains("//")) {
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
        
        //API endpoint which returns an image file in a supported format, if the request URI is
        //valid. Replies with error pages for a server failure, or if the request URI 
        //is invalid or doesn't exist.
        server.createContext("/img/", (HttpExchange t) -> {
        	try {
        		
        		//Get image URI
        		String[] path = t.getRequestURI().getPath().split("/");
            	String lastPart = (path.length > 0) ? path[path.length - 1] : "";
            	byte[] imageData = Profile.loadImage(lastPart, "images/");
            	
            	//Get image filetype
            	String[] splitName = lastPart.split("\\.");
        		String contentType = (splitName.length > 0) ? splitName[splitName.length - 1].toLowerCase() : "";
        		
        		//Verify content type and send image
        		if (contentType.equals("png")) {
        			sendImage(t, "image/png", imageData);
        		} else if (contentType.equals("jpg") || contentType.equals("jpeg")) {
        			sendImage(t, "image/jpeg", imageData);
        		} else if (contentType.equals("gif")) {
        			sendImage(t, "image/gif", imageData);
        		} else {
        			sendError(t, 404, "Error: page not found.");
        		}
        		
        	} catch (AccessDeniedException e) {
        		System.out.println(e);
        		sendError(t, 403, "Error: access denied.");
        	
        	} catch (InvalidPathException | IOException e) {
        		System.out.println(e);
        		sendError(t, 404, "Error: page not found.");
        		
        	} catch (Exception e) {
        		System.out.println(e);
        		sendError(t, 500, "Error: server error.");
        	}
        });
       
        //API endpoint which returns a page explaining what Drop.Info is, if the request URI is
        //valid. Replies with error pages for a server failure, or if the request URI is invalid.
        server.createContext("/what", (HttpExchange t) -> {
        	try {
        		
        		//Verify URI and send page
        		if (t.getRequestURI().getPath().equals("/what") && !t.getRequestURI().toString().contains("//")) {
        			send(t, "text/html; charset=utf-8", Files.readString(Paths.get("views/whatsthis.html")));
        		} else {
        			sendError(t, 404, "Error: page not found.");
        		}
        		
        	} catch (Exception e) {
        		System.out.println(e);
        		sendError(t, 500, "Error: server error.");
        	}
        });
        
        server.setExecutor(null);
        server.start();
    }

    //Sends HTTP data with 200 status, given an HttpExchange to respond to, as well as the
    //text and content type of the data. Throws exceptions if encoding or transmission fails.
    private static void send(HttpExchange t, String contentType, String data) 
    		throws IOException, UnsupportedEncodingException {
        
    	t.getResponseHeaders().set("Content-Type", contentType);
        byte[] response = data.getBytes("UTF-8");
        t.sendResponseHeaders(200, response.length);
        
        try (OutputStream os = t.getResponseBody()) {
            os.write(response);
        }
    }
    
    //Formats and sends an HTML error page, given an HttpExchange to respond to, as well as the
    //error message String to transmit and integer error status code. Throws exceptions if encoding 
    //or transmission fails.
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
    
    //Sends a plain text HTTP message, given an HttpExchange to respond to, as well as the
    //integer HTTP status and message String. Throws exceptions if encoding or transmission fails.
    private static void sendData(HttpExchange t, int code, String message) 
    		throws IOException, UnsupportedEncodingException, FileNotFoundException {
        t.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        byte[] response = message.getBytes("UTF-8");
        t.sendResponseHeaders(code, response.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(response);
        }
    }
    
    //Sends an image over HTTP with a 200 status, given an HttpExchange to respond to, as well as 
    //the image byte array and content type. Throws exceptions if encoding or transmission fails.
    private static void sendImage(HttpExchange t, String contentType, byte[] imageData) 
    		throws IOException, UnsupportedEncodingException {
        
    	t.getResponseHeaders().set("Content-Type", contentType);
        t.sendResponseHeaders(200, imageData.length);
        
        try (OutputStream os = t.getResponseBody()) {
            os.write(imageData);
        }
    }

    //Receives, decodes, and returns a request body String from an HttpExchange, given the
    //request body's InputStream and the charset to decode to. Throws an exception if the
    //decoding fails.
    private static String receiveBody(InputStream body, Charset charset) throws IOException {
    	StringWriter writer = new StringWriter();
    	IOUtils.copy(body, writer, charset);
    	return writer.toString();
    }
}
