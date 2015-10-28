package com.last_khajiit.vkb.utils;

import java.io.File;
import java.util.Set;

import org.mapdb.DB;
import org.mapdb.DBMaker;

public class PostponedItemsHandler{
	private static PostponedItemsHandler _instance = null;	
	private static PropertiesManager propertyManager = PropertiesManager.getInstance();
	
	private static DB postponedCommunitiesDb = DBMaker.fileDB(new File(propertyManager.getProp(Properties.POSTPONED_COMMUNITIES_DB_NAME, propertyManager.DEFAULT_POSTPONED_COMMUNITIES_DB_NAME))).closeOnJvmShutdown().make();
	private static Set<String> postponedCommunities = postponedCommunitiesDb.createHashSet(propertyManager.getProp(Properties.POSTPONED_COMMUNITIES_LIST_NAME, propertyManager.DEFAULT_POSTPONED_COMMUNITIES_LIST_NAME)).makeOrGet();
	
	private static DB postponedUsersDb = DBMaker.fileDB(new File(propertyManager.getProp(Properties.POSTPONED_USERS_DB_NAME, propertyManager.DEFAULT_POSTPONED_USERS_DB_NAME))).closeOnJvmShutdown().make();
	private static Set<String> postponedUsers = postponedUsersDb.createHashSet(propertyManager.getProp(Properties.POSTPONED_USERS_LIST_NAME, propertyManager.DEFAULT_POSTPONED_USERS_LIST_NAME)).makeOrGet();

	private PostponedItemsHandler(){}
	
	public static synchronized PostponedItemsHandler getInstance() {
        if (_instance == null)
            _instance = new PostponedItemsHandler();
        return _instance;
    }	
	
	public void putToPostponedCommunities(Set<String> communityIds){
		postponedCommunities.addAll(communityIds);
		postponedCommunitiesDb.commit();
	}
	
	public void removeFromPostponedCommunities(Set<String> communityIds){
		postponedCommunities.removeAll(communityIds);
		postponedCommunitiesDb.commit();
	}
	
	public Set<String> getPostponedCommunities(){		
		return postponedCommunities;
	}
	
	public void putToPostponedUsers(Set<String> userIds){
		postponedUsers.addAll(userIds);
		postponedUsersDb.commit();
	}
	
	public Set<String> getPostponedUsers(){	
		return postponedUsers;
	}
	
	public void removeFromPostponedUsers(Set<String> userIds){
		postponedUsers.removeAll(userIds);
		postponedUsersDb.commit();
	}
}
