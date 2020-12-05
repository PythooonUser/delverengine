package com.interrupt.dungeoneer.editor.ui.properties;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.interrupt.dungeoneer.entities.Entity;

public class EntityPropertiesMenu extends PropertiesMenu {
    public EntityPropertiesMenu(Skin skin, final Entity entity) {
        super(skin, entity);
    }

    public EntityPropertiesMenu(Skin skin, final Array<Entity> entities) {
        super(skin, entities);
    }
}
