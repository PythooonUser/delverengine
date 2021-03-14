package com.interrupt.dungeoneer.requirements;

import com.interrupt.dungeoneer.annotations.EditorProperty;
import com.interrupt.dungeoneer.entities.Player;

public abstract class Requirement {
    /** The count type. */
    @EditorProperty
    public CountType type = CountType.GREATER_THAN_OR_EQUAL;

    /** Implement this for custom test conditions. */
    public abstract boolean test(Player player);

    protected boolean test(int expected, int actual) {
        if (type == CountType.GREATER_THAN_OR_EQUAL) {
            return actual >= expected;
        }

        return false;
    }

    /** Implement this for custom consume behaviors. */
    public abstract void onConsume(Player player);

    public void execute(Player player) {
        onConsume(player);
    }
}
