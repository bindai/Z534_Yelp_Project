package com.yelp_2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import org.json.JSONException;
import org.json.JSONObject;

public class ExtractEmojiAndSentiment {
	
	public static void extractEmoji(String url) throws IOException, JSONException{

		// TODO Auto-generated method stub
		
		String reviewFilePath = url;
		
		BufferedReader bReader = new BufferedReader(new FileReader(reviewFilePath));
		BufferedWriter bWriter = new BufferedWriter(new FileWriter("./emoji_updated.csv"));
		int count =0;

		while(bReader.ready()){
			System.out.println(count++);
			String line = bReader.readLine();
			JSONObject obj = new JSONObject(line);

			String text = obj.getString("text");
			String[] word = text.split(" ");
			int value = 0;
			for(int i = 0; i < word.length; i++){
				if(word[i].startsWith("=)")||word[i].startsWith(":-)")||word[i].startsWith(":)")||word[i].startsWith(":]")||word[i].startsWith(":D")||word[i].startsWith(":-D")||word[i].startsWith(":=D")){
					value++;
				}else if(word[i].startsWith("=(")||word[i].startsWith(":-(")||word[i].startsWith(":(")||word[i].startsWith(":[")||word[i].startsWith(":-O")||word[i].startsWith(":O")||word[i].startsWith(":-o")||word[i].startsWith(":o")||word[i].startsWith(":'(")){
					value++;
				}
			}

			bWriter.write(count+","+value+"\n");
		}
		bReader.close();
		bWriter.close();
	
		
	}
	
public static void extractSentiment(String url) throws IOException, JSONException{
		

		// TODO Auto-generated method stub
		
		String reviewFilePath = url;
		
		BufferedReader bReader = new BufferedReader(new FileReader(reviewFilePath));
		BufferedWriter bWriter = new BufferedWriter(new FileWriter("./sentiment_updated.csv"));
		
		BufferedReader bReader_1 = new BufferedReader(new FileReader("./positive-words.txt"));
		HashSet<String> positive = new HashSet<String>();
		
		while(bReader_1.ready()){
			String line = bReader_1.readLine();
			positive.add(line);	
		}
		bReader_1.close();
		
		BufferedReader bReader_2 = new BufferedReader(new FileReader("./negative-words.txt"));
		
		
		HashSet<String> negative = new HashSet<String>();
		
		while(bReader_2.ready()){
			String line = bReader_2.readLine();
			negative.add(line);	
		}
		bReader_2.close();
		
		
		int count =0;
		while(bReader.ready()){
			System.out.println(count++);
			String line = bReader.readLine();
			JSONObject obj = new JSONObject(line);

			String text = obj.getString("text");
			String[] word = text.split(" ");
			int value = 0;
			for(int i = 0; i < word.length; i++){
				if(positive.contains(word[i].toLowerCase())){
					value++;
				}else if(negative.contains(word[i].toLowerCase())){
					value++;
				}
			}
			
			bWriter.write(count+","+value+"\n");
		}
		bReader.close();
		bWriter.close();
	
		
	}

	public static void main(String[] args) throws IOException, JSONException {
		// TODO Auto-generated method stub
		
		String reviewFilePath = "./data/reviewSubset.json";
		extractEmoji(reviewFilePath);
		extractSentiment(reviewFilePath);
		
	}

}
