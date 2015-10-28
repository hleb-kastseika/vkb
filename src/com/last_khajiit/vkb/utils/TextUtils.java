package com.last_khajiit.vkb.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pmw.tinylog.Logger;

public class TextUtils {
	private final static String CP1251_ENCODING = "Cp1251";
	private final static String UTF8_ENCODING = "UTF-8";
	private final static String COMMUNITY_ID_PATTERN = "club\\d+";
	private final static String USER_ID_PATTERN = "id\\d+";
	private final static String CLASSIFICATION_GROUPS = "classification_groups";
	private final static String COMMA_SEPARATOR = ",";
	
	public static String cp1251ToUTF(String cp1251String){
		String utfString = null;
		try {
			utfString = new String(cp1251String.getBytes(CP1251_ENCODING), UTF8_ENCODING);
		} catch (UnsupportedEncodingException e) {
			Logger.error(e);
		}
		return utfString;
	}	
	
	public static String utfToCp1251(String utfString){
		String cp1251String = null;
		try {
			cp1251String = new String(utfString.getBytes(UTF8_ENCODING), CP1251_ENCODING);
		} catch (UnsupportedEncodingException e) {
			Logger.error(e);
		}
		return cp1251String;
	}	
	
	public static boolean validateMessage(String postMessage){
		boolean isCompetition = false;
		boolean postContainsKeyWords = classifyPostAsCompetition(postMessage);
				
		if(postContainsKeyWords && !BlackWordsDetector.getInstance().isContainedInBlackWordsList(postMessage)) isCompetition = true;
		
		//TODO add expiration date checking?!
		if(isCompetition){			
			Set<String> communitiesSet = extractIds(postMessage, COMMUNITY_ID_PATTERN, 4);
			if(!communitiesSet.isEmpty()) PostponedItemsHandler.getInstance().putToPostponedCommunities(communitiesSet);
						
			Set<String> usersSet = extractIds(postMessage, USER_ID_PATTERN, 2);
			if(!usersSet.isEmpty()) PostponedItemsHandler.getInstance().putToPostponedUsers(usersSet);
		}		
		return isCompetition;
	}
	
	private static Set<String> extractIds(String post, String patternStr, int substrIndx){
		Matcher matcher;
		Pattern userPattern = Pattern.compile(patternStr);			
		matcher = userPattern.matcher(post);
		Set<String> idSet = new HashSet<String>();
		while (matcher.find()){	
			idSet.add(matcher.group().substring(substrIndx));
		}		
		return idSet;
	}
	
	public static boolean classifyPostAsCompetition(String post){
		try {
			String classificationModel = PropertiesManager.getInstance().getProp(Properties.POST_CLASSIFICATION_MODEL);
			JSONObject jsonClassificationModel = new JSONObject(classificationModel);
			
			JSONArray keyWordsGroups = jsonClassificationModel.getJSONArray(CLASSIFICATION_GROUPS);
			boolean[] allKeyWordsGroupFlags = new boolean[keyWordsGroups.length()];
			for(int i=0; i<keyWordsGroups.length(); i++){
				ArrayList<String> keyWordsList = new ArrayList<String>();
				keyWordsList.addAll(Arrays.asList(keyWordsGroups.get(i).toString().split(COMMA_SEPARATOR)));
				allKeyWordsGroupFlags[i] = false;
				for(String keyWord: keyWordsList){
					if(post.contains(keyWord)) allKeyWordsGroupFlags[i] = true;
				}
			}
			
			for(boolean kw : allKeyWordsGroupFlags) if(!kw) return false;
			return true;			
		} catch (JSONException e) {
			Logger.error(e);
		}
		return false;
	}
}
