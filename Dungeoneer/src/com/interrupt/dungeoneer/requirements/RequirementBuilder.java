package com.interrupt.dungeoneer.requirements;

import com.badlogic.gdx.utils.Array;
import com.interrupt.dungeoneer.annotations.EditorProperty;
import com.interrupt.dungeoneer.game.Game;

public class RequirementBuilder {
    /** The collection type. */
    @EditorProperty
    public CollectionType type = CollectionType.ALL;

    /** Should the thing being required being consumed on fullfilment? */
    @EditorProperty
    public boolean consume = true;

    /** The required gold amount, items etc. */
    @EditorProperty
    public Array<Requirement> requirements = new Array<>();

    public boolean test() {
        int successCount = 0;

        for (int i = 0; i < requirements.size; i++) {
            if (requirements.get(i).test(Game.instance.player))
                successCount++;
        }

        if (type == CollectionType.ANY && successCount > 0) {
            return true;
        }

        if (type == CollectionType.ALL && successCount == requirements.size) {
            return true;
        }

        return false;
    }

    public void execute() {
        if (consume) {
            if (type == CollectionType.ALL) {
                for (int i = 0; i < requirements.size; i++) {
                    requirements.get(i).onConsume(Game.instance.player);
                }
                return;
            }

            if (type == CollectionType.ANY) {
                for (int i = 0; i < requirements.size; i++) {
                    if (requirements.get(i).test(Game.instance.player)) {
                        requirements.get(i).onConsume(Game.instance.player);
                        return;
                    }
                }
            }
        }
    }
}
