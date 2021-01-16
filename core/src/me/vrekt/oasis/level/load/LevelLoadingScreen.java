package me.vrekt.oasis.level.load;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import me.vrekt.oasis.level.Level;
import me.vrekt.oasis.ui.types.MenuUserInterface;

/**
 * A level loading screen
 */
public final class LevelLoadingScreen extends MenuUserInterface {

    /**
     * Fake delay amount
     */
    private static final float FAKE_DELAY = 1f;

    /**
     * The level to load
     */
    private final Level levelToLoad;

    /**
     * The progress bar
     */
    private ProgressBar progressBar;

    /**
     * Initialize
     *
     * @param levelToLoad the level to load
     */
    public LevelLoadingScreen(Level levelToLoad) {
        this.levelToLoad = levelToLoad;
        createComponents();
    }

    /**
     * Create components
     */
    private void createComponents() {
        final Label label = new Label("Loading level...", skin);
        progressBar = new ProgressBar(0.0f, 100.0f, 1.0f, false, skin);

        root.add(label).uniformX();
        root.row();
        root.add(progressBar).uniformX();
    }

    @Override
    public void show() {
        Gdx.app.log("Loading", "Showing loading screen for " + levelToLoad.levelName());

        progressBar.setValue(progressBar.getValue() + 50.0f);
        final boolean result = levelToLoad.load();

        if (result) {
            progressBar.setValue(progressBar.getValue() + 25.0f);
            schedule(() -> {
                progressBar.setValue(progressBar.getValue() + 25.0f);
                game.show(levelToLoad);
                this.dispose();
            }, FAKE_DELAY);
        } else {
            game.showMainMenuWithError("Failed to load.", "Failed to load the level! Please try again.");
        }
    }
}
