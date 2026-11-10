package com.interrupt.dungeoneer.gfx;

import com.badlogic.gdx.Gdx;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.dungeoneer.game.OverworldChunk;

public class TesselationThreadRunnable implements Runnable {

    WorldChunk wc = null;
    Level level = null;
    GlRenderer renderer = null;

    boolean makeFloors = true;
    boolean makeCeilings = true;
    boolean makeWalls = true;

    @Override
    public void run() {
        // Capture the work up front. init() reassigns these fields for the next chunk, and
        // UpdateDirtyWorldChunks can submit this runnable while a pass is running.
        final WorldChunk workWc = wc;
        final Level workLevel = level;
        final GlRenderer workRenderer = renderer;
        final OverworldChunk publishOwner = wc.overworldChunk;
        final int publishGeneration = publishOwner.generation;
        final int publishXOffset = wc.xOffset;
        final int publishYOffset = wc.yOffset;

        // Worker thread: this pass only writes vertex arrays. Meshes come from the mesh pool (GL),
        // static mesh merging and atlas loading need the GL context too, so all of that happens in
        // the posted runnable, see WorldChunk.publishTesselation. A fresh Tesselator and groups per
        // pass keep two tesselations of one chunk from sharing mutable state.
        final Tesselator t = new Tesselator();
        final TesselatorGroups groups = new TesselatorGroups();
        t.Tesselate(workLevel, workRenderer, workWc, workWc.xOffset, workWc.yOffset, workWc.width, workWc.height, groups, makeFloors, makeCeilings, makeWalls, true, false);

        // post a Runnable to the rendering thread that processes the result
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                // Chunks are pooled and reused for a new location, so a queued publish can be for
                // tiles this chunk no longer has. Don't publish those.
                if(workWc.xOffset != publishXOffset ||
                   workWc.yOffset != publishYOffset ||
                   workWc.overworldChunk != publishOwner ||
                   publishOwner.generation != publishGeneration) {
                    return;
                }

                workWc.publishTesselation(workRenderer, workLevel, groups, false);

                if(workRenderer.chunks != null && !workRenderer.chunks.contains(workWc, true)) {
                    workRenderer.chunks.add(workWc);
                }
            }
        });
    }

    public void init(OverworldChunk oc, Level level, GlRenderer renderer) {
        this.wc = oc.getWorldChunk();
        this.wc.overworldChunk = oc;
        this.level = level;
        this.renderer = renderer;
    }
}
