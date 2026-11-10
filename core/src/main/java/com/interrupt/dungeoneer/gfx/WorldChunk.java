package com.interrupt.dungeoneer.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Triangle;
import com.interrupt.dungeoneer.Art;
import com.interrupt.dungeoneer.GameManager;
import com.interrupt.dungeoneer.entities.Entity;
import com.interrupt.dungeoneer.entities.Group;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.dungeoneer.game.OverworldChunk;
import com.interrupt.dungeoneer.game.OverworldLevel;
import com.interrupt.dungeoneer.gfx.drawables.DrawableMesh;
import com.interrupt.dungeoneer.gfx.shaders.ShaderInfo;
import com.interrupt.dungeoneer.screens.GameScreen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class WorldChunk {
	public static Tesselator tesselator = new Tesselator();
    protected TesselatorGroups tesselators;

	protected int height = 17;
	protected int width = 17;

	protected int xOffset = 0;
	protected int yOffset = 0;

	protected Array<Entity> entities = new Array<Entity>();

	protected BoundingBox bounds = new BoundingBox();

	public boolean visible = true;
	public boolean hasBuilt = false;
	public boolean needsRetessellation = false;

	public ArrayMap<String,Array<Mesh>> staticMeshBatch = null;

	//private Array<Vector3> staticMeshCollisionTriangles = new Array<Vector3>();

	private boolean makeWalls = true;
	private boolean makeFloors = true;
	private boolean makeCeilings = true;

	Vector3 position = new Vector3();

	public static Comparator<WorldChunk> sorter = new Comparator<WorldChunk>() {
		@Override
		public int compare (WorldChunk o1, WorldChunk o2) {
			float dist1 = GameManager.renderer.camera.position.dst(o2.position);
			float dist2 = GameManager.renderer.camera.position.dst(o1.position);
			return (int)Math.signum(dist2 - dist1);
		}
	};

	public OverworldChunk overworldChunk = null;

	public WorldChunk(GlRenderer renderer)
	{
        tesselators = new TesselatorGroups();
	}

	public void setOffset(int x, int y)
	{
		xOffset = x;
		yOffset = y;

		bounds.set(new Vector3(xOffset, -0.5f, yOffset), new Vector3(xOffset + width, 0.5f, yOffset + height));
	}

	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;

		bounds.set(new Vector3(xOffset, -0.5f, yOffset), new Vector3(xOffset + width, 0.5f, yOffset + height));
	}

	public void render() {
        tesselators.world.render();
	}

	public void renderWater() {
        tesselators.water.render();
	}

	public void renderWaterfall() {
        tesselators.waterfall.render();
	}

    public void renderWaterEdges() {
        tesselators.waterEdges.render();
    }

    public void renderWaterfallEdges() {
        tesselators.waterfallEdges.render();
    }

	public void clear() {
        tesselators.clear();
		hasBuilt = false;
		freeStaticMeshes();
	}

	public void refresh() {
        tesselators.refresh();
		hasBuilt = false;
		freeStaticMeshes();
	}

	public void freeStaticMeshes() {
		if(staticMeshBatch != null) {
			for (Entry<String, Array<Mesh>> e : staticMeshBatch.entries()) {
				for (Mesh m : e.value) {
					GlRenderer.staticMeshPool.freeMesh(m);
				}
			}
			staticMeshBatch.clear();
		}
	}

	public void AddEntityForEditor(Entity e, Level level) {
		// Needed for static meshes in groups in the editor
		if(e instanceof Group) {
			// Don't init the group again if it has already been initialized before
			Group group = (Group)e;
			if(group.entities.size == 0)
				e.init(level, Level.Source.EDITOR);

			for(Entity g : ((Group) e).entities) {
				AddEntityForEditor(g, level);
			}
		} else {
			entities.add(e);
			e.updateLight(level);
		}
	}

	public void Tesselate(Level level, GlRenderer renderer)
	{
		tesselator.Tesselate(level, renderer, this, xOffset, yOffset, width, height, tesselators, makeFloors, makeCeilings, makeWalls, true, true);
		needsRetessellation = false;

		if(hasBuilt) return;

		publishTesselation(renderer, level, tesselators, true);
	}

	// Publishes the vertices a Tesselator pass wrote into groups. Taking meshes from the mesh
	// pool, merging static meshes and loading texture atlases all need the GL context, so this
	// only runs on the render thread - chunks tesselated on a worker post their groups here with
	// Gdx.app.postRunnable. meshesBuilt says whether the pass already took meshes from the pool.
	public void publishTesselation(GlRenderer renderer, Level level, TesselatorGroups groups, boolean meshesBuilt)
	{
		// hand the meshes the outgoing pass is holding back to the pools, before new ones are taken
		if(tesselators != null && tesselators != groups) tesselators.clear();
		freeStaticMeshes();

		if(!meshesBuilt) groups.build();

		// world chunks own static entities in their bounds
		entities.clear();

		for(Entity e : level.static_entities) {
			if(e.x >= xOffset && e.x < xOffset + width && e.y >= yOffset && e.y < yOffset + height) {
				entities.add(e);
				e.updateLight(level);
			}
		}

		// renderer hasn't sorted entities into static_entities yet
		if(renderer.editorIsRendering) {
			for(Entity e : level.entities) {
				if(e.x >= xOffset && e.x < xOffset + width && e.y >= yOffset && e.y < yOffset + height) {
					AddEntityForEditor(e, level);
				}
			}
		}

		if(level instanceof OverworldLevel) {
			for(OverworldChunk chunk : ((OverworldLevel)level).chunks.values()) {
				if(chunk.xChunk * chunk.width == xOffset && chunk.yChunk * chunk.height == yOffset) {
					entities.addAll(chunk.static_entities);
				}
			}
		}

		// sort the mesh entities into buckets, determined by their texture
		HashMap<String, Array<Entity>> meshesByTexture = new HashMap<String, Array<Entity>>();
		for(Entity e : entities) {
			if(e.drawable != null && e.drawable instanceof DrawableMesh) {
				DrawableMesh drbl = (DrawableMesh)e.drawable;
				if(drbl.isStaticMesh && !drbl.bakeLighting) {
					if(!meshesByTexture.containsKey(drbl.textureFile))  {
						meshesByTexture.put(drbl.textureFile, new Array<Entity>());
					}
					meshesByTexture.get(drbl.textureFile).add(e);
				}
			}
		}

		BoundingBox newBounds = new BoundingBox();
		newBounds.clr();
		newBounds.min.x = xOffset;
		newBounds.max.x = xOffset + width;

		newBounds.min.z = yOffset;
		newBounds.max.z = yOffset + height;

		// set drawing bounds
		Array<BoundingBox> calcedBounds = new Array<BoundingBox>();

		calcedBounds.add(groups.world.calculateBoundingBox());
		calcedBounds.add(groups.water.calculateBoundingBox());
		calcedBounds.add(groups.waterfall.calculateBoundingBox());

		// make a static mesh from each entity bucket
		// mergeStaticMeshes wants a collision triangle list, chunks don't use it
		List<Vector3> tempCollisionTriangles = new ArrayList<Vector3>();
		ArrayMap<String, Array<Mesh>> newStaticMeshBatch = new ArrayMap<String, Array<Mesh>>();
		for(String key : meshesByTexture.keySet()) {
			Array<Mesh> m = GlRenderer.mergeStaticMeshes(level, meshesByTexture.get(key), tempCollisionTriangles);
			if(m != null) {
				newStaticMeshBatch.put(key, m);

				for(Mesh mesh : m) {
					BoundingBox meshBounds = mesh.calculateBoundingBox();
					newBounds.min.x = Math.min(newBounds.min.x, meshBounds.min.x);
					newBounds.min.y = Math.min(newBounds.min.y, meshBounds.min.y);
					newBounds.min.z = Math.min(newBounds.min.z, meshBounds.min.z);
					newBounds.max.x = Math.max(newBounds.max.x, meshBounds.max.x);
					newBounds.max.y = Math.max(newBounds.max.y, meshBounds.max.y);
					newBounds.max.z = Math.max(newBounds.max.z, meshBounds.max.z);
				}
			}
		}

		// cleanup, don't need the mesh buckets anymore
		meshesByTexture.clear();

		for(BoundingBox b : calcedBounds)
		{
			if(b != null) {
				newBounds.min.y = Math.min(newBounds.min.y, b.min.y);
				newBounds.max.y = Math.max(newBounds.max.y, b.max.y);
			}
		}

		// publish. The chunk is drawn from these groups from now on.
		tesselators = groups;
		bounds = newBounds;
		staticMeshBatch = newStaticMeshBatch;
		hasBuilt = true;

		GameScreen.resetDelta = true;
		position.set(xOffset + (width / 2f), 0, yOffset + (height / 2f));
	}

	public boolean UpdateVisiblity(PerspectiveCamera camera) {
		visible = camera.frustum.boundsInFrustum(bounds);
		return visible;
	}

	public boolean Empty() {
		return (entities.size == 0 && tesselators.isEmpty());
	}

	public void renderStaticMeshBatch(ShaderInfo shader) {
		if(staticMeshBatch != null) {
			for(Entry <String, Array<Mesh>> e : staticMeshBatch.entries()) {
				Texture t = Art.cachedTextures.get(e.key);
				if(t == null) Art.loadTexture(e.key);
				if(t != null) GlRenderer.bindTexture(t);

				for(Mesh m: e.value) {
					if (shader != null) {
				        m.render(shader.shader, GL20.GL_TRIANGLES);
					}
				}
			}
		}
	}

	public void setOverworldChunk(OverworldChunk overworldChunk) {
		this.overworldChunk = overworldChunk;
	}

	public int getWorldX() {
		return xOffset + (width / 2);
	}

	public int getWorldY() {
		return yOffset + (height / 2);
	}

	public int getRadius() {
		return width / 2;
	}
}
