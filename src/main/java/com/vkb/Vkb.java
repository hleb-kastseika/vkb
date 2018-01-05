package com.vkb;

import com.vkb.api.VkClient;
import com.vkb.strategies.AddFriendsStrategy;
import com.vkb.strategies.BehaviourStrategy;
import com.vkb.strategies.CheckCurrentCommunitiesWallsStrategy;
import com.vkb.strategies.CheckFriendsWallsStrategy;
import com.vkb.strategies.CheckPrivateMessagesStrategy;
import com.vkb.strategies.PostponedActionsStrategy;
import com.vkb.strategies.RepostingStrategy;
import com.vkb.strategies.SearchCompetitionNewsStrategy;
import com.vkb.strategies.SearchForCompetitionCommunitiesStrategy;
import com.vkb.utils.Properties;
import com.vkb.utils.PropertiesManager;
import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Vkb {
    private static PropertiesManager propertyManager = PropertiesManager.getInstance();

    public static void main(String[] args) {
        Logger.info("## Vkb is started");
        VkClient vkClient = VkClient.with(propertyManager.getProp(Properties.APP_ID),
                propertyManager.getProp(Properties.ACCESS_TOKEN));

        ArrayList<BehaviourStrategy> strategies = new ArrayList<BehaviourStrategy>();
        strategies.add(new AddFriendsStrategy(vkClient));
        strategies.add(new CheckFriendsWallsStrategy(vkClient));
        strategies.add(new CheckCurrentCommunitiesWallsStrategy(vkClient));
        strategies.add(new RepostingStrategy(vkClient));
        strategies.add(new SearchCompetitionNewsStrategy(vkClient));
        strategies.add(new SearchForCompetitionCommunitiesStrategy(vkClient));
        strategies.add(new CheckPrivateMessagesStrategy(vkClient));
        strategies.add(new PostponedActionsStrategy(vkClient));

        Timer timer = new Timer();
        for (BehaviourStrategy bs : strategies) {
            TimerTask task = new TimerTask() {
                public void run() {
                    bs.execute();
                }
            };
            timer.scheduleAtFixedRate(task,
                    Long.valueOf(propertyManager.getProp(Properties.TIMER_START_DELAY,
                            propertyManager.DEFAULT_TIMER_START_DELAY)),
                    Long.valueOf(propertyManager.getProp(Properties.TASKS_EXECUTION_PERIOD,
                            propertyManager.DEFAULT_TASKS_EXECUTION_PERIOD)));
        }
    }
}
