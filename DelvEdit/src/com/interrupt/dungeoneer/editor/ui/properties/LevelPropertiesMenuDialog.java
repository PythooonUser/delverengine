package com.interrupt.dungeoneer.editor.ui.properties;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.interrupt.dungeoneer.game.Level;

public class LevelPropertiesMenuDialog extends Dialog {
    public LevelPropertiesMenuDialog(Level level, Skin skin) {
        super("Level Properties", skin);

        getContentTable().add(new LevelPropertiesMenu(level, skin));
        getContentTable().row();

        button("Save", true);
        button("Cancel", false);
    }
}
