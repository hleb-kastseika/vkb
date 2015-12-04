# vkb - bot for vk.com competitions

Script allows you to automate the searching and participation in random reposts competitions in vk.com.

### Main capabilities
- Check friends walls for competition posts and repost these posts (also joined all needed communities and added all needed users to friends) 
- Do reposts of random posts from mentioned communities (for  simulation of the real user behavior)
- To add random friends (also for simulation, like previose one)
- Search competition posts in VK search and repost it
- Search competition posts in already joined communities and repost it
- Search competition communities, check walls for competitions, reposts these posts and join communities

### Requirements for building and running
- Java 8
- Gradle 2.8 and latest
- an account in vk.com that is not a pity to lose (admins are struggling with fake accounts and it can be banned)

### How to build, configure and run
For **building the project** use Gradle. Go to the root project directory and run `gradle dist` command. It will generate zip archive in *build\dist* directory. Extract it somewhere and script is almost ready for running (just need to link it with your VK account and setup some properties).

Also there is ability to deploy the bot to [Heroku](https://www.heroku.com/). This process is described [here](https://gist.github.com/last-khajiit/b6fa4eef443d3d753fce). 

Now about the **configuration**.
First, you need to have an account in vk.com, which you'll use for running the script. I would recommend to use an account that is not a pity to lose, because it can be banned for "strange activity".
The next thing you need to do - to configure some script properties. Go to *conf* directory and find there *vkb.properties* file. Here is an example of configured properties - [vkb.properties](https://gist.github.com/last-khajiit/d5a4e2c6b40104d88e45). There are five **required properties**:

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


*Feel free to make pull requests!*


---

**Copyright © 2015 Last Khajiit <last.khajiit@gmail.com>**

This work is free. You can redistribute it and/or modify it under the
terms of the Do What The Fuck You Want To Public License, Version 2,
as published by Sam Hocevar. See the [COPYING](https://raw.githubusercontent.com/last-khajiit/vkb/master/copying.txt) file for more details.
