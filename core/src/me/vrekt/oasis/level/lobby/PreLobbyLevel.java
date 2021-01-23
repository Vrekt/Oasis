package me.vrekt.oasis.level.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import me.vrekt.oasis.level.Level;
import me.vrekt.oasis.level.world.LevelWorld;
import me.vrekt.oasis.utilities.CustomOrthoTiledMapRenderer;
import protocol.packet.client.ClientLevelLoaded;

import java.util.function.Consumer;

/**
 * The pre-lobby level
 */
public final class PreLobbyLevel extends Level {

    /**
     * Default scaling for this level
     */
    private static final float SCALE = 1.0f / 16.0f;

    /**
     * Width
     * 25 meters or tiles
     */
    private static final float VIEWPORT_WIDTH = 25;

    /**
     * Height
     * 19 meters or tiles
     */
    private static final float VIEWPORT_HEIGHT = 19;

    /**
     * If render debug
     */
    private static final boolean RENDER_DEBUG = false;

    /**
     * The camera position before interpolation
     */
    private final Vector3 cameraPosition = new Vector3();

    /**
     * Debug renderer
     */
    private Box2DDebugRenderer debugRenderer;

    public PreLobbyLevel() {
        super("PreLobby");
    }

    @Override
    public void show() {
        Gdx.app.log("Testing", "Showing level test");
    }

    @Override
    public boolean load(Consumer<Float> loadingCallback) {
        try {
            batch = new SpriteBatch();
            tiledMap = new TmxMapLoader().load("levels\\lobby\\Lobby.tmx");
            renderer = new CustomOrthoTiledMapRenderer(tiledMap, SCALE, batch);
            loadingCallback.accept(25.0f);

            world = new LevelWorld(tiledMap, SCALE);

            camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
            camera.position.set(world.spawn().x, world.spawn().y, 0f);
            camera.update();

            loadingCallback.accept(25.0f);

            // add the FPS text label and lobby invite label
            final Label lobbyTextLabel = new Label("Lobby: " + thePlayer.lobbyIn(), skin);
            root.add(lobbyTextLabel);
            root.row();
            root.add(fpsTextLabel);
            root.top().right();

            if (RENDER_DEBUG) debugRenderer = new Box2DDebugRenderer();

            thePlayer.connection().send(new ClientLevelLoaded());
            loadingCallback.accept(25.0f);
            return true;
        } catch (Exception any) {
            log("Failed to load level", any);
        }
        return false;
    }

    @Override
    public void unload() {

    }

    @Override
    public void render(float delta) {
        world.update(delta);
        update();
        updateUi(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        draw();
        world.render(delta, batch);
        batch.end();

        drawUi();

        if (RENDER_DEBUG) debugRenderer.render(world.box2dWorld(), camera.combined);
    }

    @Override
    public void resize(int width, int height) {

    }

    /**
     * Update the camera position
     */
    private void update() {
        // set to player interpolated position
        // clamp the value so camera does not go out of bounds
        cameraPosition.x = MathUtils.clamp(thePlayer.x(), VIEWPORT_WIDTH / 2f, 32 - VIEWPORT_WIDTH / 2f);
        cameraPosition.y = MathUtils.clamp(thePlayer.y(), VIEWPORT_HEIGHT / 2f, 32 - VIEWPORT_HEIGHT / 2f);
        // interpolate further
        camera.position.interpolate(cameraPosition, 1.0f, Interpolation.linear);
        // update
        camera.update();
    }

    /**
     * Draw the tiled map
     */
    private void draw() {
        renderer.setView(camera);
        renderer.render();
    }

}
