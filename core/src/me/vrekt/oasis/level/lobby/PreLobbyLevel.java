package me.vrekt.oasis.level.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.entity.player.local.LocalEntityPlayer;
import me.vrekt.oasis.level.Level;

/**
 * Represents the pre-lobby before a game starts.
 */
public final class PreLobbyLevel extends Level {

    /**
     * Default lobby step
     */
    private static final float DEFAULT_STEP = 1 / 60f;

    /**
     * The player
     */
    private final LocalEntityPlayer thePlayer;

    public PreLobbyLevel() {
        super("PreLobbyLevel");
        thePlayer = game.thePlayer();
    }

    @Override
    public boolean loadLevel() {
        Gdx.app.log("PreLobbyLevel", "Loading PreLobby");

        // in the future, custom renderer
        initializeLevelCamera();
        batch = new SpriteBatch();
        levelWorld = new World(new Vector2(0, 0), true);

        try {
            levelMap = new TmxMapLoader().load("levels\\lobby\\Lobby.tmx");
            renderer = new OrthogonalTiledMapRenderer(levelMap, 2f);
            renderer.setView(camera);

            loadSpawnLocation();

            thePlayer.spawnEntityInWorld(levelWorld, spawn);
            game.controller().setPlayerAnimations(thePlayer);
            Gdx.app.log("PreLobbyLevel", "PreLobby loaded successfully.");
        } catch (Exception any) {
            Gdx.app.log("PreLobbyLevel", "Failed to load!", any);
            game.showErrorDialog("Error loading", "Failed to load the lobby.\n" + any.getMessage());
        }
        return false;
    }

    /**
     * Load spawn location
     * TODO: In the future Tiled object setting
     */
    private void loadSpawnLocation() {
        spawn = new Vector2(420, 544);
    }

    /**
     * Initialize the camera for this level
     */
    private void initializeLevelCamera() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(420, 720, 0); // ideal position for this level
        camera.update();
    }

    @Override
    public void update(float delta) {
        worldStepAccumulator += delta;
        while (worldStepAccumulator >= DEFAULT_STEP) {
            levelWorld.step(DEFAULT_STEP, 6, 3);
            worldStepAccumulator -= DEFAULT_STEP;
        }

        thePlayer.update(delta);

        //clamp camera position so it does not go out of bounds.
        camera.position.x = MathUtils.clamp(thePlayer.x(), Gdx.graphics.getWidth() / 2f, 1024 - (camera.viewportWidth / 2));
        camera.position.y = MathUtils.clamp(thePlayer.y(), Gdx.graphics.getHeight() / 2f, 1024 - (camera.viewportHeight / 2));
        camera.update();
    }

    @Override
    public void render(float delta) {
        renderer.setView(camera);
        renderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        thePlayer.render(delta, batch);
        batch.end();
    }

}
