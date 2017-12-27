Properties for the bot are stored in *conf* directory. There are properties for logging in *tinylog.properties*, properties for vk.com API - *vk.api.properties* and properties for app - *vkb.properties* - the most interesting for us. There are five **required properties**:

-  **current.user.id** - identification number of your account. You can get it from URL of your account page of from settings (e.g. 294238387)
-  **application.id** - identification number of application (it's used for API requests to vk.com).  Go to [create app page](https://vk.com/editapp?act=create) and create new Standalone-application. After creation and verification you get application ID (e.g. 5062591).
-  **access.token** - the key that is used for access your application to vk.com API. After successful application authorization, user's browser will be redirected to REDIRECT_URI URL specified when the authorization dialog box was opened. With that, access.token access key for API and other parameters will be passed in URL part of the link (e.g. 533bacf01e11f55b536a565b57531ad114461ae8736d6506a3).
- **communities.search.words** - comma-separated list of key words, which will be used for searching of competition communities (e.g. *competition,gifts,prizes*). Since most vk.com users to communicate in russian, I suggest to use keywords in russian. And since property-files has ASCII format it would look something like *\u043A\u043E\u043D\u043A\u0443\u0440\u0441,\u0440\u043E\u0437\u044B\u0433\u0440\u044B\u0448* (that means *конкурс,розыгрыш*).
-  **post.classification.model** - the model that allow identify competition posts. It has such structure: 
```
{"classification_groups":["key-word1,key-word2,key-word3","key-word4,key-word5,key-word6"]}
```
which on logical level looks like 

```
(postMessage.contains(key-word1)||postMessage.contains(key-word2)||postMessage.contains(key-word3))
&&
(postMessage.contains(key-word4)||postMessage.contains(key-word5)||postMessage.contains(key-word6))
```
and if this condition is true, the post is determined as a competition-post. Key-words in each group are also list of comma-separated words and there may be any number of groups. For example, if we have such model *{"classification_groups":["competition,prize","repost,share"]}*, the script will mark all posts as a competition posts, which contains one or more words from first group ("competition" or "prize") and also contains one or more words from second group ("repost" or "share"). Like in case with *communities.search.words* property, here I also suggest use russian.

Also there are a list of non-required properties:

-  **requests.delay** and **reposts.delay** - are used to avoid flood control and captcha verification. By default has values 1000 and 450000 (in milliseconds).
-  **timer.start.delay** and **tasks.execution.period** - are used for scheduling different tasks execution (java.util.Timer and java.util.TimerTask are used). By default has values 0 and 14400000 (in milliseconds too).
- **random.reposts.communities.ids** - comma-separated list of community IDs (e.g. *-35283306,-22886007*, minus character is important here!). It's used for simulation of "real-user" behavior for avoiding blocking. By default is empty.
-  **posted.db.name**, **community.black.list.db.name**, **postponed.communities.db.name**, **postponed.users.db.name** - names of DB storage that are used for storing ID's of posts, communities and users (for avoiding repetitive reposts). By default has such values respectively: "db\\postedIds.db", "db\\communityBlackList.db", "db\\postponedCommunities.db", "db\\postponedUsers.db".
-  **posted.set.name**, **community.black.list.name**, **postponed.communities.list.name**, **postponed.users.list.name** - names of DB tables that are used for storing ID's of posts, communities and users (entities from previous list item). By default has such values respectively: "postedIds", "communityBlackList", "postponedCommunities", "postponedUsers".
- **percentage.of.requests.for.friendhship** - value that is used for calculation of friendship requests amount (is also used for simulation of "real-user" behavior). Important - you already need to have some friends, because script looks for new friends among friends of friends. Default value is 2.
-  **blocked.words** - comma-separated list of words, that are undesirable in competition posts and names of competition communities (for example *cosmetics,steam*). Here also preferable to use russian. By default is empty.

I put an example of configured properties to gist - [vkb.properties](https://gist.github.com/gleb-kosteiko/d5a4e2c6b40104d88e45).
