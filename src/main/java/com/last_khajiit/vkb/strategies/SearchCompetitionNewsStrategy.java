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
import com.last_khajiit.vkb.utils.Properties;
import com.last_khajiit.vkb.utils.TextUtils;

/*
 * This strategy implements searching for competition posts in currently joined communities.
 */
public class SearchCompetitionNewsStrategy extends BehaviourStrategy {
	private Set<String> competitionkeyWords = new HashSet<String>();

	public SearchCompetitionNewsStrategy(VkClient apiClient) {
		super.apiClient = apiClient;
	}

	@Override
	public void execute() {
		try {
			searchCompetitionNews();
		} catch (JSONException e) {
			Logger.error(e, "## Error in news checking");
		}
	}

	private void searchCompetitionNews() throws JSONException {
		Logger.info("## SearchCompetitionNewsStrategy searchCompetitionNews was started");
		competitionkeyWords.addAll(
				Arrays.asList(propertyManager.getProp(Properties.COMMUNITIES_SEARCH_WORDS).split(COMMA_SEPARATOR)));

		int competitionNewsAmount = 0;
		JSONArray competitionNewsArray = new JSONArray();
		for (String keyWord : competitionkeyWords) {
			String competitionNews = TextUtils.cp1251ToUTF(apiClient.searchNews(TextUtils.utfToCp1251(keyWord)));
			if (competitionNews.contains(RESPONSE_NODE) && competitionNews.contains(ITEMS_NODE)) {
				JSONArray newsList = new JSONObject(competitionNews).getJSONObject(RESPONSE_NODE)
						.getJSONArray(ITEMS_NODE);
				for (int j = 0; j < newsList.length(); j++) {
					if (newsList.get(j) instanceof JSONObject) {
						JSONObject post = (JSONObject) newsList.get(j);
						String text = null;
						try {
							text = (String) post.get(TEXT_NODE);
						} catch (JSONException e) {
							// post don't contains message
						}
						if (text != null && !text.isEmpty()) {
							if (TextUtils.validateMessage(text)) {
								competitionNewsAmount++;
								competitionNewsArray.put(post);
							}
						}
					}
				}
			}
			sleepBetweenRequests();
		}
		Logger.info("## Amount of competition news = " + competitionNewsAmount);
		new VkEntitiesProcessor(apiClient).repostCompetitionPosts(competitionNewsArray);

		Logger.info("## SearchCompetitionNewsStrategy searchCompetitionNews was executed");
	}
}
