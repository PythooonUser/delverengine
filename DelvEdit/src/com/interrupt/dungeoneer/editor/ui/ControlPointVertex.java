package com.interrupt.dungeoneer.editor.ui;

import com.interrupt.dungeoneer.tiles.Tile;

public class ControlPointVertex {
    public Tile tile;
    public ControlVertex vertex = ControlVertex.SLOPE_NORTHEAST;

    public ControlPointVertex(Tile tile, ControlVertex vertex) {
        this.tile = tile;
        this.vertex = vertex;
    }
}
