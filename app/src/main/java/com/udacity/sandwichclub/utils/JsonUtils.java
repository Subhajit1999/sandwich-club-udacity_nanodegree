package com.udacity.sandwichclub.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.udacity.sandwichclub.model.Sandwich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private static final String TAG = "JsonUtils";

    public static Sandwich parseSandwichJson(Context context, String json) {
        Log.d(TAG, "parseSandwichJson: parsing json data");
        Sandwich sandwichItem=null;
        try {
            JSONObject parentObject = new JSONObject(json);
            JSONObject nameObject = parentObject.getJSONObject("name");
            String mainName = nameObject.getString("mainName");
            JSONArray otherNamesArray = nameObject.getJSONArray("alsoKnownAs");
            List<String> otherNames = new ArrayList<>();
            for (int i=0; i<otherNamesArray.length(); i++){
                otherNames.add(otherNamesArray.getString(i));
            }
            String origin = parentObject.getString("placeOfOrigin");
            String description = parentObject.getString("description");
            String imageUrl = parentObject.getString("image");
            JSONArray ingredientsArray = parentObject.getJSONArray("ingredients");
            List<String> ingredients = new ArrayList<>();
            for (int i=0; i<ingredientsArray.length(); i++){
                ingredients.add(ingredientsArray.getString(i));
            }
            sandwichItem = new Sandwich(mainName,otherNames,origin,description,imageUrl,ingredients);
        } catch (JSONException e) {
            Log.d(TAG, "parseSandwichJson: Error parsing json");
            e.printStackTrace();
        }

        return sandwichItem;
    }
}
