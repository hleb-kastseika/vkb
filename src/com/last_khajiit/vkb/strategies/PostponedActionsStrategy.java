package com.last_khajiit.vkb.strategies;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.pmw.tinylog.Logger;

import com.last_khajiit.vkb.api.VkClient;
import com.last_khajiit.vkb.api.VkEntitiesProcessor;
import com.last_khajiit.vkb.utils.PostponedItemsHandler;
/*
 * This strategy implements execution of postponed actions (adding to friend, community joining).
 */
public class PostponedActionsStrategy extends BehaviourStrategy{	
	
	public PostponedActionsStrategy(VkClient apiClient){
		super.apiClient = apiClient;
	}
	
	@Override
	public void execute() {
		try {
			executePostponedActions();
		} catch (JSONException e) {
			Logger.error(e, "Error in postponed actions execution");
		}
	}
	
	private void executePostponedActions() throws JSONException{	
		Logger.info("## PostponedActionsStrategy executePostponedActions was started");
		
		processCommunities();
		processUsers();
		
		Logger.info("## PostponedActionsStrategy executePostponedActions was executed");
	}
	
	private void processCommunities(){
		VkEntitiesProcessor vkProcessor = new VkEntitiesProcessor(apiClient);
		Set<String> processedCommunities = new HashSet<String>();
		for(String communityId: PostponedItemsHandler.getInstance().getPostponedCommunities()){
			boolean isGroupJoined = false;
			sleepBetweenRequests();
			if(vkProcessor.isCommunity(communityId)){
				sleepBetweenRequests();
				if(vkProcessor.isCommunityJoined(communityId)){
					isGroupJoined = true;
				}else{
					sleepBetweenRequests();
					isGroupJoined = vkProcessor.joinCompetitionCommunity(communityId);
				}						
			}else{
				isGroupJoined = true;
			}
			if(isGroupJoined) processedCommunities.add(communityId);
		}
		PostponedItemsHandler.getInstance().removeFromPostponedCommunities(processedCommunities);
	}
	
	private void processUsers(){
		Set<String> processedUsers = new HashSet<String>();
		for(String userId: PostponedItemsHandler.getInstance().getPostponedUsers()){
			sleepBetweenRequests();
			boolean isRequestSuccessfull = apiClient.addToFriends(String.valueOf(userId));
			if(isRequestSuccessfull) processedUsers.add(userId);
		}
		PostponedItemsHandler.getInstance().removeFromPostponedUsers(processedUsers);
	}
}
