package com.yelp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class subsetGeneration {

	public static ArrayList<String> getBusinessIDs (String businessFilePath, String keyword) throws IOException, JSONException{
		File file = new File(businessFilePath);
		BufferedReader reader = new BufferedReader(new FileReader(file));

		ArrayList<String> ids = new ArrayList<String>();

		while (reader.ready()) {
			String line = reader.readLine();
			JSONObject obj = new JSONObject(line);

			JSONArray categories = obj.getJSONArray("categories");
			int num = categories.length();
			ArrayList<String> temp = new ArrayList<String>();
			for (int i = 0 ; i<num; i++){
				temp.add(categories.get(i).toString());
			}
			
			if(temp.contains(keyword)){
				String business_id = obj.getString("business_id");
				ids.add(business_id);
			}
	}
		reader.close();
		return ids;
	}

	public static ArrayList<String> getUserIDs (String reviewFilePath, ArrayList<String> businessIDs) throws IOException, JSONException {
		File file = new File(reviewFilePath);
		BufferedReader reader = new BufferedReader(new FileReader(file));

		ArrayList<String> ids = new ArrayList<String>();

		while (reader.ready()) {
			String line = reader.readLine();
			JSONObject obj = new JSONObject(line);

			String business_id = obj.getString("business_id");
			if(businessIDs.contains(business_id)){
				String user_id = obj.getString("user_id");
				ids.add(user_id);
			}
	}
		reader.close();
		return ids;
	}

	public static void getReviewSubset (String reviewFilePath, String outPath, ArrayList<String> businessIDs) throws IOException, JSONException{
		File file = new File(reviewFilePath);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		BufferedWriter output = new BufferedWriter(new FileWriter(outPath));

		while (reader.ready()) {
			String line = reader.readLine();
			JSONObject obj = new JSONObject(line);
			JSONStringer objOut = new JSONStringer();

			String business_id = obj.getString("business_id");
			if(businessIDs.contains(business_id)) {
				objOut.object();
				objOut.key("business_id");
				objOut.value(business_id);
				objOut.key("user_id");
				objOut.value(obj.getString("user_id"));
				objOut.key("text");
				objOut.value(obj.getString("text"));
				objOut.key("votes");
				objOut.value(obj.getJSONObject("votes"));
				objOut.endObject();
				output.append(objOut.toString() + "\n");
			}
		}
		reader.close();
		output.close();
	}

	public static void getUserSubset (String userFilePath, String outPath, ArrayList<String> userIDs) throws IOException, JSONException{
		File file = new File(userFilePath);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		BufferedWriter output = new BufferedWriter(new FileWriter(outPath));
		
		while (reader.ready()) {
			String line = reader.readLine();
			JSONObject obj = new JSONObject(line);
			JSONStringer objOut = new JSONStringer();

			String user_id = obj.getString("user_id");
			if(userIDs.contains(user_id)) {
				objOut.object();
				objOut.key("user_id");
				objOut.value(user_id);
				objOut.key("review_count");
				objOut.value(obj.getString("review_count"));
				objOut.key("votes");
				objOut.value(obj.getJSONObject("votes"));
				objOut.key("elite");
				objOut.value(obj.getJSONArray("elite"));
				objOut.endObject();
				output.append(objOut.toString() + "\n");
			}
		}
		reader.close();
		output.close();
	}
	
	public static void main(String[] args) throws IOException, JSONException {
		// TODO Auto-generated method stub

		String businessFilePath = "./data/yelp_academic_dataset_business.json";
		String keyword = "Restaurants";
		ArrayList<String> businessIDs = getBusinessIDs(businessFilePath,keyword);
		System.out.println("finish business ids");
		
		String reviewFilePath = "./data/yelp_academic_dataset_review.json";
		ArrayList<String> userIDs = getUserIDs(reviewFilePath, businessIDs);
		System.out.println("finish user ids");
		
		String reviewOutputPath = "./output/reviewSubset.json";
		String userFilePath = "./data/yelp_academic_dataset_user.json";
		String userOutputPath = "./output/userSubset.json";
		getReviewSubset(reviewFilePath, reviewOutputPath, businessIDs);
		System.out.println("get review subset");
		getUserSubset(userFilePath, userOutputPath, userIDs);
		System.out.println("get user subset");
}
}
