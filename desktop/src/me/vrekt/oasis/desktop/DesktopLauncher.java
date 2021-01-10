package me.vrekt.oasis.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import me.vrekt.oasis.OasisGameAdapter;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.width = 800;
        config.height = 600;
        config.backgroundFPS = 60;
        config.foregroundFPS = 0;
        config.resizable = false;
        config.title = "Oasis";

        new LwjglApplication(new OasisGameAdapter() {
            @Override
            public void setFps(int fps) {
                config.foregroundFPS = fps;
            }
        }, config);
    }
}
