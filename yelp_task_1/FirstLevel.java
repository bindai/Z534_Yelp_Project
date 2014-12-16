package com.yelp_1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Stopwords;
import weka.core.converters.ArffSaver;

public class FirstLevel {
	final static String[] categories = { "Restaurants", "Nightlife",
			"Shopping", "Food", "Health_Medical", "Beauty_Spas",
			"Home_Services", "Local_Services", "Event_Planning_Services",
			"Arts_Entertainment", "Active_Life", "Education", "Automotive",
			"Financial_Services", "Hotels_Travel", "Local_Flavor",
			"Mass_Media", "Pets", "Professional_Services",
			"Public_Services_Government", "Real_Estate",
			"Religious_Organizations" };

	public static void main(String[] args) throws IOException, JSONException {
		// TODO Auto-generated method stub
		System.out.println(categories.length);
		HashMap<String, Integer> categoriesMap = new HashMap<String, Integer>();
		for (int i = 0; i < categories.length; i++) {
			categoriesMap.put(categories[i], i);
		}

		BufferedReader bReader = new BufferedReader(new FileReader(
				"./output/collectedData.json"));
		ArrayList<Attribute> atts;
		ArrayList<String> attVals;
		Instances data;
		double[] vals;

		attVals = new ArrayList<String>();
		attVals.add("0");
		attVals.add("1");

		// 1. set up attributes
		atts = new ArrayList<Attribute>(2 + categories.length);
		atts.add(new Attribute("business_id", (ArrayList<String>) null));
		atts.add(new Attribute("text", (ArrayList<String>) null));
		for (int i = 0; i < categories.length; i++)
			atts.add(new Attribute(categories[i], attVals));

		// 2. create Instances object
		data = new Instances("MyRelation", atts, 0);
		int count = 0;
		while (bReader.ready()) {
			System.out.println(count++);

			vals = new double[data.numAttributes()];

			String line = bReader.readLine();
			JSONObject obj = new JSONObject(line);

			String business_id = obj.getString("business_id");

			vals[0] = data.attribute(0).addStringValue(business_id);

			String reviews = obj.getString("reviews");
			String tips = obj.getString("tips");

			StringBuilder reviewsAndTips = new StringBuilder();
			reviewsAndTips.append(reviews).append(" ").append(tips);
			StringBuilder reviewsAndTipsNoStopword = new StringBuilder();
			for (String s : reviewsAndTips.toString().split("\\b")) {
				if (!Stopwords.isStopword(s))
					reviewsAndTipsNoStopword.append(s).append(" ");
			}
			String text = reviewsAndTipsNoStopword.toString();
			text = text.replaceAll("\\W", " ");
			text = text.replaceAll(" +", " ");
			vals[1] = data.attribute(1).addStringValue(text);

			for (int i = 0; i < categories.length; i++)
				vals[i + 2] = 0;

			JSONArray categoriesArray = obj.getJSONArray("categories");
			boolean categoryFlag = false;
			for (int i = 0; i < categoriesArray.length(); i++) {
				String item = categoriesArray.get(i).toString();
				if (categoriesMap.containsKey(item)) {
					vals[categoriesMap.get(item) + 2] = 1;
					categoryFlag = true;
				}
			}
			if (categoryFlag == false) {
				continue;
			}
			data.add(new DenseInstance(1.0, vals));
		}

		bReader.close();
		// 4. output data
		// System.out.println(data);
		// save to a arrf file
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File("./output/train.arff"));
		saver.writeBatch();

	}

}
