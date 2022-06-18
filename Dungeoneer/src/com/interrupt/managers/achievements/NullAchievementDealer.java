package com.interrupt.managers.achievements;

import com.badlogic.gdx.Gdx;
import com.interrupt.dungeoneer.achievements.Achievement;
import com.interrupt.dungeoneer.achievements.ProgressionAchievement;

public class NullAchievementDealer implements AchievementDealerInterface {
    @Override
    public void achieve(Achievement achievement) {
        Gdx.app.log("Achievement", achievement.title + " unlocked!");
    }

    @Override
    public void achieve(ProgressionAchievement achievement, int currentProgress) {
        Gdx.app.log("Achievement",
                achievement.title + " (" + currentProgress + "/" + achievement.maxProgress + ")" + " unlocked!");
    }

    @Override
    public void achieve(String name) {
        Gdx.app.log("Achievement", name + " unlocked!");
    }
}
