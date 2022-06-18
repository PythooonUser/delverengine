package com.interrupt.managers.achievements;

import com.interrupt.dungeoneer.achievements.Achievement;
import com.interrupt.dungeoneer.achievements.ProgressionAchievement;

public interface AchievementDealerInterface {
    public void achieve(Achievement achievement);

    public void achieve(ProgressionAchievement achievement, int currentProgress);

    /** @deprecated */
    public void achieve(String name);
}
