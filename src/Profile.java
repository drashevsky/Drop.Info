// Kavi Gill, Daniel Rashevsky, Joel Renish
// CSE 143 Final Project
// Handles storage of images and post (known as cards) on the disk.

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.nio.file.*;
import java.util.Random;

public class Profile {
	
	private static final int ID_LENGTH = 8;

	private String profileId; 				// Unique identifier for individual card
	private String title;
	private String content;					// Markdown content of individual card
	private String imageId;
	private String isTextConstrained;		// Special formatting setting for display of card
	private static Random rand;

	
	
	// Takes data in JSON format and constructs it into Profile object. 
	// Throws exception if can't read or parse data.
	public Profile(String json) throws FileNotFoundException, IOException, ParseException {
		Object obj = new JSONParser().parse(new StringReader(json));
		JSONObject jo = (JSONObject) obj;
		
		this.title = (String) jo.get("title");
		this.content = (String) jo.get("content");
		this.imageId = (String) jo.get("imageId");
		this.profileId = (String) jo.get("profileId");
		this.isTextConstrained = (String) jo.get("isTextConstrained");
		this.rand = new Random();
		
	}

	// Takes in String data about title, content, image name, and a text constrained
	// display setting and constructs it into a Profile object.
	public Profile(String title, String content, String imageId, String isTextConstrained) {
		this.title = title;
		this.content = content;
		this.imageId = imageId;
		this.isTextConstrained = isTextConstrained;
		this.rand = new Random();
		this.profileId = generateRandomId();
	}

	//Generates a random alphanumeric id of ID_LENGTH length,
	//which it returns as a String.
	private static String generateRandomId() {
		String id = "";

		for (int i = 0; i < ID_LENGTH; i++) {
			int num = rand.nextInt(36);

			if (num <= 25) {
				id += (char) ('a' + num);
			} else {
				id += (char) ('0' + (num - 26));
			}
		}

		return id;
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
	
	public String getProfileId() {
		return profileId;
	}
	
	
	
	//Given a alphanumeric String profile id, parses and returns a Profile object from 
	//its corresponding JSON flatfile on disk. Throws an exception if the disk 
	//or parse operation failed.
	public static Profile loadProfile(String profileId) throws Exception {
		return loadProfileFromJSON("Profile" + profileId.toLowerCase() + ".json");
	}

	//Given a String filename for a JSON flatfile, parses and returns its Profile object.
	//Throws an exception if the disk or parse operation failed.
	public static Profile loadProfileFromJSON(String fileName) throws Exception {
		return loadProfileFromJSON(fileName, "profiles/");
	}

	//Given a String filename and path for a JSON flatfile, parses and returns its Profile object.
	//Throws an exception if the disk or parse operation failed.
	public static Profile loadProfileFromJSON(String fileName, String path) throws Exception {
		String profileData = readFileAsString(Paths.get(path).resolve(fileName).toString());
		return new Profile(profileData);
	}

	//Given a alphanumeric String profile id, returns a String containing its JSON data from 
	//a corresponding flatfile on disk. Throws an exception if the disk operation failed.
	public static String readProfileJSONString(String profileId) throws Exception {
		Path p = Paths.get("profiles/").resolve("Profile" + profileId.toLowerCase() + ".json");
		return readFileAsString(p.toString());
	}
	
	//Given a String filepath, returns a String containing its contents on disk. 
	//Throws an exception if the disk operation failed.
	public static String readFileAsString(String filePath) throws Exception {
		return new String(Files.readAllBytes(Paths.get(filePath)));
	}
	
	//Given a alphanumeric String profile id, checks if the corresponding Profile exists on disk
	//and returns a boolean. Throws an exception if the disk operation failed.
	public static boolean exists(String profileId) throws Exception {
		Path p = Paths.get("profiles/").resolve("Profile" + profileId.toLowerCase() + ".json");
		return Files.exists(Paths.get(p.toString()));
	}

	//Saves this profile to a JSON flatfile on disk. Throws an exception if the
	//disk operation fails. Returns a reference to the saved Profile.
	public Profile saveProfileToJSON() throws FileNotFoundException {
		return saveProfileToJSON("Profile" + profileId + ".json", "profiles/");
	}

	//Saves this profile to a JSON flatfile on disk, given a String filename and path to save to. 
	//Throws an exception if the disk operation fails. Returns a reference to the saved Profile.
	public Profile saveProfileToJSON(String fileName, String path) throws FileNotFoundException {
		JSONObject jo = new JSONObject();

		jo.put("profileId", profileId);
		jo.put("title", title);
		jo.put("content", content);
		jo.put("imageId", imageId);
		jo.put("isTextConstrained", isTextConstrained);

		Path p = Paths.get(path);

		PrintWriter pw = new PrintWriter(p.resolve(fileName).toString());

		pw.write(jo.toJSONString());

		pw.flush();
		pw.close();
		return this;
	}

	
	
	//Saves an image to disk under this profile's name given a byte array of the image data 
	//and a format to save in. If no format is given, saves as PNG. Throws an exception if the
	//image conversion or disk operation fails.
	public void saveImage(byte[] imageData, String format) throws IOException {
		if (format == null) {
			format = "png";
		}
		
		saveImage(imageData, format, "image" + profileId + "." + format, "images/");
	}

	//Saves an image to disk under this profile's name given a byte array of the image data,
	//a format to save in, and a String filename and path. Sets the filename to this Profile's
	//imageId. Throws an exception if the image conversion or disk operation fails.
	public void saveImage(byte[] imageData, String format, String fileName, String path) throws IOException {
		InputStream is = new ByteArrayInputStream(imageData);
		BufferedImage newBi = ImageIO.read(is);
		
		Path p = Paths.get(path);
		ImageIO.write(newBi, format, p.resolve(fileName).toFile());
		
		this.imageId = fileName;
	}

	//Retrieves this Profile's corresponding image, which is returned as a byte array. Throws an
	//exception of the disk or image processing operation fails.
	public byte[] loadImage() throws IOException {
		return loadImage(imageId, "images/");
	}

	//Retrieves this Profile's corresponding image, given a String filename and path. The image is 
	//returned as a byte array. Throws an exception of the disk or image processing operation fails.
	public static byte[] loadImage(String fileName, String path) throws IOException {
		Path p = Paths.get(path);
		p = p.resolve(fileName);
		BufferedImage newBi = ImageIO.read(p.toFile());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(newBi, "png", baos);
		byte[] bytes = baos.toByteArray();

		return bytes;
	}

	
	
	//Returns a String representation of this Profile.
	@Override
	public String toString() {
		return "Profile [profileId=" + profileId 
					+ ", title=" + title 
					+ ", content=" + content 
					+ ", imageId=" + imageId 
					+ ", isTextConstrained=" + isTextConstrained + "]";
	}

	//Runs a series of tests to ensure the Profile class works. 
	//Throws an exception if any tests fail.
	public static void main(String[] args) throws Exception {
		rand = new Random(100);
		Profile p = new Profile("{\n" + "  \"title\": \"my First Card\",\n" + 
										"  \"content\": \"data that goes on a card\",\n" + 
										"  \"imageId\": \"logo.png\"\n" + 
										"  \"isTextConstrained\": \"true\",\n" + 
										"  \"profileId\": \"100\"\n" + "}");
		System.out.println(p);
		p.saveProfileToJSON();

		Profile p2 = new Profile("New Card", "data", null, "true");

		System.out.println(p2);
		p2.saveProfileToJSON();

		Profile p3 = Profile.loadProfileFromJSON("Profilehkkah4uu.json");
		p3.setTitle("new title");
		p3.saveProfileToJSON();

		Path source = Paths.get("images/image0.png");
		BufferedImage bi = ImageIO.read(source.toFile());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bi, "png", baos);
		byte[] bytes = baos.toByteArray();

		p3.saveImage(bytes, "png");
		p3.saveProfileToJSON();

		for (int i = 0; i < 10; i++) {
			System.out.println(generateRandomId());
		}
	}
}
