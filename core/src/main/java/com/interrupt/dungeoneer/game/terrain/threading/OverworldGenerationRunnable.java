package com.interrupt.dungeoneer.game.terrain.threading;

import com.badlogic.gdx.Gdx;
import com.interrupt.dungeoneer.GameManager;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.game.OverworldChunk;
import com.interrupt.dungeoneer.game.OverworldLevel;

// Runs on the main thread: generation loads .obj model meshes through the shared ObjLoader (GL),
// so it can't run on a worker. This class sequences the generation passes and the post-generation
// publish.
public class OverworldGenerationRunnable implements Runnable {
    private OverworldLevel level = null;
    private OverworldChunk chunk = null;

    Runnable afterRunnable = new Runnable() {
        @Override
        public void run() {
            level.madeNewChunkAt(chunk.xChunk, chunk.yChunk);
            chunk.init(level);
            level.updateStaticSpatialHash();
            level.updateLightSpatialHash();

            // Tesselation is CPU work once every repeating atlas is preloaded on the render
            // thread, so offload the chunks the player isn't in.
            if(Game.instance.player == null || chunk.entityIsInChunk(Game.instance.player)) {
                chunk.TesselateOnThisThread(level, GameManager.renderer);
            }
            else {
                chunk.TesselateOnThread(level, GameManager.renderer);
            }

            // FIXME: Do we still need this?
            GameManager.renderer.UpdateDirtyWorldChunks(level);
        }
    };

    public OverworldGenerationRunnable() { }

    public void init(OverworldChunk chunk, OverworldLevel level) {
        this.chunk = chunk;
        this.level = level;
    }

    @Override
    public void run() {
        if(chunk == null || level == null) return;

        chunk.tileToLoad = level.getLocationAt(chunk.xChunk, chunk.yChunk);

        chunk.Init(level);
        chunk.decorate(level);
        chunk.runGenInfo(level);
        chunk.doLighting(level);

        // post a Runnable to the rendering thread that processes the result
        Gdx.app.postRunnable(afterRunnable);
    }
}
