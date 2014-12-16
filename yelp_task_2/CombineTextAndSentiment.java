package com.yelp_2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class CombineTextAndSentiment {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		BufferedReader bReader_1 = new BufferedReader(new FileReader("./emoji_updated.csv"));
		HashMap<String, String> emoji = new HashMap<String, String>();
		while(bReader_1.ready()){
			String[] line = bReader_1.readLine().split(",");
			emoji.put(line[0], line[1]);
		}
		bReader_1.close();
		
		BufferedReader bReader_2 = new BufferedReader(new FileReader("./sentiment_updated.csv"));
		HashMap<String, String> sentiment = new HashMap<String, String>();
		while(bReader_2.ready()){
			String[] line = bReader_2.readLine().split(",");
			sentiment.put(line[0], line[1]);
			
		}
		bReader_2.close();
		
		BufferedReader bReader = new BufferedReader(new FileReader("./text/reviewUser/nonTopicalFeaturesSubset.csv"));
		BufferedWriter bWriter = new BufferedWriter(new FileWriter("./text/reviewUser/nonTopicalAndSentimentFeatures_updated.csv"));
		int count =0;
		while(bReader.ready()){
			System.out.println(count++);
			String[] line = bReader.readLine().split(",");
			String id = line[0];
			String coolCount = line[1];
			String funnyCount = line[2];
			String usefulCount = line[3];
			int countVote = Integer.parseInt(coolCount)+Integer.parseInt(funnyCount)+Integer.parseInt(usefulCount);
			String paragraphCount = line[4];
			String sentenceCount = line[5];
			String averageLengthofSentence = line[6];
			String linkCount = line[7];
			String userReviewCount = line[8];
			String eliteorNot = line[9];
			String emojiCount = emoji.get(id);
			String sentimentCount = sentiment.get(id);
			bWriter.append(id+",");
			bWriter.append(countVote+",");
			bWriter.append(coolCount+",");
			bWriter.append(funnyCount+",");
			bWriter.append(usefulCount+",");
			bWriter.append(paragraphCount+",");
			bWriter.append(sentenceCount+",");
			bWriter.append(averageLengthofSentence+",");
			bWriter.append(linkCount+",");
			bWriter.append(userReviewCount+",");
			bWriter.append(eliteorNot+",");
			bWriter.append(emojiCount+",");
			bWriter.append(sentimentCount+"\n");
			
		}
		bReader.close();
		bWriter.close();

	}

}
