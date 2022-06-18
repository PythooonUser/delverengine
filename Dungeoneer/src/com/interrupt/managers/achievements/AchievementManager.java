package com.interrupt.managers.achievements;

import com.badlogic.gdx.utils.Array;
import com.interrupt.dungeoneer.achievements.Achievement;
import com.interrupt.dungeoneer.achievements.AchievementTriggerEvent;

public class AchievementManager {
    public AchievementDealerInterface achievementDealer = new NullAchievementDealer();
    public Array<Achievement> achievements = new Array<>();

    public void merge(AchievementManager other) {
        if (null != achievementDealer) {
            achievementDealer = other.achievementDealer;
        }

        achievements = other.achievements;
    }

    public void triggerAchievement(AchievementTriggerEvent event) {
        for (Achievement achievement : achievements) {
            if (achievement.isFulfilled(event)) achievementDealer.achieve(achievement);
        }
    }
}
