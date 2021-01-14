package me.vrekt.oasis.level.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.level.Level;
import me.vrekt.oasis.level.world.LevelWorld;
import protocol.packet.client.ClientLevelLoaded;

/**
 * The level for the pre-game lobby.
 */
public final class PreLobbyLevel extends Level {

    /**
     * The drawing location for the lobby code text
     */
    private final Vector3 lobbyIdText = new Vector3(710f, 5f, 0f);

    public PreLobbyLevel() {
        super("PreLobby");
    }

    @Override
    public void show() {
        Gdx.app.log("PreLobby", "Showing pre-lobby");
    }

    @Override
    public boolean load() {
        final long now = System.currentTimeMillis();
        Gdx.app.log("PreLobbyLevel", "Loading PreLobby");

        try {
            tiledMap = new TmxMapLoader().load("levels\\lobby\\Lobby.tmx");
            renderer = new OrthogonalTiledMapRenderer(tiledMap, 2f);

            // in the future, custom renderer
            initializeLevelCamera();
            batch = new SpriteBatch();
            font = new BitmapFont(Gdx.files.internal("ui/alagard_by_pix3m-d6awiwp.fnt"));

            world = new LevelWorld();
            renderer.setView(camera);

            thePlayer.spawnInWorld(world, new Vector2(420, 544));
            thePlayer.createPlayerAnimations();
            thePlayer.disableInputs(Input.Keys.W, Input.Keys.S);

            loaded = true;
            Gdx.app.log("PreLobbyLevel", "PreLobby loaded successfully, took " + (System.currentTimeMillis() - now) + " ms");

            game.connection().send(new ClientLevelLoaded());
        } catch (Exception any) {
            Gdx.app.log("PreLobbyLevel", "Failed to load!", any);
            return false;
        }
        return true;
    }

    @Override
    public void unload() {
        thePlayer.enableInputs(Input.Keys.W, Input.Keys.S);
    }

    /**
     * Initialize the camera for this level
     */
    private void initializeLevelCamera() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(420, 720, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (loaded) {
            renderInternal(delta);
            updateInternal(delta);
        }
    }

    /**
     * Update internal
     *
     * @param delta delta
     */
    private void updateInternal(float delta) {
        world.update(delta);
        thePlayer.update(delta);

        // clamp camera position so it does not go out of bounds.
        camera.position.x = MathUtils.clamp(thePlayer.x(), Gdx.graphics.getWidth() / 2f, 1024 - (camera.viewportWidth / 2));
        camera.position.y = MathUtils.clamp(thePlayer.y(), Gdx.graphics.getHeight() / 2f, 1024 - (camera.viewportHeight / 2));
        camera.update();
    }

    /**
     * Render internal
     *
     * @param delta delta
     */
    private void renderInternal(float delta) {
        renderer.setView(camera);
        renderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        world.render(delta, batch, font);
        thePlayer.render(delta, batch);

        final Vector3 location = camera.unproject(new Vector3(710, 5f, 0));
        font.draw(batch, "Lobby: " + thePlayer.lobbyIn(), location.x, location.y);
        batch.end();
    }

}
