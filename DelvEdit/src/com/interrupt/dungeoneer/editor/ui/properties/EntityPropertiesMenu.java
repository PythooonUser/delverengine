package com.interrupt.dungeoneer.editor.ui.properties;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.interrupt.dungeoneer.entities.Entity;

public final class EntityPropertiesMenu extends PropertiesMenu {
    public EntityPropertiesMenu(final Entity entity, final Skin skin) {
        super(entity, skin);
    }

    public EntityPropertiesMenu(final Array<Entity> entities, final Skin skin) {
        super(entities, skin);
    }
}
