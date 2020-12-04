package com.interrupt.dungeoneer.editor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.StringBuilder;
import com.interrupt.dungeoneer.annotations.EditorProperty;
import com.interrupt.dungeoneer.editor.Editor;
import com.interrupt.dungeoneer.editor.EditorArt;
import com.interrupt.dungeoneer.entities.*;
import com.interrupt.dungeoneer.gfx.TextureAtlas;
import com.interrupt.dungeoneer.gfx.Material;
import org.lwjgl.LWJGLUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.HashMap;

public class PropertiesMenu extends Table {
    private HashMap<Field, Actor> fieldMap = new HashMap<Field, Actor>();

    private final Array<Entity> selectedEntities;

    public PropertiesMenu(Skin skin, final Array<Entity> entities) {
        super(skin);
        final Entity entity = entities.get(0);
        selectedEntities = entities;

        createMenuHeader(entity);
        Array<Class<?>> classes = getCommonClassesForObjects(entities);

        try {
            ArrayMap<String, Array<Field>> fieldGroups = getFieldGroups(classes, entity);

            // loop through the groups
            for (ObjectMap.Entry<String, Array<Field>> item : fieldGroups.entries()) {
                String groupName = item.key;
                Array<Field> fields = item.value;
                if (fields == null) continue;

                if (!shouldRenderGroup(entity, groupName)) {
                    continue;
                }

                if(fields.size == 0) continue;
                createGroupHeader(groupName);

                // loop through the fields in the group
                for(final Field field : fields) {
                    Object value = getCommonValueForObjects(field, entities);

                    Label label = new Label(field.getName(), skin);
                    label.setColor(1f, 1f, 1f, 0.75f);

                    if (isSelectBoxType(field)) {
                        renderSelectBoxType(field, value, label, skin);
                    }
                    else if (isFilePickerType(field)) {
                        renderFilePickerType(field, value, label, skin);
                    }
                    else if(field.getType() == String.class) {
                        TextField tf = new TextField((value != null ? value.toString() : ""), skin);
                        tf.setTextFieldListener(getTextFieldListener(field));

                        add(label).align(Align.left);
                        add(tf).align(Align.left).fill();

                        fieldMap.put(field, tf);
                    }
                    else if(field.getType() == Material.class) {
                        Material val = (Material)value;

                        final TextureAtlas atlas = val == null ? null : TextureAtlas.getCachedRegion(val.texAtlas);
                        ImageButton button = null;

                        try {
                            ImageButton.ImageButtonStyle buttonStyle =
                                    new ImageButton.ImageButtonStyle(skin.get(ImageButton.ImageButtonStyle.class));

                            int texVal = 0;

                            if(val != null) {
                                texVal = val.tex;
                                if (texVal < 0) texVal = 0;
                                if (texVal > atlas.getSpriteRegions().length) texVal = atlas.getSpriteRegions().length;

                                Drawable sprite = new TextureRegionDrawable(atlas.getClippedSprite(val.tex));
                                buttonStyle.imageUp = sprite;
                                buttonStyle.imageOver = sprite;
                                buttonStyle.imageDown = sprite;
                            }

                            button = new ImageButton(buttonStyle);
                            button.getImage().setScaling(Scaling.stretch);

                            float scaleMod = 1f / atlas.getClippedSizeMod()[texVal].y;
                            button.getImageCell().height(40f).width(40f * atlas.getClippedSizeMod()[texVal].x * scaleMod);
                        }
                        catch(Exception ex) {
                            ImageButton.ImageButtonStyle buttonStyle =
                                    new ImageButton.ImageButtonStyle(skin.get(ImageButton.ImageButtonStyle.class));

                            buttonStyle.imageUp = null;
                            buttonStyle.imageOver = null;
                            buttonStyle.imageDown = null;

                            button = new ImageButton(buttonStyle);
                        }

                        button.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                TextureRegionPicker picker = new TextureRegionPicker("Pick Sprite", EditorUi.smallSkin, atlas != null ? atlas.name : null, TextureAtlas.getAllSpriteAtlases()) {
                                    @Override
                                    public void result(Integer value, String atlas) {
                                        if(value != null) {
                                            int v = value;
                                            applyChanges(field, new Material(atlas, (byte)v));
                                        }

                                        Editor.app.ui.showEntityPropertiesMenu(false);
                                    }
                                };
                                Editor.app.ui.getStage().addActor(picker);
                                picker.show(Editor.app.ui.getStage());
                            }
                        });

                        add(label).align(Align.left);
                        add(button).height(58f).align(Align.left).fill();

                        fieldMap.put(field, button);
                    }
                    else if((field.getType() == int.class) && isAtlasRegionPicker(field)) {
                        Integer val = (Integer)value;
                        if(val == null) val = 0;

                        String spriteAtlas = null;
                        try {
                            spriteAtlas = ((Entity)entity).spriteAtlas;
                        } catch (Exception ignored) { }

                        final TextureAtlas atlas = TextureAtlas.getCachedRegion(spriteAtlas);
                        ImageButton button = null;

                        try {
                            if(val < 0) val = 0;
                            if (val > atlas.getSpriteRegions().length) val = atlas.getSpriteRegions().length;

                            ImageButton.ImageButtonStyle buttonStyle =
                                    new ImageButton.ImageButtonStyle(skin.get(ImageButton.ImageButtonStyle.class));

                            Drawable sprite = new TextureRegionDrawable(atlas.getClippedSprite(val));
                            buttonStyle.imageUp = sprite;
                            buttonStyle.imageOver = sprite;
                            buttonStyle.imageDown = sprite;

                            button = new ImageButton(buttonStyle);
                            button.getImage().setScaling(Scaling.stretch);

                            float scaleMod = 1f / atlas.getClippedSizeMod()[val].y;
                            button.getImageCell().height(40f).width(40f * atlas.getClippedSizeMod()[val].x * scaleMod);
                        }
                        catch(Exception ex) {
                            ImageButton.ImageButtonStyle buttonStyle =
                                    new ImageButton.ImageButtonStyle(skin.get(ImageButton.ImageButtonStyle.class));

                            buttonStyle.imageUp = null;
                            buttonStyle.imageOver = null;
                            buttonStyle.imageDown = null;

                            button = new ImageButton(buttonStyle);
                        }

                        button.addListener(new ClickListener() {
                           @Override
                           public void clicked(InputEvent event, float x, float y) {
                               TextureRegionPicker picker = new TextureRegionPicker("Pick Sprite", EditorUi.smallSkin, atlas != null ? atlas.name : null, TextureAtlas.getAllSpriteAtlases()) {
                                    @Override
                                    public void result(Integer value, String atlas) {

                                        for(int i = 0; i < entities.size; i++) {
                                            entities.get(i).spriteAtlas = atlas;
                                            if (entities.get(i).drawable != null) entities.get(i).drawable.refresh();
                                        }

                                        applyChanges(field, value.toString());
                                        Editor.app.ui.showEntityPropertiesMenu(false);
                                    }
                               };
                               Editor.app.ui.getStage().addActor(picker);
                               picker.show(Editor.app.ui.getStage());
                           }
                       });

                        add(label).align(Align.left);
                        add(button).height(58f).align(Align.left).fill();

                        fieldMap.put(field, button);
                    }
                    else if(field.getType() == int.class || field.getType() == Integer.class) {
                        final TextField tf = new TextField((value != null ? value.toString() : ""), skin);
                        tf.setTextFieldFilter(new IntegerFilter());
                        tf.setTextFieldListener(getTextFieldListener(field));
                        tf.addListener(new InputListener() {
                            @Override
                            public boolean keyDown(InputEvent event, int keycode) {
                                if(keycode == Input.Keys.UP) {
                                    int val = 0;
                                    if(!tf.getText().equals("")) val = Integer.parseInt(tf.getText());
                                    tf.setTextFieldFilter(null);
                                    tf.setText(Integer.toString(val + 1));
                                    tf.setTextFieldFilter(new IntegerFilter());
                                }
                                else if(keycode == Input.Keys.DOWN) {
                                    int val = 0;
                                    if(!tf.getText().equals("")) val = Integer.parseInt(tf.getText());
                                    tf.setTextFieldFilter(null);
                                    tf.setText(Integer.toString(val - 1));
                                    tf.setTextFieldFilter(new IntegerFilter());
                                }
                                return super.keyDown(event, keycode);
                            }
                        });

                        add(label).align(Align.left);
                        add(tf).align(Align.left).fill();

                        fieldMap.put(field, tf);
                    }
                    else if(field.getType() == float.class || field.getType() == double.class) {
                        final TextField tf = new TextField((value != null ? value.toString() : ""), skin);
                        tf.setTextFieldFilter(new DecimalsFilter());
                        tf.setTextFieldListener(getTextFieldListener(field));
                        tf.addListener(new InputListener() {
                            @Override
                            public boolean keyDown(InputEvent event, int keycode) {
                                DecimalFormat format = new DecimalFormat("##.##");
                                if(keycode == Input.Keys.UP) {
                                    double dval = 0;
                                    if(!tf.getText().equals("")) dval = Double.parseDouble(tf.getText());
                                    dval += 0.1;
                                    tf.setTextFieldFilter(null);
                                    tf.setText(format.format(dval));
                                    tf.setTextFieldFilter(new DecimalsFilter());
                                }
                                else if(keycode == Input.Keys.DOWN) {
                                    double dval = 0;
                                    if(!tf.getText().equals("")) dval = Double.parseDouble(tf.getText());
                                    dval -= 0.1;
                                    tf.setTextFieldFilter(null);
                                    tf.setText(format.format(dval));
                                    tf.setTextFieldFilter(new DecimalsFilter());
                                }
                                return false;
                            }
                        });

                        // Allow drag on label to scrub value.
                        label.addListener(new InputListener() {
                            private final DecimalFormat format = new DecimalFormat("##.##");
                            private float firstX;
                            private float firstY;
                            private float lastX;

                            @Override
                            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                firstX = Gdx.input.getX();
                                firstY = Gdx.input.getY();
                                lastX = x;

                                if (!Gdx.input.isCursorCatched()) {
                                    Gdx.input.setCursorCatched(true);
                                }

                                return true;
                            }

                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                if (Gdx.input.isCursorCatched()) {
                                    Gdx.input.setCursorCatched(false);
                                }

                                Gdx.input.setCursorPosition((int) firstX, (int) firstY);
                                if (LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_MACOSX) {
                                    Gdx.input.setCursorPosition((int) firstX, Gdx.graphics.getHeight() - 1 - (int) firstY);
                                }
                            }

                            @Override
                            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                                float dx = x - lastX;

                                double dval = 0;
                                if(!tf.getText().equals("")) dval = Double.parseDouble(tf.getText());
                                dval += 0.1 * dx / 10;
                                tf.setTextFieldFilter(null);
                                tf.setText(format.format(dval));
                                tf.setTextFieldFilter(new DecimalsFilter());

                                applyChanges(field);

                                lastX = x;
                            }
                        });

                        add(label).align(Align.left);
                        add(tf).align(Align.left).fill();

                        fieldMap.put(field, tf);
                    }
                    else if(field.getType().isEnum()) {
                        SelectBox<Object> sb = new SelectBox<>(skin);
                        sb.setItems(field.getType().getEnumConstants());
                        setSelectedIn(sb, (value != null ? value.toString() : ""));
                        sb.addListener(getSelectBoxListener(field));

                        add(label).align(Align.left);
                        add(sb).align(Align.left).fill();

                        fieldMap.put(field, sb);
                    }
                    else if(field.getType() == Vector3.class) {
                        Vector3 vecval = (Vector3)value;

                        TextField xField = new TextField(vecval != null ? Float.toString(vecval.x) : "", skin);
                        xField.setTextFieldFilter(new DecimalsFilter());
                        xField.setTextFieldListener(getTextFieldListener(field));

                        TextField yField = new TextField(vecval != null ? Float.toString(vecval.y) : "", skin);
                        yField.setTextFieldFilter(new DecimalsFilter());
                        yField.setTextFieldListener(getTextFieldListener(field));

                        TextField zField = new TextField(vecval != null ? Float.toString(vecval.z) : "", skin);
                        zField.setTextFieldFilter(new DecimalsFilter());
                        zField.setTextFieldListener(getTextFieldListener(field));

                        Table vecTable = new Table(skin);
                        vecTable.add(xField).width(62f);
                        vecTable.add(yField).width(62f);
                        vecTable.add(zField).width(62f);
                        vecTable.pack();

                        add(label).align(Align.left);
                        add(vecTable).align(Align.left);

                        fieldMap.put(field, vecTable);
                    }
                    else if(field.getType() == Color.class) {
                        Color colorval = (Color)value;
                        if(colorval == null) colorval = new Color();

                        EditorColorPicker picker = new EditorColorPicker(200, 48, colorval) {
                            @Override
                            public void onSetValue(Color color) {
                                applyChanges(field);
                            }
                        };

                        add(label).align(Align.left);
                        add(picker).align(Align.left);

                        fieldMap.put(field, picker);
                    }
                    else if(field.getType() == boolean.class) {
                        Boolean[] values = new Boolean[2];
                        values[0] = true;
                        values[1] = false;

                        SelectBox<Boolean> sb = new SelectBox<>(skin);
                        sb.setItems(values);
                        setSelectedIn(sb, (value != null ? value.toString() : ""));
                        sb.addListener(getSelectBoxListener(field));

                        add(label).align(Align.left);
                        add(sb).align(Align.left).fill();

                        fieldMap.put(field, sb);
                    }

                    row();
                }
            }

            pack();
        }
        catch(Exception ex) {
            Gdx.app.error("DelvEdit", ex.getMessage());
        }
    }

    /** Returns all classes (including super classes) for the given object. */
    private <T> Array<Class<?>> getClassesForObject(T object) {
        Array<Class<?>> classes = new Array<>();

        Class<?> c = object.getClass();
        while (c != null) {
            classes.add(c);
            c = c.getSuperclass();
        }

        return classes;
    }

    /** Returns all common classes (including super classes) for the given array of objects. */
    private <T> Array<Class<?>> getCommonClassesForObjects(Array<T> objects) {
        if (objects.size <= 0) {
            return new Array<>();
        }
        else if (objects.size <= 1) {
            return new Array<>(getClassesForObject(objects.get(0)));
        }
        else {
            Array<Class<?>> classes = getClassesForObject(objects.get(0));

            // Remove any non-common classes.
            Array<Class<?>> nonCommonClasses = new Array<>();
            for(T o : objects) {
                Array<Class<?>> classesToCheck = getClassesForObject(o);
                for (Class<?> existingClass : classes) {
                    if(!classesToCheck.contains(existingClass, true)) {
                        nonCommonClasses.add(existingClass);
                    }
                }
            }
            classes.removeAll(nonCommonClasses, true);

            return classes;
        }
    }

    private <T> String getObjectName(T object) {
        String[] nameParts = object.getClass().getName().split("\\.");

        String name = "Unknown";
        if (nameParts.length > 0) {
            name = nameParts[nameParts.length - 1];
        }

        return name;
    }

    private <T> void createMenuHeader(T object) {
        String objectName = getObjectName(object);

        add(new Label(objectName, EditorUi.mediumSkin)).align(Align.left).padLeft(-12f);
        row();
    }

    private void createGroupHeader(String groupName) {
        add(groupName).colspan(2).align(Align.left).padLeft(-8f);
        row();
    }

    private <T> ArrayMap<String, Array<Field>> getFieldGroups(Array<Class<?>> classes, T object) {
        ArrayMap<String, Array<Field>> groups = new ArrayMap<>();

        for (Class<?> c : classes) {
            Field[] fields = c.getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];

                try {
                    field.setAccessible(true);
                } catch (Exception ignored) {
                    continue;
                }

                String groupName = getEditorPropertyGroupName(field, object);
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

    private boolean isEditorProperty(Field field) {
        Annotation annotation = field.getAnnotation(EditorProperty.class);
        return (annotation != null);
    }

    private <T> String getEditorPropertyGroupName(Field field, T object) {
        EditorProperty annotation = field.getAnnotation(EditorProperty.class);

        if (annotation != null && !annotation.group().equals("")) {
            return annotation.group();
        }

        return object.getClass().getSimpleName();
    }

    private <T> boolean shouldRenderGroup(T object, String groupName) {
        if (object instanceof Prefab) {
            return !(groupName.equals("Prefab") || groupName.equals("MonsterPrefab"));
        } else if (object instanceof Group) {
            return !(groupName.equals("Group") || !groupName.equals("General"));
        }

        return true;
    }

    private String[] getValidEditorPropertyStrings(Field field) {
        EditorProperty annotation = field.getAnnotation(EditorProperty.class);
        
        if (annotation != null) {
            if (annotation.valid().length > 0) {
                return annotation.valid();
            } else if (annotation.type().equals("SPRITE_ATLAS_LIST")) {
                return EditorArt.getAtlasList();
            }
        }

        return null;
    }

    private boolean isSelectBoxType(Field field) {
        return (field.getType() == String.class && getValidEditorPropertyStrings(field) != null);
    }

    private void renderSelectBoxType(Field field, Object value, Label label, Skin skin) {
        String stringifiedValue = (value != null ? value.toString() : "");
        SelectBox<String> selectBox = new SelectBox<>(skin);
        selectBox.setItems(getValidEditorPropertyStrings(field));
        setSelectedIn(selectBox, stringifiedValue);
        selectBox.addListener(getSelectBoxListener(field));

        add(label).align(Align.left);
        add(selectBox).align(Align.left).fill();

        fieldMap.put(field, selectBox);
    }

    public static boolean isAtlasRegionPicker(Field field) {
        EditorProperty annotation = field.getAnnotation(EditorProperty.class);
        if(annotation != null && annotation.type().equals("SPRITE_ATLAS_NUM")) return true;
        return false;
    }

    private boolean isFilePickerType(Field field) {
        return (field.getType() == String.class && isFilePicker(field));
    }

    private boolean isFilePicker(Field field) {
        EditorProperty annotation = field.getAnnotation(EditorProperty.class);
        return (annotation != null && annotation.type().equals("FILE_PICKER"));
    }

    public static String getFilePickerType(Field field) {
        EditorProperty annotation = field.getAnnotation(EditorProperty.class);
        if(annotation != null && annotation.type().equals("FILE_PICKER")) return annotation.params();
        return null;
    }

    private void renderFilePickerType(Field field, Object value, Label label, Skin skin) {
        String stringifiedValue = (value != null ? value.toString() : "");
        final String filePickerType = getFilePickerType(field);
        final Skin finalSkin = skin;
        final TextButton button = new TextButton(stringifiedValue, skin);
        final FileHandle folder = new FileHandle(filePickerType);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AssetPicker picker = new AssetPicker("Select File", finalSkin, button.getText().toString(), folder,
                        getFilePickerIncludeBase(field)) {
                    @Override
                    protected void result(Object object) {
                        Boolean result = (Boolean) object;
                        if (result) {
                            String picked = getResult();
                            button.setText(picked);
                            applyChanges(field);
                        }
                    }
                }.setFileNameEnabled(false);

                Editor.app.ui.getStage().addActor(picker);
                picker.show(getStage());
            }
        });

        add(label).align(Align.left);
        add(button).height(58f).align(Align.left).fill();

        fieldMap.put(field, button);
    }

    public static boolean getFilePickerIncludeBase(Field field) {
        EditorProperty annotation = field.getAnnotation(EditorProperty.class);
        if(annotation != null && annotation.type().equals("FILE_PICKER")) return annotation.include_base();
        return false;
    }

    private <T> Object getCommonValueForObjects(Field field, Array<T> objects) {
        Object commonValue = null;

        for (T o : objects) {
            Object value = null;
            try {
                value = field.get(o);
            } catch (Exception e) {
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

    public TextField.TextFieldListener getTextFieldListener(final Field currentField) {
        return new TextField.TextFieldListener() {
            public void keyTyped (TextField textField, char key) {
                applyChanges(currentField);
            }
        };
    }

    public ChangeListener getSelectBoxListener(final Field currentField) {
        return new ChangeListener() {
            public void changed(ChangeEvent changeEvent, Actor actor) {
                applyChanges(currentField);
                Editor.app.ui.showEntityPropertiesMenu(false);
            }
        };
    }

    public void applyChanges(final Field currentField, String val) {
        try {
            if (currentField.getType() == int.class) {
                if (val.equals("")) val = "0";
                for (Entity entity : selectedEntities) {
                    currentField.set(entity, Integer.parseInt(val));
                }
            }
        }
        catch(Exception ex) {
            Gdx.app.error("DelvEdit", ex.getMessage());
        }
    }

    public void applyChanges(final Field currentField, Material val) {
        try {
            if (currentField.getType() == Material.class && val != null) {
                for (Entity entity : selectedEntities) {
                    currentField.set(entity, new Material(val.texAtlas, val.tex));
                }
            }
        }
        catch(Exception ex) {
            Gdx.app.error("DelvEdit", ex.getMessage());
        }
    }

    public void applyChanges(final Field currentField) {
        try {
            Gdx.app.debug("DelvEdit", "Applying change for " + currentField.getName());

            if(selectedEntities != null && fieldMap.containsKey(currentField)) {
                Actor actor = fieldMap.get(currentField);

                // apply based on the type of input
                if(actor instanceof TextField) {
                    String val = ((TextField)actor).getText();

                    if(currentField.getType() == int.class) {
                        if(val.equals("")) val = "0";
                        for(Entity entity : selectedEntities) {
                            currentField.set(entity, Integer.parseInt(val));
                        }
                    }
                    else if(currentField.getType() == Integer.class) {
                        if(val.equals("")) val = null;
                        for(Entity entity : selectedEntities) {
                            currentField.set(entity, Integer.parseInt(val));
                        }
                    }
                    else if(currentField.getType() == float.class) {
                        if(val.equals("")) val = "0";
                        for(Entity entity : selectedEntities) {
                            currentField.set(entity, Float.parseFloat(val));
                        }
                    }
                    else if(currentField.getType() == double.class) {
                        if(val.equals("")) val = "0";
                        for(Entity entity : selectedEntities) {
                            currentField.set(entity, Double.parseDouble(val));
                        }
                    }
                    else if(currentField.getType() == String.class) {
                        for(Entity entity : selectedEntities) {
                            currentField.set(entity, val);
                        }
                    }
                }
                else if(actor instanceof TextButton) {
                    String val = ((TextButton)actor).getText().toString();
                    if(currentField.getType() == String.class) {
                        for(Entity entity : selectedEntities) {
                            currentField.set(entity, val);
                        }
                    }
                }
                else if(actor instanceof SelectBox) {
                    String val = ((SelectBox)actor).getSelected().toString();
                    if(currentField.getType() == boolean.class) {
                        for(Entity entity : selectedEntities) {
                            currentField.set(entity, Boolean.parseBoolean(val));
                        }
                    }
                    else if(currentField.getType().isEnum()) {
                        for(Entity entity : selectedEntities) {
                            currentField.set(entity, Enum.valueOf((Class<Enum>) currentField.getType(), val));
                        }
                    }
                    else if(currentField.getType() == String.class) {
                        for(Entity entity : selectedEntities) {
                            currentField.set(entity, val);
                        }
                    }
                }
                else if(actor instanceof EditorColorPicker) {
                    EditorColorPicker picker = (EditorColorPicker)actor;

                    for(Entity entity : selectedEntities) {
                        currentField.set(entity, new Color(picker.getValue()));
                    }
                }
                else if(actor instanceof Table) {
                    Table t = (Table)actor;

                    if(currentField.getType() == Vector3.class) {
                        String x = ((TextField)t.getCells().get(0).getActor()).getText();
                        String y = ((TextField)t.getCells().get(1).getActor()).getText();
                        String z = ((TextField)t.getCells().get(2).getActor()).getText();

                        if(x.equals("")) x = "0";
                        if(y.equals("")) y = "0";
                        if(z.equals("")) z = "0";

                        for(Entity entity : selectedEntities) {
                            currentField.set(entity, new Vector3(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(z)));
                        }
                    }
                    else if(currentField.getType() == Color.class) {
                        String r = ((TextField)t.getCells().get(0).getActor()).getText();
                        String g = ((TextField)t.getCells().get(1).getActor()).getText();
                        String b = ((TextField)t.getCells().get(2).getActor()).getText();

                        if(r.equals("")) r = "0";
                        if(g.equals("")) g = "0";
                        if(b.equals("")) b = "0";

                        for(Entity entity : selectedEntities) {
                            currentField.set(entity, new Color(Float.parseFloat(r) / 255f, Float.parseFloat(g) / 255f, Float.parseFloat(b) / 255f, 1f));
                        }
                    }
                }
            }

            Editor.app.refreshLights();

            for(Entity entity : selectedEntities) {
                if (entity.drawable != null) entity.drawable.refresh();

                if (entity instanceof ProjectedDecal) {
                    ((ProjectedDecal) entity).refresh();
                }
            }
        }
        catch(Exception ex) {
            Gdx.app.error("DelvEdit", ex.getMessage());
        }
    }

    static public class DecimalsFilter implements TextField.TextFieldFilter {
        @Override
        public boolean acceptChar (TextField textField, char c) {
            try {
                String newValue = new StringBuilder(textField.getText()).insert(textField.getCursorPosition(), c).toString();
                if(newValue.equals("-")) return true;

                Double.parseDouble(newValue);
                return true;
            }
            catch(Exception ex) { }
            return false;
        }
    }

    static public class IntegerFilter implements TextField.TextFieldFilter {
        private int max = Integer.MAX_VALUE;
        private int min = Integer.MIN_VALUE;

        public IntegerFilter() { }

        public IntegerFilter(int min, int max) {
            this.max = max;
            this.min = min;
        }

        @Override
        public boolean acceptChar (TextField textField, char c) {
            try {
                String newValue = new StringBuilder(textField.getText()).insert(textField.getCursorPosition(), c).toString();
                if(newValue.equals("-")) return true;

                Integer parsed = Integer.parseInt(newValue);
                return parsed <= max && parsed >= min;
            }
            catch(Exception ex) { }
            return false;
        }
    }

    private <T> void setSelectedIn(SelectBox<T> selectBox, String value) {
        Array<T> items = selectBox.getItems();
        for(int i = 0; i < items.size; i++) {
            if(items.get(i).toString().equals(value)) {
                selectBox.setSelectedIndex(i);
                return;
            }
        }
    }
}
