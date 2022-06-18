package com.interrupt.dungeoneer.achievements;

import com.interrupt.dungeoneer.game.Game;

public class Achievement {
    /** The identifier of the achievement, e.g. for usage with Steam. */
    public String identifier;

    /** An optional title of the achievement visible to players. */
    public String title;

    /** An optional description of the achievement visible to players. */
    public String description;

    /** The trigger associated with this achievement. */
    public AchievementTrigger trigger;

    public boolean isFulfilled(AchievementTriggerEvent event) {
        if (trigger.triggerType != event.triggerType)
            return false;

        if (trigger.playerNeedsToBeAlive) {
            if (null == Game.instance || null == Game.instance.player || !Game.instance.player.isAlive())
                return false;
        }

        if (trigger.conditionType == AchievementConditionType.GREATER) {
            if (event.current > trigger.value)
                return true;
        }

        if (trigger.conditionType == AchievementConditionType.GREATER_EQUAL) {
            if (event.current >= trigger.value)
                return true;
        }

        return false;
    }
}
