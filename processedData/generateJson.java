package firstTaskYelp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class generateJson {

	public static HashMap<String, String> getReviewTip(String filePath)
			throws IOException, JSONException {
		File file = new File(filePath);
		BufferedReader reader = new BufferedReader(new FileReader(file));

		HashMap<String, String> reviewsHashMap = new HashMap<>();

		while (reader.ready()) {
			String line = reader.readLine();
			JSONObject obj = new JSONObject(line);

			String business_id = obj.getString("business_id");

			StringBuilder review = new StringBuilder();
			String tempReview = obj.getString("text");
			if (reviewsHashMap.containsKey(business_id)) {
				String previousReview = reviewsHashMap.get(business_id);
				review.append(previousReview + tempReview);
			} else {
				review.append(tempReview);
			}
			reviewsHashMap.put(business_id, review.toString());
		}

		reader.close();
		return reviewsHashMap;
	}

	public static void getCategories(String filePath,
			HashMap<String, String> reviews, HashMap<String, String> tips)
			throws IOException, JSONException {

		File file = new File(filePath);
		BufferedReader bReader = new BufferedReader(new FileReader(file));
		BufferedWriter output = new BufferedWriter(new FileWriter("./output/collectedData.json"));

		while (bReader.ready()) {
			String line = bReader.readLine();
			JSONObject obj = new JSONObject(line);
			JSONStringer objOut = new JSONStringer();

			String business_id = obj.getString("business_id");
			objOut.object();
			objOut.key("business_id");
			objOut.value(business_id);
			objOut.key("name");
			objOut.value(obj.getString("name"));
			objOut.key("categories");
			objOut.value(obj.getJSONArray("categories"));

			if (reviews.containsKey(business_id)) {
				objOut.key("reviews");
				objOut.value(reviews.get(business_id));
			}
			else {
				objOut.key("reviews");
				objOut.value(" ");
			}

			if (tips.containsKey(business_id)) {
				objOut.key("tips");
				objOut.value(tips.get(business_id));
			}
			else {
				objOut.key("tips");
				objOut.value(" ");
			}

			objOut.endObject();
			output.append(objOut.toString() + "\n");
		}
		bReader.close();
		output.close();
	}

	public static void main(String[] args) throws IOException, JSONException {
		// TODO Auto-generated method stub

		HashMap<String, String> reviews = getReviewTip("./data/yelp_academic_dataset_review.json");
		System.out.println("a" + reviews.size());
		HashMap<String, String> tips = getReviewTip("./data/yelp_academic_dataset_tip.json");
		System.out.println("b" + tips.size());
		getCategories("./data/yelp_academic_dataset_business.json", reviews,tips);
	}

}
