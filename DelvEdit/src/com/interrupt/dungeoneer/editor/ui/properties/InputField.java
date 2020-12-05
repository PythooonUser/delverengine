package com.interrupt.dungeoneer.editor.ui.properties;

import java.lang.reflect.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public abstract class InputField {
    public abstract boolean canHandle(final Field field);

    public final void render(final String fieldName, final Field field, final Object value,
            final InputFieldListener listener, final Table table, final Skin skin) {
        renderLabel(fieldName, table, skin);
        renderField(field, value, listener, table, skin);
    }

    private final void renderLabel(final String fieldName, final Table table, final Skin skin) {
        final Label label = new Label(fieldName, skin);
        label.setColor(1f, 1f, 1f, 0.75f);
        table.add(label).align(Align.left);
    }

    protected abstract void renderField(final Field field, final Object value,
            final InputFieldListener onChangeListener, final Table table, final Skin skin);

    public void notifyListenerAboutChange(final InputFieldListener listener, final Field field, final Object value) {
        listener.onChange(this, field, value);
    }

    protected void apply(final Field field, final Object object, final Object value) {
        try {
            field.set(object, value);
        } catch (final Exception exception) {
            Gdx.app.error("DelvEdit", exception.getMessage());
        }
    }
}
