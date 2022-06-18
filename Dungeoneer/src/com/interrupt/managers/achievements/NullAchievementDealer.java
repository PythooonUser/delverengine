package com.interrupt.managers.achievements;

import com.badlogic.gdx.Gdx;

public class NullAchievementDealer implements AchievementDealerInterface {
    @Override
    public void achieve(String achievementName) {
        Gdx.app.log("Achievement", achievementName + " unlocked!");
    }

    @Override
    public void achieve(String achievementName, int currentProgress, int maxProgress) {
        Gdx.app.log("Achievement", achievementName + " (" + currentProgress + "/" + maxProgress + ")" + " unlocked!");
    }
}
