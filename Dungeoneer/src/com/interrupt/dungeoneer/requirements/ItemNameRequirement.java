package com.interrupt.dungeoneer.requirements;

import com.badlogic.gdx.utils.Array;
import com.interrupt.dungeoneer.annotations.EditorProperty;
import com.interrupt.dungeoneer.entities.Item;
import com.interrupt.dungeoneer.entities.Player;
import com.interrupt.dungeoneer.game.Game;

public final class ItemNameRequirement extends Requirement {
    /** The name of the item. */
    @EditorProperty
    public String name;

    /** The required item count. */
    @EditorProperty
    public int count = 1;

    public boolean test(Player player) {
        return test(count, getItemCountInInventory(name, player.inventory));
    }

    private int getItemCountInInventory(String name, Array<Item> inventory) {
        int itemCount = 0;

        for (int i = 0; i < inventory.size; i++) {
            Item item = inventory.get(i);

            if (item == null)
                continue;

            if (item.name.equals(name)) {
                itemCount++;
            }
        }

        return itemCount;
    }

    public void onConsume(Player player) {
        Item itemToConsume = null;

        for (int i = 0; i < player.inventory.size; i++) {
            Item item = player.inventory.get(i);

            if (item == null)
                continue;

            if (item.name.equals(name)) {
                itemToConsume = item;
            }
        }

        if (itemToConsume != null) {
            onConsume(player, itemToConsume);
        }
    }

    private void onConsume(Player player, Item item) {
        int location = player.inventory.indexOf(item, true);
        if (location == -1)
            return;

        player.inventory.set(location, null);
        Game.RefreshUI();
    }
}
