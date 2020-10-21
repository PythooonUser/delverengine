package com.interrupt.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.interrupt.dungeoneer.game.Game;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Logger {
    public static final String TAG_DELVER = "Delver";
    public static final String TAG_DELVER_GAME = "DelverGame";
    public static final String TAG_DELVER_GAME_MANAGER = "DelverGameManager";
    public static final String TAG_DELVER_GENERATOR = "DelverGenerator";
    public static final String TAG_GET_AMBIENT_TILE_LIGHTING = "getAmbientTileLighting";

    private Logger() {}

    public static void log(String tag, String message) {
        Gdx.app.log(tag, message);
    }

    public static void log(String tag, String message, Throwable exception) {
        Gdx.app.log(tag, message, exception);
    }

    public static void error(String tag, String message) {
        Gdx.app.error(tag, message);
    }

    public static void logExceptionToFile(Exception ex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ex.printStackTrace(ps);
        ps.close();

        FileHandle f = Game.getFile("errorlog.txt");
        f.writeString("Fatal error in Game loop!\n\n", true);
        f.writeString(baos.toString(), true);
        f.writeString("\n", true);

        Logger.log(Logger.TAG_DELVER_GAME_MANAGER, "Fatal error!", ex);
    }
}
