package com.interrupt.managers.achievements;

public class AchievementManager {
    public AchievementDealerInterface achievementDealer = new NullAchievementDealer();

    public void merge(AchievementManager other) {
        if (null != achievementDealer) {
            achievementDealer = other.achievementDealer;
        }
    }
}
