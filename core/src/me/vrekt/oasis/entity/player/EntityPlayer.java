package me.vrekt.oasis.entity.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import me.vrekt.oasis.Oasis;
import me.vrekt.oasis.animation.AnimationController;
import me.vrekt.oasis.asset.character.CharacterType;
import me.vrekt.oasis.collision.CollisionObject;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.level.world.LevelWorld;

/**
 * Represents an entity that is a player.
 */
public abstract class EntityPlayer extends Entity {

    /**
     * The username of the player
     */
    protected String username;

    /**
     * Controller
     */
    protected AnimationController controller;

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
     * If this player is colliding.
     */
    protected boolean colliding;

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
     * @return {@code true if the player is colliding}
     */
    public boolean colliding() {
        return colliding;
    }

    /**
     * Set colliding
     *
     * @param colliding colliding
     */
    public void colliding(boolean colliding) {
        this.colliding = colliding;
    }

    /**
     * Create player animations
     */
    public void createPlayerAnimations() {
        controller = new AnimationController(Oasis.get().assets().getCharacter(characterType).get());
    }

    /**
     * Start collision
     *
     * @param with with
     */
    public void startCollision(Fixture with) {
        final CollisionObject object = (CollisionObject) with.getUserData();
        switch (object.collisionType()) {
            case INVISIBLE_WALL:
                this.colliding = true;
                break;
            case PLAYER:
                this.colliding = false;
                break;
            default:
                break;
        }
    }

    /**
     * End collision
     */
    public void endCollision() {
        this.colliding = false;
    }

    @Override
    public void spawnEntityInWorld(LevelWorld world, float x, float y) {
        worldIn = world;

        // default body def for all player types (network + local)
        final BodyDef definition = new BodyDef();
        definition.type = BodyDef.BodyType.DynamicBody;
        definition.fixedRotation = true;
        definition.position.set(x, y);

        body = world.box2dWorld().createBody(definition);

        // TODO: Need to fix this shape, idk how.
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(24, 32, new Vector2(12, 12), 0f);

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose();

        previous.set(x, y);
        current.set(x, y);
    }

    @Override
    public void dispose() {
        if (controller != null) controller.dispose();
    }
}
