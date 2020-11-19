package com.interrupt.dungeoneer.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.GameInput;
import com.interrupt.dungeoneer.GameManager;
import com.interrupt.dungeoneer.gfx.GlRenderer;

/** Abstract class representing UI overlays. */
public abstract class Overlay {
	public boolean pausesGame = true;
	public boolean visible = false;
	public boolean running = false;
	public boolean showCursor = true;
	public boolean catchInput = true;

	protected Stage ui;
	protected GlRenderer renderer;
	protected GL20 gl;
	
	private InputProcessor previousInputProcessor = null;
	private boolean cursorWasShownBefore = false;
	
	/**
	 * Shows the overlay and performs some initializations. Implement <code>onShow</code> to add additional logic here.
	 * @param setInputSettings Handle input settings.
	 */
	public void show(boolean setInputSettings) {
		visible = true;
		running = true;

		if(setInputSettings) {
			cursorWasShownBefore = !Gdx.input.isCursorCatched();

			if ((Game.instance == null || !Game.instance.input.usingGamepad) && showCursor != cursorWasShownBefore) {
				Gdx.input.setCursorCatched(!showCursor);
				Game.instance.input.caughtCursor = !showCursor;
			}

			if(catchInput) {
				if (Gdx.input.getInputProcessor() instanceof GameInput) {
					((GameInput) Gdx.input.getInputProcessor()).clear();
				}

				previousInputProcessor = Gdx.input.getInputProcessor();
			}
		}
		
		onShow();
	}

	/** Shows the overlay and performs some initializations, including handling of input settings etc. Implement <code>onShow</code> to add additional logic here. */
	public void show() {
		show(true);
	}

	/** Implement this method in order to provide a means of showing the overlay content on the screen. */
	protected abstract void onShow();
	
	/** Hides the overlay and performs some clean-ups. Implement <code>onHide</code> to add additional logic here. */
	public void hide() {
		visible = false;
		running = false;

		if(showCursor != cursorWasShownBefore) {
			Gdx.input.setCursorCatched(!cursorWasShownBefore);
			Game.instance.input.caughtCursor = !cursorWasShownBefore;
		}
		
		if(previousInputProcessor != null) {
			Gdx.input.setInputProcessor(previousInputProcessor);
		}
		
		onHide();
	}

	/** Implement this method in order to perform additional clean-up logic when the overlay gets removed from the screen. */
	protected abstract void onHide();
	
	/**
	 * Draws the overlay. Typically called every frame.
	 * @param delta Time in seconds since the last frame.
	 */
	protected void draw(float delta) {
		renderer = GameManager.renderer;
		gl = renderer.getGL();
		
		if(ui != null) {
			if(running) {
				ui.act(delta);
			}

			ui.draw();
		}
	}
	
	/**
	 * Implement this method in order to provide support for animations, handling of input events etc. Typically called every frame.
	 * @param delta Time in seconds since the last frame.
	 */
	public abstract void tick(float delta);

	/** Pauses the input handling of the overlay etc. */
	public void pause() {
		running = false;
	}
	
	/** Resumes the input handling of the overlay etc. */
	public void resume() {
		running = true;
	}

	/**
	 * Resizes the overlay to the given dimensions.
	 * @param width The width of the client area in logical pixels.
	 * @param height The height of the client area in logical pixels.
	 */
    public void resize(int width, int height) {
	    if(ui != null && ui.getViewport() != null) {
            Viewport viewport = ui.getViewport();
            viewport.setWorldHeight(height / Game.GetUiSize() * 22f);
            viewport.setWorldWidth(width / Game.GetUiSize() * 22f);
            viewport.update(width, height, true);
        }
    }

	/**
	 * Copies input settings from an existing overlay.
	 * @param overlay The existing overlay to copy input settings from.
	 */
	public void copyInputSettingsFrom(Overlay overlay) {
		previousInputProcessor = overlay.previousInputProcessor;
		cursorWasShownBefore = overlay.cursorWasShownBefore;
	}
}
