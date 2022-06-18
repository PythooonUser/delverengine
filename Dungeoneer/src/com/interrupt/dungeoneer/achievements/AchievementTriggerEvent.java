package com.interrupt.dungeoneer.achievements;

public class AchievementTriggerEvent {
    public AchievementTriggerType triggerType;
    public int current;

    public AchievementTriggerEvent(AchievementTriggerType triggerType, int current) {
        this.triggerType = triggerType;
        this.current = current;
    }
}
