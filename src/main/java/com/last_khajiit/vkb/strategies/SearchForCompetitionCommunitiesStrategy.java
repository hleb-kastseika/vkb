package com.last_khajiit.vkb.strategies;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pmw.tinylog.Logger;

import com.last_khajiit.vkb.api.VkClient;
import com.last_khajiit.vkb.api.VkEntitiesProcessor;
import com.last_khajiit.vkb.utils.BlackWordsDetector;
import com.last_khajiit.vkb.utils.Properties;
import com.last_khajiit.vkb.utils.TextUtils;

/*
 * This strategy implements searching for competition communities.
 */
public class SearchForCompetitionCommunitiesStrategy extends BehaviourStrategy {
	private Set<String> competitionkeyWords = new HashSet<String>();
	private static final String GROUP = "group";
	private static final String PAGE = "page";

	public SearchForCompetitionCommunitiesStrategy(VkClient apiClient) {
		super.apiClient = apiClient;
	}

	@Override
	public void execute() {
		try {
			searchCommunities();
		} catch (JSONException e) {
			Logger.error(e, "## Error in competition communities searching");
		}
	}

	private void searchCommunities() throws JSONException {
		Logger.info("## SearchForCompetitionCommunitiesStrategy searchCommunities was started");
		competitionkeyWords.addAll(
				Arrays.asList(propertyManager.getProp(Properties.COMMUNITIES_SEARCH_WORDS).split(COMMA_SEPARATOR)));

		int competitionPostsAmount = 0;
		JSONArray competitionWallPosts = new JSONArray();
		for (String keyWord : competitionkeyWords) {
			String searchedCommunities = apiClient.searchCommunities(TextUtils.utfToCp1251(keyWord));
			if (searchedCommunities.contains(RESPONSE_NODE) && searchedCommunities.contains(ITEMS_NODE)) {
				JSONArray communitiesList = new JSONObject(searchedCommunities).getJSONObject(RESPONSE_NODE)
						.getJSONArray(ITEMS_NODE);
				for (int i = 0; i < communitiesList.length(); i++) {
					JSONObject community = communitiesList.getJSONObject(i);
					Logger.info(community.toString());
					if ((Integer) community.get(IS_CLOSED_NODE) != 2
							&& (community.getString(TYPE_PROPERTY).equals(GROUP)
									|| community.getString(TYPE_PROPERTY).equals(PAGE))
							&& (Integer) community.get(IS_MEMBER_NODE) == 0) {
						// validate that community name don't contains blocked
						// words
						if (!BlackWordsDetector.getInstance()
								.isContainedInBlackWordsList(community.getString(NAME_PROPERTY))) {
							String wallPosts = TextUtils
									.cp1251ToUTF(apiClient.getWallPosts(COMMUNITY_MAREKER + community.get(ID_NODE)));
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
						} else {
							// add to community IDs black list
							BlackWordsDetector.getInstance()
									.addToBlockedCommunities(community.get(ID_PROPERTY).toString());
							Logger.info("## community with ID = [" + community.getLong(ID_PROPERTY)
									+ "] is added to communities black list");
						}
					} else {
						Logger.info(
								"## community with ID = [" + community.getLong(ID_PROPERTY) + "] is already joined");
					}
				}
			}
			sleepBetweenRequests();
		}
		Logger.info("## Amount of competition posts = " + competitionPostsAmount);
		new VkEntitiesProcessor(apiClient).repostCompetitionPosts(competitionWallPosts);

		Logger.info("## SearchForCompetitionCommunitiesStrategy searchCommunities was executed");
	}
}
