package com.interrupt.managers.achievements;

public interface AchievementDealerInterface {
    public void achieve(String achievementName);

    public void achieve(String achievementName, int currentProgress, int maxProgress);
}
