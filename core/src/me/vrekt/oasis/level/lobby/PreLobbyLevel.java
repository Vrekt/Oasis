package me.vrekt.oasis.level.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import me.vrekt.oasis.level.Level;
import me.vrekt.oasis.level.world.LevelWorld;
import protocol.packet.client.ClientLevelLoaded;

/**
 * The level for the pre-game lobby.
 */
public final class PreLobbyLevel extends Level {

    /**
     * The scale of this level.
     */
    private static final float SCALE = 2f;

    /**
     * Width of screen halved by 2
     */
    private static final float WIDTH_HALVED = Gdx.graphics.getWidth() / 2f;

    /**
     * Height of screen halved by 2
     */
    private static final float HEIGHT_HALVED = Gdx.graphics.getHeight() / 2f;

    /**
     * The max camera X bounds
     */
    private static final float CAMERA_X_BOUNDS = 1024 - WIDTH_HALVED;

    /**
     * The max camera Y bounds
     */
    private static final float CAMERA_Y_BOUNDS = 1024 - HEIGHT_HALVED;

    /**
     * If debug rendering should be done.
     */
    private static final boolean DO_DEBUG_RENDERING = false;

    /**
     * Debug renderer
     */
    private Box2DDebugRenderer debugRenderer;

    public PreLobbyLevel() {
        super("PreLobby");
    }

    @Override
    public void show() {
        Gdx.app.log("PreLobby", "Showing PreLobby");
        if (debugRenderer == null) debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public boolean load() {
        final long now = System.currentTimeMillis();
        Gdx.app.log("PreLobbyLevel", "Loading PreLobby");

        try {
            tiledMap = new TmxMapLoader().load("levels\\lobby\\Lobby.tmx");
            renderer = new OrthogonalTiledMapRenderer(tiledMap, SCALE);
            batch = new SpriteBatch();

            // create the new world and initialize the camera.
            world = new LevelWorld(tiledMap, SCALE);
            initializeLevelCamera(world.spawn());

            // disable inputs for this level
            thePlayer.disableInputs(Input.Keys.W, Input.Keys.S);

            // add the lobby ID text.
            final Label lobbyIdText = new Label("Lobby: " + thePlayer.lobbyIn(), skin);
            root.add(lobbyIdText);

            // position and add it.
            root.top().right();
            stage.clear();
            stage.addActor(root);

            loaded = true;
            game.connection().send(new ClientLevelLoaded());
            Gdx.app.log("PreLobbyLevel", "PreLobby loaded successfully, took " + (System.currentTimeMillis() - now) + " ms");
        } catch (Exception any) {
            Gdx.app.log("PreLobbyLevel", "Failed to load!", any);
            return false;
        }
        return true;
    }

    @Override
    public void unload() {
        thePlayer.enableInputs(Input.Keys.W, Input.Keys.S);
        loaded = false;
    }

    /**
     * Initialize the camera for this level
     */
    private void initializeLevelCamera(Vector2 location) {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(location.x, location.y, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
        draw(delta);
    }

    /**
     * Update internal
     *
     * @param delta delta
     */
    private void update(float delta) {
        world.update(delta);

        // clamp camera position so it does not go out of bounds.
        camera.position.x = MathUtils.clamp(thePlayer.x(), WIDTH_HALVED, CAMERA_X_BOUNDS);
        camera.position.y = MathUtils.clamp(thePlayer.y(), HEIGHT_HALVED, CAMERA_Y_BOUNDS);
        camera.update();

        stage.act(delta);
    }

    /**
     * Render internal
     *
     * @param delta delta
     */
    private void draw(float delta) {
        renderer.setView(camera);
        renderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        world.render(delta, batch);
        batch.end();

        stage.draw();
        if (DO_DEBUG_RENDERING) debugRenderer.render(world.box2dWorld(), camera.combined);
    }

    @Override
    public void dispose() {
        if (debugRenderer != null) debugRenderer.dispose();
        super.dispose();
    }

}
