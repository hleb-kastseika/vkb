package com.last_khajiit.vkb.strategies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pmw.tinylog.Logger;

import com.last_khajiit.vkb.api.VkClient;
import com.last_khajiit.vkb.api.VkEntitiesProcessor;
import com.last_khajiit.vkb.utils.TextUtils;
/*
 * This strategy implements searching of competition posts in private messages that you recive.
 */
public class CheckPrivateMessagesStrategy extends BehaviourStrategy {

	public CheckPrivateMessagesStrategy(VkClient apiClient) {
		super.apiClient = apiClient;
	}

	@Override
	public void execute() {
		try {
			checkPrivateMessages();
		} catch (JSONException e) {
			Logger.error(e, "## Error in private messages checking");
		}
	}

	private void checkPrivateMessages() throws JSONException {
		Logger.info("## CheckPrivateMessagesStrategy checkPrivateMessages was started");
				
		String messages = TextUtils.cp1251ToUTF(apiClient.getPrivateMessages());
		
		int competitionPostsAmount = 0;
		JSONArray competitionWallPosts = new JSONArray();
		if(messages.contains(RESPONSE_NODE)
				 && messages.contains(ITEMS_NODE)){
			JSONArray messagesArray = new JSONObject(messages).getJSONObject(RESPONSE_NODE).getJSONArray(ITEMS_NODE);
			for(int i=0; i< messagesArray.length(); i++){
				if(messagesArray.get(i) instanceof JSONObject){
					JSONObject message = (JSONObject)messagesArray.get(i);
					if(message.has(ATTACHMENTS_NODE)){
						JSONArray attachments = message.getJSONArray(ATTACHMENTS_NODE);
						for(int j=0; j< attachments.length(); j++){
							if(attachments.get(j) instanceof JSONObject){
								JSONObject attachment = (JSONObject)attachments.get(j);
								if(attachment.getString(TYPE_NODE).equals(WALL_NODE)){									
									JSONObject wallPost = (JSONObject)attachment.get(WALL_NODE);
									String text = null;
									try{
										text = (String)wallPost.get(TEXT_NODE);
									}catch(JSONException e){
										//post don't contains message
									}			
									if(text != null && !text.isEmpty()){
										if(TextUtils.validateMessage(text)){
											competitionPostsAmount++;
											competitionWallPosts.put(wallPost);
										}
									}
								}
							}
						}
					}
				}				
			}		
		}
		Logger.info("## Amount of competition posts in private messages = "+competitionPostsAmount);
		new VkEntitiesProcessor(apiClient).repostCompetitionPosts(competitionWallPosts);		
		
		Logger.info("## CheckPrivateMessagesStrategy checkPrivateMessages was executed");
	}
}
