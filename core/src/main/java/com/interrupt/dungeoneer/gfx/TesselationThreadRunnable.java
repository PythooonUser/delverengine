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
        // Capture the work at the start of the pass. init() reassigns these fields for a new
        // chunk, and UpdateDirtyWorldChunks can submit this runnable while a pass is running.
        final Level workLevel = level;
        final GlRenderer workRenderer = renderer;
        final WorldChunk workWc = wc;
        final OverworldChunk publishOwner = wc.overworldChunk;
        final int publishGeneration = publishOwner.generation;
        final int publishXOffset = wc.xOffset;
        final int publishYOffset = wc.yOffset;

        // Worker thread: this pass only writes bare vertex arrays. Meshes, atlas loads and
        // static mesh merging need the GL context, so they happen in the posted runnable, see
        // WorldChunk.publishTesselation. A fresh Tesselator and groups per run keep two
        // tesselations of one chunk from sharing mutable state.
        final Tesselator t = new Tesselator();
        final TesselatorGroups groups = new TesselatorGroups();
        t.Tesselate(workLevel, workRenderer, workWc, workWc.xOffset, workWc.yOffset, workWc.width, workWc.height, groups, makeFloors, makeCeilings, makeWalls, true, false);

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                // the chunk can be disposed and reused for a new location while this is queued,
                // so don't publish vertices for tiles this chunk no longer has
                if(workWc.xOffset != publishXOffset || workWc.yOffset != publishYOffset ||
                   workWc.overworldChunk != publishOwner || publishOwner.generation != publishGeneration) {
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
