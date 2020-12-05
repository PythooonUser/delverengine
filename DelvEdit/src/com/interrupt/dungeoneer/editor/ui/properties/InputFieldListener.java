package com.interrupt.dungeoneer.editor.ui.properties;

import java.lang.reflect.Field;

public interface InputFieldListener {
    public void onChange(InputField inputField, Field field, Object value);
}
