package me.vrekt.oasis.entity.player;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import me.vrekt.oasis.Oasis;
import me.vrekt.oasis.asset.character.CharacterType;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.player.render.player.PlayerEntityRenderer;
import me.vrekt.oasis.level.world.LevelWorld;

/**
 * Represents an entity that is a player.
 */
public abstract class EntityPlayer extends Entity {

    /**
     * Player scaling
     */
    public static final float PLAYER_SCALE = 1 / 32.0f;

    /**
     * Default player width (Ruff)
     * Could be different depending on model/rotation
     */
    protected static final float DEFAULT_PLAYER_WIDTH = 24f;

    /**
     * Default player height (Ruff)
     * Could be different depending on model/rotation
     */
    protected static final float DEFAULT_PLAYER_HEIGHT = 34f;

    /**
     * The username of the player
     */
    protected String username;

    /**
     * Renderer
     */
    protected PlayerEntityRenderer renderer;

    /**
     * The world this player is in
     */
    protected LevelWorld worldIn;

    /**
     * The player character type
     */
    protected CharacterType characterType;

    /**
     * The lobby in
     */
    protected int lobbyIn;

    /**
     * Initialize
     *
     * @param entityId ID
     */
    public EntityPlayer(int entityId) {
        super(entityId);
    }

    /**
     * @return the username
     */
    public String username() {
        return username;
    }

    /**
     * Set the username
     *
     * @param username username
     */
    public void username(String username) {
        this.username = username;
    }

    /**
     * @return the character selected
     */
    public CharacterType character() {
        return characterType;
    }

    /**
     * Set the character for this player
     *
     * @param characterType the typed
     */
    public void character(CharacterType characterType) {
        this.characterType = characterType;
    }

    /**
     * @return the world in
     */
    public LevelWorld worldIn() {
        return worldIn;
    }

    /**
     * @return the lobbyIn
     */
    public int lobbyIn() {
        return lobbyIn;
    }

    /**
     * Set
     *
     * @param lobbyIn lobbyIn
     */
    public void lobbyIn(int lobbyIn) {
        this.lobbyIn = lobbyIn;
    }

    /**
     * Create player animations
     */
    public void createPlayerRenderer() {
        renderer = new PlayerEntityRenderer(Oasis.get().assets().getCharacter(characterType).get());
    }

    @Override
    public void spawnEntityInWorld(LevelWorld world, float x, float y) {
        worldIn = world;

        // set initial positions
        previousPosition.set(x, y);
        currentPosition.set(x, y);
        interpolatedPosition.set(x, y);

        // default body def for all player types (network + local)
        final BodyDef definition = new BodyDef();
        definition.type = BodyDef.BodyType.DynamicBody;
        definition.fixedRotation = true;
        definition.position.set(x, y);

        // create body and set the basic poly shape
        body = world.box2dWorld().createBody(definition);
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(DEFAULT_PLAYER_WIDTH / 2f * PLAYER_SCALE, DEFAULT_PLAYER_HEIGHT / 2f * PLAYER_SCALE);

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void dispose() {
        if (renderer != null) renderer.dispose();
    }
}
