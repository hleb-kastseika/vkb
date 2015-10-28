package com.last_khajiit.vkb.utils;

import java.io.File;
import java.util.Set;

import org.mapdb.*;

public class DupDetector {
	private static DupDetector _instance = null;
	private static PropertiesManager propertyManager = PropertiesManager.getInstance();
	private static DB db = DBMaker.fileDB(new File(propertyManager.getProp(Properties.POSTED_DB_NAME, propertyManager.DEFAULT_POSTED_DB_NAME))).closeOnJvmShutdown().make();	
	private static Set<String> postedIds = db.createHashSet(propertyManager.getProp(Properties.POSTED_SET_NAME, propertyManager.DEFAULT_POSTED_SET_NAME)).makeOrGet();
		
	private DupDetector(){}
	
	public static synchronized DupDetector getInstance() {
        if (_instance == null)
            _instance = new DupDetector();
        return _instance;
    }
	
	public boolean isAlreadyPosted(String wallPostId){
		boolean isAlreadyPosted = postedIds.contains(wallPostId);
		return isAlreadyPosted;
	}	
	
	public void addPostedId(String wallPostId){
		postedIds.add(wallPostId);
		db.commit();
	}
}
