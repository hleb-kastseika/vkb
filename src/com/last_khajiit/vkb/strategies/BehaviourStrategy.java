package com.last_khajiit.vkb.strategies;

import org.pmw.tinylog.Logger;

import com.last_khajiit.vkb.api.VkClient;
import com.last_khajiit.vkb.utils.Properties;
import com.last_khajiit.vkb.utils.PropertiesManager;

public abstract class BehaviourStrategy {
	protected PropertiesManager propertyManager = PropertiesManager.getInstance();
	protected VkClient apiClient;
	protected static final String RESPONSE_NODE = "response";
	protected static final String ITEMS_NODE = "items";
	protected static final String TEXT_NODE = "text";
	protected static final String TYPE_NODE = "type";
	protected static final String WALL_NODE = "wall";
	protected static final String FROM_ID_NODE = "from_id";
	protected static final String ID_NODE = "id";
	protected static final String IS_MEMBER_NODE = "is_member";
	protected static final String IS_CLOSED_NODE = "is_closed";
	protected static final String ATTACHMENTS_NODE = "attachments";
	protected static final String COMMUNITY_MAREKER = "-";
	protected static final String UNDERSCORE_SEPARATOR = "_";
	protected static final String COMMA_SEPARATOR = ",";
	protected static final String NAME_PROPERTY = "name";
	protected static final String ID_PROPERTY = "id";
	protected static final String TYPE_PROPERTY = "type";
	
	public void execute(){};
	
	public void sleepBetweenRequests(){
		try {
			Thread.sleep(Integer.valueOf(propertyManager.getProp(Properties.REQUESTS_DELAY, propertyManager.DEFAULT_REQUESTS_DELAY)));
		} catch (NumberFormatException | InterruptedException e) {
			Logger.error(e);
		}
	};
}
