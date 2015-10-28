package com.last_khajiit.vkb.api;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pmw.tinylog.Logger;

import com.last_khajiit.vkb.utils.DupDetector;
import com.last_khajiit.vkb.utils.JSONUtils;
import com.last_khajiit.vkb.utils.Properties;
import com.last_khajiit.vkb.utils.PropertiesManager;

public class VkClient {
	private static PropertiesManager propertyManager = PropertiesManager.getInstance();
	private static final String AUTH_URL = "https://oauth.vk.com/authorize"
            + "?client_id={APP_ID}"
            + "&scope={PERMISSIONS}"
            + "&redirect_uri={REDIRECT_URI}"
            + "&display={DISPLAY}"
            + "&v={API_VERSION}"
            + "&response_type=token";
    
    private static final String API_REQUEST = "https://api.vk.com/method/{METHOD_NAME}"
            + "?{PARAMETERS}"
            + "&access_token={ACCESS_TOKEN}"
            + "&v=" + propertyManager.getProp(Properties.API_VERSION);
    
	private final String accessToken;
	
	private VkClient(String appId, String accessToken) throws IOException {
        this.accessToken = accessToken;
        if (accessToken == null || accessToken.isEmpty()) {
            auth(appId);
            throw new Error("Need access token");
        }
    }
	
	public static VkClient with(String appId, String accessToken){
        try {
			return new VkClient(appId, accessToken);
		} catch (IOException e){
			Logger.error(e, "Can't initialize VkClient");
		}
		return null;
    }
	
	private void auth(String appId) throws IOException {
        String reqUrl = AUTH_URL
                .replace("{APP_ID}", appId)
                .replace("{PERMISSIONS}", "notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions,wall,groups,messages,notifications,stats,ads,offline")
                .replace("{REDIRECT_URI}", "https://oauth.vk.com/blank.html")
                .replace("{DISPLAY}", "page")
                .replace("{API_VERSION}", propertyManager.getProp(Properties.API_VERSION));
        try {
            Desktop.getDesktop().browse(new URL(reqUrl).toURI());
        } catch (URISyntaxException ex){            
            Logger.error(ex);
        }
    }
	
	public String getAlbums(String userId){
		String result = "";
        try{
        	result = invokeApi("photos.getAlbums", Params.create()
                    .add("owner_id", userId)
                    .add("photo_sizes", "1")
                    .add("thumb_src", "1"));
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return result;
    }
	
	public String getNewsFeed(){
        String result = "";
        try{
        	result = invokeApi("newsfeed.get", Params.create()
                    .add("count", "100"));
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return result;
    }
	
	public String getStatus(String userId){
		String result = "";
        try{
        	result = invokeApi("status.get", Params.create()
                    .add("user_id", userId));
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return result;
    }
	
	public boolean isCommunityJoined(String communityId){
		boolean isJoined = false;
        try{
        	String result = invokeApi("groups.isMember", Params.create()
                    .add("group_id", communityId)
                    .add("user_id", propertyManager.getProp(Properties.CURRENT_USER_ID)));
        	if(result.contains("error") && result.contains("captcha")){
        		//process captcha
        		Logger.error("CAPTCHA!!!!!!!!");
        	}else if(result.contains("response") && result.contains("1")){
        		isJoined = true;
        	}        	
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return isJoined;
    }
	
	public String getFriendIds(String userId){
		String result = "";
        try{
        	result = invokeApi("friends.get", Params.create()
                    .add("user_id", userId));
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return result;
    }
	
	public boolean addToFriends(String userId){
		boolean isRequestSuccessfull = false;
		try{
        	String result = invokeApi("friends.add", Params.create()
                    .add("user_id", userId));
        	if(result.contains("error") && result.contains("captcha")){
        		//process captcha
        		Logger.error("CAPTCHA!!!!!!!!");
        	}else if(result.contains("response") && result.contains("1")){
        		isRequestSuccessfull = true;
        		Logger.info("## Request to user is sent, result: "+result);
        	}else{
        		Logger.info("Request to user is not sent, result: "+result);
        	}
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return isRequestSuccessfull;
    }
	
	public String getWallPosts(){
        String wallPosts = "";
        try{
        	wallPosts = invokeApi("wall.get", Params.create()
                    .add("count", "100"));
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return wallPosts;
    }
	
	public String searchNews(String keyWord){
        String wallPosts = "";
        try{
        	wallPosts = invokeApi("newsfeed.search", Params.create()
                    .add("q", keyWord)
                    .add("count", "200"));
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return wallPosts;
    }	
	
	public String getFriendsOfFriendsIds(){
		JSONArray userIds = new JSONArray();
        try{
        	String myFriendIdResponse = invokeApi("friends.get", Params.create()
        			.add("user_id", propertyManager.getProp(Properties.CURRENT_USER_ID)));
        	JSONArray friendIds = new JSONObject(myFriendIdResponse).getJSONObject("response").getJSONArray("items");
        	for(int i = 0; i < friendIds.length(); i++){
        		Thread.sleep(Integer.parseInt(propertyManager.getProp(Properties.REQUESTS_DELAY, propertyManager.DEFAULT_REQUESTS_DELAY)));
        		String friendIdResponse = invokeApi("friends.get", Params.create()
            			.add("user_id", friendIds.get(i).toString()));
        		if(friendIdResponse.contains("response") && friendIdResponse.contains("items")){
        			JSONArray friendOfFriendIds = new JSONObject(friendIdResponse).getJSONObject("response").getJSONArray("items");
            		userIds = JSONUtils.concatArray(userIds, friendOfFriendIds);
        		}
        	}
        }catch(IOException e){
        	Logger.error(e);
        } catch (JSONException e) {
        	Logger.error(e);
		} catch (InterruptedException e) {
			Logger.error(e);
		}
        if(userIds.length() > 0) userIds = JSONUtils.removeDuplicateIds(userIds);
        
		return userIds.toString();
    }
	
	public String getWallPosts(String owner_id){
        String wallPosts = "";
        try{
        	wallPosts = invokeApi("wall.get", Params.create()
                    .add("owner_id", owner_id)
                    .add("count", "100"));
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return wallPosts;
    }
	
	public String doRepost(String objectId){
        String responce = "";
        try{
        	responce = invokeApi("wall.repost", Params.create()
                    .add("object", objectId));        	
        	if(responce.contains("error") && responce.contains("captcha")){
        		//process captcha
        		Logger.error("CAPTCHA!!!!!!!!");
        	}else if(responce.contains("success")){
        		DupDetector.getInstance().addPostedId(objectId);
        		Logger.info("## Post is reposted, result: "+responce);
        	}else{
        		Logger.info("Post is not reposted, result: "+responce);
        	}
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return responce;
    }
	
	public boolean putLike(String ownerId, String itemId){
		boolean isLiked = false;
        try{
        	String responce = invokeApi("likes.add", Params.create()
                    .add("owner_id", ownerId)
                    .add("item_id", itemId)
                    .add("type", "post"));        	
        	if(responce.contains("error") && responce.contains("captcha")){
        		//process captcha
        		Logger.error("CAPTCHA!!!!!!!!");
        	}else if(responce.contains("likes")){
        		isLiked = true;
        		Logger.info("## Post is liked, result: "+responce);
        	}else{
        		Logger.info("Post is not liked, result: "+responce);
        	}
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return isLiked;
    }
	
	public boolean isLiked(String ownerId, String itemId){
		boolean isLiked = false;
        try{
        	String responce = invokeApi("likes.isLiked", Params.create()
                    .add("owner_id", ownerId)
                    .add("item_id", itemId)
                    .add("type", "post"));        	
        	if(responce.contains("error") && responce.contains("captcha")){
        		//process captcha
        		Logger.error("CAPTCHA!!!!!!!!");
        	}else if(responce.contains("\"liked\":1")){
        		isLiked = true;        		
        	}
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return isLiked;
    }
	
	public boolean joinCommunity(String communityId){
		boolean isJoined = false;
        try{
        	String responce = invokeApi("groups.join", Params.create()
                    .add("group_id", communityId));
        	if(responce.contains("error") && responce.contains("captcha")){
        		//process captcha
        		Logger.error("CAPTCHA!!!!!!!!");
        	}else if(responce.contains("\"response\":1")){
        		isJoined = true;
        		Logger.info("## Community is joined, result: "+responce);
        	}else{
        		Logger.info("Community is not joined, result: "+responce);
        	}        	
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return isJoined;
    }
	
	public String searchCommunities(String searchWord){
        String responce = "";
        try{
        	responce = invokeApi("groups.search", Params.create()
                    .add("q", searchWord)
                    .add("count", "1000"));
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return responce;
    }
	
	public String getCommunityById(String communityId){
        String responce = "";
        try{
        	responce = invokeApi("groups.getById", Params.create()
                    .add("group_id", communityId));
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return responce;
    }
	
	public String getUserCommunities(String userId){
        String responce = "";
        try{
        	responce = invokeApi("groups.get", Params.create()
                    .add("user_id", userId)
                    .add("count", "1000"));
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return responce;
    }
	
	public String getPrivateMessages(){
        String responce = "";
        try{
        	responce = invokeApi("messages.get", Params.create()
                    .add("out", "0")
                    .add("count", "200")
                    .add("time_offset", "0"));
        }catch(IOException e){
        	Logger.error(e, "Error in API invoke");
        }
		return responce;
    }
	
	private String invokeApi(String method, Params params) throws IOException {
        final String parameters = (params == null) ? "" : params.build();
        String reqUrl = API_REQUEST
                .replace("{METHOD_NAME}", method)
                .replace("{ACCESS_TOKEN}", accessToken)
                .replace("{PARAMETERS}&", parameters);
        return invokeApi(reqUrl);
    }

    private static String invokeApi(String requestUrl) throws IOException {
    	Logger.info(requestUrl);
    	final StringBuilder result = new StringBuilder();
        final URL url = new URL(requestUrl);
        try (InputStream is = url.openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            reader.lines().forEach(result::append);
        }
        return result.toString();
    }   
	
	private static class Params {
	        
	        public static Params create() {
	            return new Params();
	        }

	        private final HashMap<String, String> params;
	        
	        private Params() {
	            params = new HashMap<>();
	        }
	        
	        public Params add(String key, String value) {
	            params.put(key, value);
	            return this;
	        }
	        
	        public String build() {
	            if (params.isEmpty()) return "";
	            final StringBuilder result = new StringBuilder();
	            params.keySet().stream().forEach(key -> {
	                result.append(key).append('=').append(params.get(key)).append('&');
	            });
	            return result.toString();
	        }
	    }
}
