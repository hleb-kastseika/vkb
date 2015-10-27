# vkb - script for vk.com competitions

### Main capabilities
1. Check friends walls for competition posts and repost these posts (also joined all needed communities and added all needed users to friends) 
2. Do reposts of random posts from mentioned communities (for  simulation of the real user behavior)
3. To add random friends (also for simulation, like previose one)
4. Search competition posts in VK search and repost it
5. Search competition posts in already joined communities and repost it
6. Search competition communities, check walls for competitions, reposts these posts and join communities

### Requirements for building and running
- Java 8
- Apache Ant (I use 1.9.5 version, didn't check with previous)
- some Windows OS (I developed it on Windows and hardcoded windows path separator "\". So, for correct running on other OS, modify it).

### How to build, configure and run
For **building the project** use Apache Ant. I don't use any dependency management tools (like Ivy or Maven, so all libraries that you need are placed in *lib* directory). Go to the root project directory and run `ant dist` command. It will generate zip archive in *dist* directory. Extract it somewhere and script is almost ready for running (just need to link it with your VK account and setup some properties).

Now about the **configuration**.
