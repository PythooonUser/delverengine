package com.interrupt.managers.achievements;

import com.interrupt.api.steam.SteamApi;

public class SteamAchievementDealer implements AchievementDealerInterface {
    @Override
    public void achieve(String achievementName) {
        SteamApi.api.achieve(achievementName);
    }

    @Override
    public void achieve(String achievementName, int currentProgress, int maxProgress) {
        SteamApi.api.achieve(achievementName, currentProgress, maxProgress);
    }
}
