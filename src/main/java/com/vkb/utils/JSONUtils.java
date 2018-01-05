package com.vkb.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.pmw.tinylog.Logger;

import java.util.HashSet;

public class JSONUtils {

    public static JSONArray concatArray(JSONArray... arrs) {
        JSONArray result = new JSONArray();
        for (JSONArray arr : arrs) {
            for (int i = 0; i < arr.length(); i++) {
                try {
                    result.put(arr.get(i));
                } catch (JSONException e) {
                    Logger.error(e);
                }
            }
        }
        return result;
    }

    public static JSONArray removeDuplicateIds(JSONArray array) {
        JSONArray result = new JSONArray();
        HashSet<Integer> jsonSet = new HashSet<Integer>();
        for (int i = 0; i < array.length(); i++) {
            try {
                jsonSet.add((Integer) array.get(i));
            } catch (JSONException e) {
                Logger.error(e);
            }
        }
        for (Integer jsonObj : jsonSet) {
            result.put(jsonObj);
        }
        return result;
    }
}
