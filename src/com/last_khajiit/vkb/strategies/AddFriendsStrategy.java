package com.last_khajiit.vkb.strategies;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.pmw.tinylog.Logger;

import com.last_khajiit.vkb.api.VkClient;
import com.last_khajiit.vkb.utils.Properties;

/*
 * This strategy implements adding random users to friends.
 * Candidate IDs are searched among friends of your current friends (friends of friends).
 */
public class AddFriendsStrategy extends BehaviourStrategy {
	public AddFriendsStrategy(VkClient apiClient) {
		super.apiClient = apiClient;
	}

	@Override
	public void execute() {
		try {
			addRandomFriends();
		} catch (JSONException e) {
			Logger.error(e, "Error in friends additing");
		}
	}

	private void addRandomFriends() throws JSONException {
		Logger.info("## AddFriendsStrategy addRandomFriends was started");
		JSONArray friendIds = new JSONArray(apiClient.getFriendsOfFriendsIds());
		int possibleFriendsSize = friendIds.length();
		Logger.info("## Posible friends size: " + possibleFriendsSize);

		int requestsAmount = new Random().nextInt(possibleFriendsSize
				* Integer.parseInt(propertyManager.getProp(Properties.PERCENTAGE_OF_REQUESTS_FOR_FRIENDSHIP,
						propertyManager.DEFAULT_PERCENTAGE_OF_REQUESTS_FOR_FRIENDSHIP))
				/ 100);
		Logger.info("## Amount of requests for friendship: " + requestsAmount);
		for (int i = 0; i < requestsAmount; i++) {
			int candidateId = (int) friendIds.get(new Random().nextInt(possibleFriendsSize));
			sleepBetweenRequests();
			apiClient.addToFriends(String.valueOf(candidateId));
		}
		Logger.info("## AddFriendsStrategy addRandomFriends was executed");
	}
}
