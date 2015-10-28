package com.last_khajiit.vkb.strategies;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pmw.tinylog.Logger;

import com.last_khajiit.vkb.api.VkClient;
import com.last_khajiit.vkb.api.VkEntitiesProcessor;
import com.last_khajiit.vkb.utils.BlackWordsDetector;
import com.last_khajiit.vkb.utils.DupDetector;
import com.last_khajiit.vkb.utils.Properties;
import com.last_khajiit.vkb.utils.TextUtils;
/*
 * This strategy implements reposting of random posts from mentioned communities.
 * It should to help hide "strange" reposting bot's activity.
 */
public class RepostingStrategy extends BehaviourStrategy{
	
	public RepostingStrategy(VkClient apiClient){
		super.apiClient = apiClient;
	}
	
	@Override
	public void execute() {
		try {
			doRepost();
		} catch (JSONException e) {
			Logger.error(e, "## Error in posts reposting");
		}
	}
	
	private void doRepost() throws JSONException{
		Logger.info("## RepostingStrategy doRepost was started");		
		String[] randomRepostsCommunitiesId = propertyManager.getProp(Properties.RANDOM_REPOSTS_COMMUNITIES_IDS).split(COMMA_SEPARATOR);		
		for(String communityId: randomRepostsCommunitiesId){
			if(new VkEntitiesProcessor(apiClient).isCommunity(communityId)){		
				String wallPosts = TextUtils.cp1251ToUTF(apiClient.getWallPosts(communityId));					
				if(wallPosts.contains(RESPONSE_NODE) &&wallPosts.contains(ITEMS_NODE)){
					JSONArray listWallPosts = new JSONObject(wallPosts).getJSONObject(RESPONSE_NODE).getJSONArray(ITEMS_NODE);
					int randomPostItemNumber = new Random().nextInt(listWallPosts.length());
					JSONObject randomPost = listWallPosts.getJSONObject(randomPostItemNumber);
					String fromId = randomPost.get(FROM_ID_NODE).toString();
					String wallPostId = WALL_NODE
							+fromId
							+UNDERSCORE_SEPARATOR
							+randomPost.get(ID_NODE);				
					if(!DupDetector.getInstance().isAlreadyPosted(wallPostId)
							&& !BlackWordsDetector.getInstance().isCommunityBlocked(fromId)){
						try {
							Thread.sleep(Integer.valueOf(propertyManager.getProp(Properties.REPOSTS_DELAY, propertyManager.DEFAULT_REPOSTS_DELAY)));
							apiClient.doRepost(wallPostId);
						} catch (NumberFormatException | InterruptedException e){
							Logger.error(e);
						}
						
					}
				}
			}			
		}		
		Logger.info("## RepostingStrategy doRepost was executed");
	}
}
