package com.interrupt.dungeoneer.editor.ui;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class ControlPoint {
    public Vector3 point;
    public ControlPointType controlPointType;

    public Array<ControlPointVertex> vertices = new Array<>();

    public ControlPoint(Vector3 point, ControlPointType type) {
        this.point = point;
        controlPointType = type;
    }

    public ControlPoint(Vector3 point, ControlPointVertex vertex) {
        this.point = point;
        controlPointType = ControlPointType.VERTEX;
        vertices.add(vertex);
    }

    public boolean isCeiling() {
        return controlPointType == ControlPointType.CEILING_NORTH || controlPointType == ControlPointType.CEILING_EAST || controlPointType == ControlPointType.CEILING_SOUTH || controlPointType == ControlPointType.CEILING_WEST;
    }

    public boolean isFloor() {
        return controlPointType == ControlPointType.FLOOR_NORTH || controlPointType == ControlPointType.FLOOR_EAST || controlPointType == ControlPointType.FLOOR_SOUTH || controlPointType == ControlPointType.FLOOR_WEST;
    }

    public boolean isNorthCeiling() {
        return controlPointType == ControlPointType.CEILING_NORTH || controlPointType == ControlPointType.CEILING;
    }

    public boolean isSouthCeiling() {
        return controlPointType == ControlPointType.CEILING_SOUTH || controlPointType == ControlPointType.CEILING;
    }

    public boolean isEastCeiling() {
        return controlPointType == ControlPointType.CEILING_EAST || controlPointType == ControlPointType.CEILING;
    }

    public boolean isWestCeiling() {
        return controlPointType == ControlPointType.CEILING_WEST || controlPointType == ControlPointType.CEILING;
    }

    public boolean isNorthFloor() {
        return controlPointType == ControlPointType.FLOOR_NORTH || controlPointType == ControlPointType.FLOOR;
    }

    public boolean isSouthFloor() {
        return controlPointType == ControlPointType.FLOOR_SOUTH || controlPointType == ControlPointType.FLOOR;
    }

    public boolean isEastFloor() {
        return controlPointType == ControlPointType.FLOOR_EAST || controlPointType == ControlPointType.FLOOR;
    }

    public boolean isWestFloor() {
        return controlPointType == ControlPointType.FLOOR_WEST || controlPointType == ControlPointType.FLOOR;
    }
}
