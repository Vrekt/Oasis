package me.vrekt.oasis.ui.connect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import me.vrekt.oasis.ui.types.MenuUserInterface;

import java.util.concurrent.CompletableFuture;

/**
 * A loading screen for connecting to the server
 */
public final class ConnectLoadingScreen extends MenuUserInterface {

    /**
     * The progress bar
     */
    private ProgressBar progressBar;

    /**
     * The loading screen callback.
     */
    private final Runnable callback;

    /**
     * Initialize
     */
    public ConnectLoadingScreen(Runnable callback) {
        this.callback = callback;
        createComponents();
    }

    /**
     * Create components
     */
    private void createComponents() {
        final Label label = new Label("Connecting to the server...", skin);
        progressBar = new ProgressBar(0.0f, 100.0f, 1.0f, false, skin);

        root.add(label).uniformX();
        root.row();
        root.add(progressBar).uniformX();

        // initialize the connection state listener
        game.network().onConnectionStateChange(state -> {
            switch (state) {
                case CONNECTED:
                    progressBar.setValue(progressBar.getValue() + 50.0f);
                    break;
                case AUTHENTICATED:
                    progressBar.setValue(progressBar.getValue() + 25.0f);
                    callback.run();

                    // dispose in main thread
                    Gdx.app.postRunnable(this::dispose);
                    break;
                case HANDSHAKING:
                    progressBar.setValue(progressBar.getValue() + 25.0f);
                    break;
            }
        });
    }

    @Override
    public void show() {
        Gdx.app.log("UI", "Showing loading screen to connect to server");
        CompletableFuture.runAsync(() -> {
            final boolean result = game.network().connectToServer();
            if (!result)
                game.showMainMenuWithError("Failed to connect", "Failed to connect to the server.");
        });
    }
}
