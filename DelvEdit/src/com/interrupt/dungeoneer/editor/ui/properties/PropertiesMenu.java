package com.interrupt.dungeoneer.editor.ui.properties;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.interrupt.dungeoneer.annotations.EditorProperty;
import com.interrupt.dungeoneer.editor.ui.EditorUi;
import com.interrupt.dungeoneer.entities.Group;
import com.interrupt.dungeoneer.entities.Prefab;

public class PropertiesMenu extends Table {
    private final Array<?> objects;

    private final InputFieldListener onChangeListener = new InputFieldListener() {
        @Override
        public void onChange(final InputField inputField, final Field field, final Object value) {
            PropertiesMenu.this.handleOnChange(inputField, field, value);
        }
    };

    public <T> PropertiesMenu(final T object, final Skin skin) {
        super(skin);

        final Array<T> objects = new Array<>();
        objects.add(object);

        this.objects = objects;
        render(skin);
    }

    public <T> PropertiesMenu(final Array<T> objects, final Skin skin) {
        super(skin);

        this.objects = objects;
        render(skin);
    }

    private final void render(final Skin skin) {
        renderHeader();
        renderGroups(skin);
    }

    private final void renderHeader() {
        final String objectName = getObjectName(objects.get(0));

        add(new Label(objectName, EditorUi.mediumSkin)).align(Align.left).padLeft(-12f);
        row();
    }

    private final void renderGroups(final Skin skin) {
        final Array<Class<?>> classes = getCommonClassesForObjects(objects);
        final ArrayMap<String, Array<Field>> fieldGroups = getFieldGroups(classes, objects.get(0));
        final Array<InputField> inputFields = getAvailableInputFields();

        for (final Entry<String, Array<Field>> item : fieldGroups.entries()) {
            final String groupName = item.key;
            final Array<Field> fields = item.value;
            if (fields == null || fields.size == 0 || !shouldRenderGroup(groupName, classes))
                continue;

            renderGroupHeader(groupName);

            for (final Field field : fields) {
                InputField inputField = null;

                for (final InputField f : inputFields) {
                    if (f.canHandle(field)) {
                        inputField = f;
                        break;
                    }
                }

                if (inputField == null) {
                    throw new RuntimeException("No InputField implementation defined to handle Field of type: '"
                            + field.getType().toString() + "'");
                }

                inputField.renderLabel(field.getName(), this, skin);
                inputField.renderField(field, getCommonValueForObjects(field, objects), onChangeListener, this, skin);
            }
        }

        pack();
    }

    private final void renderGroupHeader(final String groupName) {
        add(groupName).colspan(2).align(Align.left).padLeft(-8f);
        row();
    }

    private final <T> String getObjectName(final T object) {
        final String[] nameParts = object.getClass().getName().split("\\.");

        String name = "Unknown Object";
        if (nameParts.length > 0) {
            name = nameParts[nameParts.length - 1];
        }

        return name;
    }

    private final <T> Array<Class<?>> getCommonClassesForObjects(final Array<T> objects) {
        if (objects.size <= 0) {
            return new Array<>();
        } else if (objects.size <= 1) {
            return new Array<>(getClassesForObject(objects.get(0)));
        } else {
            final Array<Class<?>> classes = getClassesForObject(objects.get(0));

            // Remove any non-common classes.
            final Array<Class<?>> nonCommonClasses = new Array<>();
            for (final T o : objects) {
                final Array<Class<?>> classesToCheck = getClassesForObject(o);
                for (final Class<?> existingClass : classes) {
                    if (!classesToCheck.contains(existingClass, true)) {
                        nonCommonClasses.add(existingClass);
                    }
                }
            }
            classes.removeAll(nonCommonClasses, true);

            return classes;
        }
    }

    private final <T> Array<Class<?>> getClassesForObject(final T object) {
        final Array<Class<?>> classes = new Array<>();

        Class<?> c = object.getClass();
        while (c != null) {
            classes.add(c);
            c = c.getSuperclass();
        }

        return classes;
    }

    private final <T> ArrayMap<String, Array<Field>> getFieldGroups(final Array<Class<?>> classes, final T object) {
        final ArrayMap<String, Array<Field>> groups = new ArrayMap<>();

        for (final Class<?> c : classes) {
            final Field[] fields = c.getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {
                final Field field = fields[i];

                try {
                    field.setAccessible(true);
                } catch (final Exception ignored) {
                    continue;
                }

                final String groupName = getEditorPropertyGroupName(field, object);
                if (!field.getType().isArray() && !Modifier.isTransient(field.getModifiers())
                        && isEditorProperty(field)) {
                    Array<Field> groupItems = groups.get(groupName);
                    if (groupItems == null) {
                        groupItems = new Array<Field>();
                        groups.put(groupName, groupItems);
                    }

                    groupItems.add(field);
                }
            }
        }

        return groups;
    }

    private final <T> Object getCommonValueForObjects(final Field field, final Array<T> objects) {
        Object commonValue = null;

        for (final T o : objects) {
            Object value = null;
            try {
                value = field.get(o);
            } catch (final Exception e) {
                continue;
            }

            if (commonValue == null && value != null) {
                commonValue = value;
            } else if (commonValue != null && !commonValue.equals(value)) {
                return null;
            }
        }

        return commonValue;
    }

    private final <T> String getEditorPropertyGroupName(final Field field, final T object) {
        final EditorProperty annotation = field.getAnnotation(EditorProperty.class);

        if (annotation != null && !annotation.group().equals("")) {
            return annotation.group();
        }

        return object.getClass().getSimpleName();
    }

    private final boolean isEditorProperty(final Field field) {
        final Annotation annotation = field.getAnnotation(EditorProperty.class);
        return (annotation != null);
    }

    private final <T> boolean shouldRenderGroup(final String groupName, final Array<Class<?>> classes) {
        if (classes.contains(Prefab.class, false)) {
            return !(groupName.equals("Prefab") || groupName.equals("MonsterPrefab"));
        } else if (classes.contains(Group.class, false)) {
            return !(groupName.equals("Group") || !groupName.equals("General"));
        }

        return true;
    }

    private final Array<InputField> getAvailableInputFields() {
        final Array<InputField> inputFields = new Array<>();

        inputFields.add(new TextInputField());

        return inputFields;
    }

    private final void handleOnChange(final InputField inputField, final Field field, final Object value) {
        for (final Object object : objects) {
            inputField.apply(field, object, value);
        }
    }
}
