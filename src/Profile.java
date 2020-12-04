import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedHashMap; 
import java.util.Map;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 

import java.util.Iterator; 
import java.util.Map; 
  
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.*; 
import java.nio.file.*;

public class Profile {
	 private String profileId; // Unique identifier for individual profile
	 private String title;
	 private String content; 
	 private String imageId;
	 
	 private static int nextIDnumber = 0; // gives Id for next instance of profile
	 
	 // Takes data in a string format and parses it into object
	 public Profile (String json) throws FileNotFoundException, IOException, ParseException {
		 Object obj = new JSONParser().parse(new StringReader(json));
		 JSONObject jo = (JSONObject) obj;
		 this.title = (String) jo.get("title");
		 this.content = (String) jo.get("content");
		 this.imageId = (String) jo.get("imageId");
		 this.profileId = (String) jo.get("profileId");
		 
		 
		 
		 
		 
	 }
	 // General constructor
	 public Profile (String title, String content, String imageId) {
		 this.title = title;
		 this.content = content;
		 this.imageId = imageId;
		 profileId = ""+ nextIDnumber;
		 nextIDnumber++;
		 
	 }
	 
	 // This is for loading file from JSON (wrapper)
	 public static Profile loadProfileFromJSON (String fileName) throws Exception {
		 return loadProfileFromJSON(fileName, "Profiles/");
	 }
	 
	 // loading file
     public static Profile loadProfileFromJSON (String fileName, String path) throws Exception {
    	 Path p = Paths.get(path);
    	 
		 String profileData = readFileAsString(p.resolve(fileName).toString());
		 return new Profile(profileData);
	 }
     
     public static String readFileAsString(String fileName) throws Exception 
     { 
       String data = ""; 
       data = new String(Files.readAllBytes(Paths.get(fileName))); 
       return data; 
     } 
     
     // This is for saving file to JSON (wrapper)
  	 public Profile saveProfileToJSON () throws FileNotFoundException {
  		return saveProfileToJSON("Profile" + profileId + ".json", "Profiles/");
  		
  	 }
  	 
  	 
  	 
    public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	// saving file to JSON (wrapper)
  	 public Profile saveProfileToJSON(String fileName, String path) throws FileNotFoundException {
  		 JSONObject jo = new JSONObject();
  		 
  		 jo.put("profileId", profileId);
  		 jo.put("title", title);
  		 jo.put("content", content);
  		 jo.put("imageId", imageId);
  		 
  		 Path p = Paths.get(path);
  		 
  		 PrintWriter pw = new PrintWriter(p.resolve(fileName).toString());
  		 
  		 pw.write(jo.toJSONString()); 
  		 
  		 pw.flush(); 
         pw.close(); 
         return this;
  		  
  	 }
  	 
  	 
  	 
	 
	 
	// saves/stores image
    public void saveImage(byte[] imageData, String format) throws IOException {
    	if (format == null) {
    		format = "png";
    	}
    	saveImage(imageData, format, "image" + profileId + "." + format, "images/" );
    }
    
    public void saveImage(byte[] imageData, String format, String fileName, String path) throws IOException {
    	InputStream is = new ByteArrayInputStream(imageData);
    	BufferedImage newBi = ImageIO.read(is);
    	
    	Path p = Paths.get(path);
    	
    	ImageIO.write(newBi, format, p.resolve(fileName).toFile());
    	
    	this.imageId = fileName;
    	
    }
    
    // Retrieving image
    public byte[] loadImage() throws IOException {
    	return loadImage(imageId, "images/");
    }
    
    public byte[] loadImage(String fileName, String path) throws IOException {
    	Path p = Paths.get(path);
    	p = p.resolve(fileName);
    	BufferedImage newBi = ImageIO.read(p.toFile());
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(newBi, "png", baos);
		byte[] bytes = baos.toByteArray();
		
		return bytes;
    	
    	
    	
    }
    
    
	





	@Override
	public String toString() {
		return "Profile [profileId=" + profileId + ", title=" + title + ", content=" + content + ", imageId=" + imageId
				+ "]";
	}
	
	public static void main(String[] args) throws Exception {
		Profile p = new Profile("{\n"
				+ "  \"title\": \"my First Card\",\n"
				+ "  \"content\": \"data that goes on a card\",\n"
				+ "  \"imageId\": \"logo.png\"\n"
				+ "  \"profileId\": \"100\"\n"
				+ "}"); 
		System.out.println(p);
		p.saveProfileToJSON();
		
		Profile p2 = new Profile("New Card", "data", null);
		
		System.out.println(p2);
		p2.saveProfileToJSON();
		
		Profile p3 = Profile.loadProfileFromJSON("Profile0.json");
		p3.setTitle("new title");
		p3.saveProfileToJSON();
		
		Path source = Paths.get("testimage.png");
		BufferedImage bi = ImageIO.read(source.toFile());
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bi, "png", baos);
		byte[] bytes = baos.toByteArray();
		
		p3.saveImage(bytes, "png");
		p3.saveProfileToJSON();

	}

}
