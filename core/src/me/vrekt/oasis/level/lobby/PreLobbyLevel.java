package me.vrekt.oasis.level.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import me.vrekt.oasis.level.Level;
import me.vrekt.oasis.level.load.LoadingProgressCallback;
import protocol.packet.client.ClientLevelLoaded;

/**
 * A pre lobby level.
 */
public final class PreLobbyLevel extends Level {

    /**
     * Default scaling for this level
     */
    private static final float SCALE = 1 / 16.0f;

    /**
     * Viewport
     */
    private ExtendViewport viewport;

    /**
     * Map layers for rendering.
     */
    private Array<TiledMapTileLayer> layers;

    public PreLobbyLevel() {
        super("PreLobby");
    }

    @Override
    public void show() {
        log("Showing PreLobby.");
    }

    @Override
    public boolean load(LoadingProgressCallback progress) {
        try {
            loadInternal("levels\\lobby\\PreLobby.tmx", SCALE);
            layers = tiledMap.getLayers().getByType(TiledMapTileLayer.class);
            progress.step();

            camera = new OrthographicCamera();
            camera.setToOrtho(false, Gdx.graphics.getWidth() / 16f / 2f, Gdx.graphics.getHeight() / 16f / 2f);
            viewport = new ExtendViewport(Gdx.graphics.getWidth() / SCALE, Gdx.graphics.getHeight() / SCALE);
            progress.step();

            camera.position.set(world.spawn().x, world.spawn().y, 0f);
            camera.update();

            final Label lobbyLabel = new Label("Lobby: " + thePlayer.lobbyIn(), skin);
            root.add(lobbyLabel);
            root.row();

            root.add(fpsTextLabel);
            root.top().left();

            thePlayer.connection().send(new ClientLevelLoaded());
            progress.step();
        } catch (Exception any) {
            log("Failed to load PreLobby.", any);
            return false;
        }
        return true;
    }

    @Override
    public void render(float delta) {
        updateInternal(delta);
        drawInternal(delta);
    }

    /**
     * Update the world and camera position
     *
     * @param delta delta
     */
    private void updateInternal(float delta) {
        world.update(delta);
        camera.position.set(thePlayer.x(), thePlayer.y(), 0f);
        camera.update();

        updateUi(delta);
    }

    /**
     * Draw internal
     *
     * @param delta delta
     */
    private void drawInternal(float delta) {
        Gdx.gl.glClearColor(69 / 255f, 8f / 255f, 163f / 255, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        AnimatedTiledMapTile.updateAnimationBaseTime();
        renderer.setView(camera);
        for (TiledMapTileLayer layer : layers) {
            renderer.renderTileLayer(layer);
        }

        world.render(delta, batch);
        batch.end();

        drawUi();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        camera.setToOrtho(false, width / 16f / 2f, height / 16f / 2f);
    }

    @Override
    public void unload() {

    }
}
