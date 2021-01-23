package me.vrekt.oasis.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import me.vrekt.oasis.OasisGameAdapter;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.width = 800;
        config.height = 600;
        config.vSyncEnabled = false;
        config.title = "Oasis";
        config.backgroundFPS = 0;
        config.foregroundFPS = 0;

        new LwjglApplication(new OasisGameAdapter(), config);
    }
}
