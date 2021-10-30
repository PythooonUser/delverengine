package com.interrupt.dungeoneer.editor;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.interrupt.dungeoneer.tiles.Tile;

public class ControlPoint {
    public Vector3 point;
    public ControlPointType type;

    public Array<ControlPointVertex> vertices = new Array<>();

    public ControlPoint(Vector3 point, ControlPointType type) {
        this.point = point;
        this.type = type;
    }

    public ControlPoint(Vector3 point, ControlPointVertex vertex) {
        this.point = point;
        type = ControlPointType.VERTEX;
        vertices.add(vertex);
    }

    public boolean isCeiling() {
        return type == ControlPointType.CEILING_NORTH
                || type == ControlPointType.CEILING_EAST
                || type == ControlPointType.CEILING_SOUTH
                || type == ControlPointType.CEILING_WEST;
    }

    public boolean isFloor() {
        return type == ControlPointType.FLOOR_NORTH
                || type == ControlPointType.FLOOR_EAST
                || type == ControlPointType.FLOOR_SOUTH
                || type == ControlPointType.FLOOR_WEST;
    }

    public boolean isNorthCeiling() {
        return type == ControlPointType.CEILING_NORTH || type == ControlPointType.CEILING;
    }

    public boolean isSouthCeiling() {
        return type == ControlPointType.CEILING_SOUTH || type == ControlPointType.CEILING;
    }

    public boolean isEastCeiling() {
        return type == ControlPointType.CEILING_EAST || type == ControlPointType.CEILING;
    }

    public boolean isWestCeiling() {
        return type == ControlPointType.CEILING_WEST || type == ControlPointType.CEILING;
    }

    public boolean isNorthFloor() {
        return type == ControlPointType.FLOOR_NORTH || type == ControlPointType.FLOOR;
    }

    public boolean isSouthFloor() {
        return type == ControlPointType.FLOOR_SOUTH || type == ControlPointType.FLOOR;
    }

    public boolean isEastFloor() {
        return type == ControlPointType.FLOOR_EAST || type == ControlPointType.FLOOR;
    }

    public boolean isWestFloor() {
        return type == ControlPointType.FLOOR_WEST || type == ControlPointType.FLOOR;
    }

    public enum ControlPointType {
        FLOOR,
        CEILING,
        CEILING_NORTH,
        FLOOR_NORTH,
        CEILING_EAST,
        FLOOR_EAST,
        CEILING_SOUTH,
        FLOOR_SOUTH,
        CEILING_WEST,
        FLOOR_WEST,
        VERTEX
    }

    public enum ControlVertex {
        SLOPE_NORTH_WEST,
        SLOPE_NORTH_EAST,
        SLOPE_SOUTH_WEST,
        SLOPE_SOUTH_EAST,
        CEILING_NORTH_WEST,
        CEILING_NORTH_EAST,
        CEILING_SOUTH_WEST,
        CEILING_SOUTH_EAST
    }

    public static class ControlPointVertex {
        Tile tile;
        ControlVertex vertex = ControlVertex.SLOPE_NORTH_EAST;

        public ControlPointVertex(Tile tile, ControlVertex vertex) {
            this.tile = tile;
            this.vertex = vertex;
        }
    }
}
