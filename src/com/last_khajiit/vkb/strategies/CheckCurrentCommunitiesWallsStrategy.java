package com.last_khajiit.vkb.strategies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pmw.tinylog.Logger;

import com.last_khajiit.vkb.api.VkClient;
import com.last_khajiit.vkb.api.VkEntitiesProcessor;
import com.last_khajiit.vkb.utils.Properties;
import com.last_khajiit.vkb.utils.TextUtils;

/*
 * This strategy implements searching of competition posts in communities, that you already joined.
 */
public class CheckCurrentCommunitiesWallsStrategy extends BehaviourStrategy {

	public CheckCurrentCommunitiesWallsStrategy(VkClient apiClient) {
		super.apiClient = apiClient;
	}

	@Override
	public void execute() {
		try {
			checkCommunitiesWalls();
		} catch (JSONException e) {
			Logger.error(e, "## Error in current communities checking");
		}
	}

	private void checkCommunitiesWalls() throws JSONException {
		Logger.info("## JoinCompetitionCommunitiesStrategy checkCommunitiesWalls was started");

		String currentCommunities = apiClient.getUserCommunities(propertyManager.getProp(Properties.CURRENT_USER_ID));
		Logger.info(currentCommunities);

		JSONArray currentCommunitiesIds = new JSONObject(currentCommunities).getJSONObject(RESPONSE_NODE)
				.getJSONArray(ITEMS_NODE);
		Logger.info("## Amount of current communities = " + currentCommunitiesIds.length());

		int competitionPostsAmount = 0;
		JSONArray competitionWallPosts = new JSONArray();
		for (int i = 0; i < currentCommunitiesIds.length(); i++) {
			String wallPosts = TextUtils
					.cp1251ToUTF(apiClient.getWallPosts(COMMUNITY_MAREKER + currentCommunitiesIds.get(i).toString()));
			if (wallPosts.contains(RESPONSE_NODE) && wallPosts.contains(ITEMS_NODE)) {
				JSONArray listWallPosts = new JSONObject(wallPosts).getJSONObject(RESPONSE_NODE)
						.getJSONArray(ITEMS_NODE);
				for (int j = 0; j < listWallPosts.length(); j++) {
					if (listWallPosts.get(j) instanceof JSONObject) {
						JSONObject post = (JSONObject) listWallPosts.get(j);
						String text = null;
						try {
							text = (String) post.get(TEXT_NODE);
						} catch (JSONException e) {
							// post don't contains message
						}
						if (text != null && !text.isEmpty()) {
							if (TextUtils.validateMessage(text)) {
								competitionPostsAmount++;
								competitionWallPosts.put(post);
							}
						}
					}
				}
			}
		}
		Logger.info("## Amount of competition posts = " + competitionPostsAmount);
		new VkEntitiesProcessor(apiClient).repostCompetitionPosts(competitionWallPosts);

		Logger.info("## JoinCompetitionCommunitiesStrategy checkCommunitiesWalls was executed");
	}
}
