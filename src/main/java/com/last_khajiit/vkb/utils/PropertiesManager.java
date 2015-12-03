package com.last_khajiit.vkb.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.pmw.tinylog.Logger;

public final class PropertiesManager {

	private static PropertiesManager _instance = null;
	public final String DEFAULT_REQUESTS_DELAY = "1000";
	public final String DEFAULT_REPOSTS_DELAY = "450000";
	public final String DEFAULT_TIMER_START_DELAY = "0";
	public final String DEFAULT_TASKS_EXECUTION_PERIOD = "14400000";

	public final String DEFAULT_POSTED_DB_NAME = "db/postedIds.db";
	public final String DEFAULT_POSTED_SET_NAME = "postedIds";
	public final String DEFAULT_COMMUNITY_BLACK_LIST_DB_NAME = "db/communityBlackList.db";
	public final String DEFAULT_COMMUNITY_BLACK_LIST_NAME = "communityBlackList";
	public final String DEFAULT_POSTPONED_COMMUNITIES_DB_NAME = "db/postponedCommunities.db";
	public final String DEFAULT_POSTPONED_COMMUNITIES_LIST_NAME = "postponedCommunities";
	public final String DEFAULT_POSTPONED_USERS_DB_NAME = "db/postponedUsers.db";
	public final String DEFAULT_POSTPONED_USERS_LIST_NAME = "postponedUsers";

	public final String DEFAULT_PERCENTAGE_OF_REQUESTS_FOR_FRIENDSHIP = "2";

	private static List<String> properties_files = new ArrayList<String>();

	static {
		/*
		 * As I developed this project on Windows OS I hardcoded filepath
		 * separator here and in some other places, so for building it on linux
		 * of some other systems we need to modify these values (or rewrite file
		 * reading with File.separator or system properties)
		 */
		properties_files.add("conf/vkb.properties");
		properties_files.add("conf/vk.api.properties");
	}

	private Properties props = new Properties();

	private PropertiesManager() {
		try {
			loadProps();
		} catch (IOException e) {
			Logger.error(e, "An Error occurred while preparing properties.");
		}
	}

	public static synchronized PropertiesManager getInstance() {
		if (_instance == null)
			_instance = new PropertiesManager();
		return _instance;
	}

	private void loadProps() throws IOException {
		for (String s : properties_files) {
			FileInputStream fis = new FileInputStream(s);
			props.load(fis);
		}
	}

	public String getProp(String key) {
		return props.getProperty(key);
	}

	public String getProp(String key, String defaultValue) {
		if (props.getProperty(key).isEmpty())
			return defaultValue;
		return props.getProperty(key);
	}
}