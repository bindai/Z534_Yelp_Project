package com.yelp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class nonTopicalFeatureGeneration {
	static final Pattern URL_PATTERN = Pattern
			.compile("^(http(s)?\\://)?[\\w\\.]+\\.\\w+(\\/.+)?$");
	static final int URL_MIN_LENGTH = 5;
	
	public static Set<String> getUrls(String[] words) {
		Set<String> urls = new HashSet<String>();
		for (String word : words) {
			if (word.length() >= URL_MIN_LENGTH
					&& URL_PATTERN.matcher(word).find()) {
				urls.add(word);
			}
		}
		return urls;
	}

	public static void urlDeletion(String filePath, String outPath, String outPath1) throws IOException, JSONException{
		File file = new File(filePath);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		BufferedWriter output = new BufferedWriter(new FileWriter(outPath));
		BufferedWriter output1 = new BufferedWriter(new FileWriter(outPath1));
		String text = "";
		int count=1;
		
		while (reader.ready()) {
			String line = reader.readLine();
			JSONObject obj = new JSONObject(line);
			JSONStringer objOut = new JSONStringer();
			text = obj.getString("text");
			
			int linksCount2 = text.split("http").length - 1;
			if(linksCount2 > 0){
				output1.write(count+" " + linksCount2 + " "+ "\n");
			}
			
			String[] words = text.split(" ");
			int linksCount = 0;
			for(int i = 0; i< words.length;i++){
				
				if(words[i].contains("http")){
					
					if(words[i].contains("\n")){
						words[i] = words[i].replace("\n", " ");
					}
					
					int lastIndex = words[i].length()-1;
					String lastStr = words[i].charAt(lastIndex) + "";
					if(lastStr.equals(".")){
						words[i] = words[i].substring(0, words[i].length()-1);
					}
					
					String[] temp = words[i].split(" ");
					for(int j=0; j<temp.length;j++){
						if(temp[j].contains("http")){
							linksCount++;
							text=text.replace(temp[j], " ");
						}
					}
				}
			}
			 
			objOut.object();
			objOut.key("review_id");
			objOut.value(count);
			objOut.key("business_id");
			objOut.value(obj.getString("business_id"));
			objOut.key("user_id");
			objOut.value(obj.getString("user_id"));
			objOut.key("text");
			objOut.value(text);
			objOut.key("linksCount");
			objOut.value(linksCount);
			objOut.key("votes");
			objOut.value(obj.getJSONObject("votes"));
			objOut.endObject();
			output.append(objOut.toString() + "\n");
			
			if(linksCount>0){
				output1.write(count+" " + linksCount + " "+ "\n");
			}
			count++;
		}
		reader.close();
		output.close();
		output1.close();
		
	}

	public static void featureStat(String userFile, String filePath, String outPath) throws IOException,
			JSONException {
		
		/**********user information generation ************/
		File uFile = new File(userFile);
		BufferedReader userReader = new BufferedReader(new FileReader(uFile));
		HashMap<String, Integer> reviewCount = new HashMap<String, Integer>();
		HashMap<String, Integer> eliteCount = new HashMap<String, Integer>();
		
		while (userReader.ready()) {
			String userLine = userReader.readLine();
			JSONObject userObj = new JSONObject(userLine);
			
			String user_id = userObj.getString("user_id");
			int reviewNum = userObj.getInt("review_count");
			
			int eliteNum = 0;
			JSONArray elites = userObj.getJSONArray("elite");
			if(elites.length()>0){
				eliteNum = 1;
			}
			
			reviewCount.put(user_id, reviewNum);
			eliteCount.put(user_id, eliteNum);
		}
		userReader.close();
		
		/**********review information generation ************/
		File file = new File(filePath);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		BufferedWriter output = new BufferedWriter(new FileWriter(outPath));
		int count= 1;
		
		while (reader.ready()) {
			int pCount = 0;
			int sCount = 0;
			int wCount = 0;
		
			String line = reader.readLine();
			JSONObject obj = new JSONObject(line);
			String text = obj.getString("text");
	
			String[] subText = text.split("\n");
			for (int i = 0; i < subText.length; i++) {
				if (!subText[i].equals("")) {
					pCount++;
					String[] sentences = subText[i].split("\\.|\\!|\\?");

					for (int j = 0; j < sentences.length; j++) {
						if (!sentences[j].equals(" ")
								&& !sentences[j].equals("")) {
							sCount++;
							String[] words = sentences[j].split(" ");

							for (int k = 0; k < words.length; k++) {
								if (!words[k].equals("")) {
									wCount++;
								}
							}
						}
					}
				}
			}
			
			/**********output file generation ************/
			JSONObject votes = obj.getJSONObject("votes");
			int coolCount = votes.getInt("cool");
			int funnyCount = votes.getInt("funny");
			int usefulCount = votes.getInt("useful");
			
			output.write(obj.getInt("review_id") + "," + coolCount + "," + funnyCount + "," + usefulCount + ",");
			output.write(pCount + "," + sCount + "," + (float)wCount/sCount + "," + obj.getInt("linksCount") + ",");
			
			
			String reviewUserId = obj.getString("user_id");
			output.write(reviewCount.get(reviewUserId) + "," + eliteCount.get(reviewUserId) + "\n");
			
			
			System.out.println(count);
			count++;
			
		}
		
		reader.close();
		output.close();
	}

	public static void recordDeletion(String filePath, String outPath) throws IOException{
		File file = new File(filePath);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		BufferedWriter output = new BufferedWriter(new FileWriter(outPath));
		
		while (reader.ready()) {
			String line = reader.readLine();
			String[] items = line.split(",");
			
			if(!(items[1].equals("0") && items[2].equals("0") && items[3].equals("0"))){
				output.write(line + "\n");
			}
		}
		
		reader.close();
		output.close();
	}
	
	public static void main(String[] args) throws IOException, JSONException {
		// TODO Auto-generated method stub
		
		//urlDeletion("./output/reviewSubset.json","./Output/reviewSubsetWithoutUrls.json","./Output/urlsLine.csv");
		
		//featureStat("./output/userSubset.json","./output/reviewSubsetWithoutUrls.json", "./output/nonTopicalFeatures.csv");
		
		recordDeletion("./output/reviewUser/nonTopicalFeatures.csv","./output/reviewUser/nonTopicalFeaturesSubset.csv");
	}

}
