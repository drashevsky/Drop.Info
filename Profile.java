import java.io.FileNotFoundException; 
import java.io.PrintWriter; 
import java.util.LinkedHashMap; 
import java.util.Map; 
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator; 
import java.util.Map; 
  
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.*; 
import java.io.StringReader;
import java.io.StringWriter;

public class Profile {
	 private String title;
	 private String content; 
	 private String imageId;
	 
	 // Takes data in a string format and parses it into object
	 public Profile (String json) throws FileNotFoundException, IOException, ParseException {
		 Object obj = new JSONParser().parse(new StringReader(json));
		 JSONObject jo = (JSONObject) obj;
		 this.title = (String) jo.get("title");
		 this.content = (String) jo.get("content");
		 this.imageId = (String) jo.get("imageId");
		 
		 
		 
		 
	 }
	 
	 
	 
	

	@Override
	public String toString() {
		return "Profile [title=" + title + ", content=" + content + ", imageId=" + imageId + "]";
	}





	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		Profile p = new Profile("{\n"
				+ "  \"title\": \"my First Card\",\n"
				+ "  \"content\": \"data that goes on a card\",\n"
				+ "  \"imageID\": \"logo.png\"\n"
				+ "}"); 
		System.out.println(p);

	}

}
