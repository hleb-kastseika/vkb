package com.last_khajiit.vkb.utils;

import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

import org.mapdb.DB;
import org.mapdb.DBMaker;

public class BlackWordsDetector {
	private static BlackWordsDetector _instance = null;
	private static final String COMMUNITY_MAREKER = "-";
	private static final String WORD_SEPARATORS_REGEX = "\\s*(\\s|=|,)\\s*"; //possible need to add more separator characters
	protected static final String COMMA_SEPARATOR = ",";
	
	private static PropertiesManager propertyManager = PropertiesManager.getInstance();
	private static DB db = DBMaker.fileDB(new File(propertyManager.getProp(Properties.COMMUNITY_BLACK_LIST_DB_NAME, propertyManager.DEFAULT_COMMUNITY_BLACK_LIST_DB_NAME))).closeOnJvmShutdown().make();
	private static Set<String> blackList = db.createHashSet(propertyManager.getProp(Properties.COMMUNITY_BLACK_LIST_NAME, propertyManager.DEFAULT_COMMUNITY_BLACK_LIST_NAME)).makeOrGet();
	private static ArrayList<String> blackWords = new ArrayList<String>();
	
	static{
		blackWords.addAll(Arrays.asList(propertyManager.getProp(Properties.BLOCKED_WORDS).split(COMMA_SEPARATOR)));
	}
		
	private BlackWordsDetector(){}
	
	public static synchronized BlackWordsDetector getInstance() {
        if (_instance == null)
            _instance = new BlackWordsDetector();
        return _instance;
    }
	
	public boolean isCommunityBlocked(String communityId){
		if(communityId.startsWith(COMMUNITY_MAREKER)){
			communityId = communityId.substring(1);
		}
		return blackList.contains(communityId);
	}
	
	public void addToBlockedCommunities(String communityId){
		blackList.add(communityId);
		db.commit();
	}
	
	public boolean isContainedInBlackWordsList(String entityName){
		Pattern p = Pattern.compile(WORD_SEPARATORS_REGEX);
        String[] items = p.split(entityName);
        for(int i = 0; i<items.length; i++){			
			if(blackWords.contains(items[i])){
				return true;
			}
		}
		return false;
	}
}
