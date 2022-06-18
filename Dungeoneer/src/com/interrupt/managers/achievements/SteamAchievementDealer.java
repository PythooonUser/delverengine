package com.interrupt.managers.achievements;

import com.interrupt.api.steam.SteamApi;
import com.interrupt.dungeoneer.achievements.Achievement;
import com.interrupt.dungeoneer.achievements.ProgressionAchievement;

public class SteamAchievementDealer implements AchievementDealerInterface {
    @Override
    public void achieve(Achievement achievement) {
        SteamApi.api.achieve(achievement.identifier);
    }

    @Override
    public void achieve(ProgressionAchievement achievement, int currentProgress) {
        SteamApi.api.achieve(achievement.identifier, currentProgress, achievement.maxProgress);
    }

    @Override
    public void achieve(String name) {
        SteamApi.api.achieve(name);
    }
}
