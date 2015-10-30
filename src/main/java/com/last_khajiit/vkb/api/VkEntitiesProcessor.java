package com.last_khajiit.vkb.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pmw.tinylog.Logger;

import com.last_khajiit.vkb.utils.BlackWordsDetector;
import com.last_khajiit.vkb.utils.DupDetector;
import com.last_khajiit.vkb.utils.Properties;
import com.last_khajiit.vkb.utils.PropertiesManager;

public class VkEntitiesProcessor {
	protected static PropertiesManager propertyManager = PropertiesManager.getInstance();
	private VkClient apiClient;
	private static final String FROM_ID_NODE = "from_id";
	private static final String ID_NODE = "id";
	private static final String WALL_PREFIX = "wall";
	private static final String UNDERSCORE_SEPARATOR = "_";
	private static final String COMMUNITY_MAREKER = "-";

	public VkEntitiesProcessor(VkClient vkClient) {
		this.apiClient = vkClient;
	}

	public void repostCompetitionPosts(JSONArray competitionWallPosts) {
		for (int i = 0; i < competitionWallPosts.length(); i++) {
			try {
				String fromId = ((JSONObject) competitionWallPosts.get(i)).get(FROM_ID_NODE).toString();
				String wallPostId = WALL_PREFIX + fromId + UNDERSCORE_SEPARATOR
						+ ((JSONObject) competitionWallPosts.get(i)).get(ID_NODE);
				if (!DupDetector.getInstance().isAlreadyPosted(wallPostId)
						&& !BlackWordsDetector.getInstance().isCommunityBlocked(fromId)) {
					Thread.sleep(Integer.valueOf(
							propertyManager.getProp(Properties.REPOSTS_DELAY, propertyManager.DEFAULT_REPOSTS_DELAY)));
					boolean isGroupJoined = false;
					if (isCommunity(fromId)) {
						if (isCommunityJoined(fromId)) {
							isGroupJoined = true;
						} else {
							isGroupJoined = joinCompetitionCommunity(fromId);
						}
					} else {
						isGroupJoined = true;
					}

					boolean isLiked = false;
					if (isLiked(fromId, ((JSONObject) competitionWallPosts.get(i)).get(ID_NODE).toString())) {
						isLiked = true;
					} else {
						isLiked = putLike(fromId, ((JSONObject) competitionWallPosts.get(i)).get(ID_NODE).toString());
					}

					if (isLiked && isGroupJoined) {
						apiClient.doRepost(wallPostId);
					}
				} else {
					Logger.info("## Competition post is already contained in duplicates set, wall post ID = ["
							+ wallPostId + "]");
				}
			} catch (JSONException | InterruptedException e) {
				Logger.error(e);
			}
		}
	}

	public boolean joinCompetitionCommunity(String communityId) {
		boolean isCommunityJoined = false;
		if (communityId.startsWith(COMMUNITY_MAREKER)) {
			communityId = communityId.substring(1);
		}
		try {
			Thread.sleep(Integer.valueOf(
					propertyManager.getProp(Properties.REQUESTS_DELAY, propertyManager.DEFAULT_REQUESTS_DELAY)));
			isCommunityJoined = apiClient.joinCommunity(communityId);
		} catch (NumberFormatException | InterruptedException e) {
			Logger.error(e);
		}
		return isCommunityJoined;
	}

	private boolean putLike(String ownerId, String itemId) {
		boolean isLiked = false;
		try {
			Thread.sleep(Integer.valueOf(
					propertyManager.getProp(Properties.REQUESTS_DELAY, propertyManager.DEFAULT_REQUESTS_DELAY)));
			isLiked = apiClient.putLike(ownerId, itemId);
		} catch (NumberFormatException | InterruptedException e) {
			Logger.error(e);
		}
		return isLiked;
	}

	public boolean isCommunity(String communityId) {
		return communityId.startsWith(COMMUNITY_MAREKER);
	}

	public boolean isCommunityJoined(String communityId) {
		boolean isCommunityJoined = false;
		if (communityId.startsWith(COMMUNITY_MAREKER)) {
			communityId = communityId.substring(1);
		}
		try {
			isCommunityJoined = apiClient.isCommunityJoined(communityId);
		} catch (NumberFormatException e) {
			Logger.error(e);
		}
		return isCommunityJoined;
	}

	public boolean isLiked(String ownerId, String itemId) {
		boolean isLiked = false;
		try {
			Thread.sleep(Integer.valueOf(
					propertyManager.getProp(Properties.REQUESTS_DELAY, propertyManager.DEFAULT_REQUESTS_DELAY)));
			isLiked = apiClient.isLiked(ownerId, itemId);
		} catch (NumberFormatException | InterruptedException e) {
			Logger.error(e);
		}
		return isLiked;
	}
}
