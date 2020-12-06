package com.interrupt.dungeoneer.editor.ui.properties;

import java.lang.reflect.Field;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.utils.Align;

public final class TextInputField extends InputField {
    @Override
    public final boolean canHandle(final Field field) {
        return field.getType() == String.class;
    }

    @Override
    public final void renderField(final Field field, final Object value, final InputFieldListener onChangeListener,
            final Table table, final Skin skin) {
        final TextField textField = new TextField((value != null ? value.toString() : ""), skin);

        final TextFieldListener listener = new TextFieldListener() {
            public void keyTyped(final TextField textField, final char key) {
                TextInputField.this.notifyListenerAboutChange(onChangeListener, field, textField.getText());
            }
        };
        textField.setTextFieldListener(listener);

        table.add(textField).align(Align.left).fill();
    }
}
