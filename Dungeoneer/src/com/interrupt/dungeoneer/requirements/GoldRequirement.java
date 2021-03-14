package com.interrupt.dungeoneer.requirements;

import com.interrupt.dungeoneer.annotations.EditorProperty;
import com.interrupt.dungeoneer.entities.Player;

public final class GoldRequirement extends Requirement {
    /** The required gold amount. */
    @EditorProperty
    public int amount = 1;

    public boolean test(Player player) {
        return test(amount, player.gold);
    }

    @Override
    public void onConsume(Player player) {
        player.gold -= amount;
    }
}
