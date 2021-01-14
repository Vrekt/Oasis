package me.vrekt.oasis.entity.player;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.animated.AnimationController;
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
     * Used for interpolation
     */
    protected Vector2 previous, current;

    /**
     * The world this player is in
     */
    protected LevelWorld worldIn;

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
     * @return the world in
     */
    public LevelWorld worldIn() {
        return worldIn;
    }

    /**
     * Create player animations
     *
     * @param animations the animations
     */
    public void createPlayerAnimations(TextureAtlas animations) {
        controller = new AnimationController(animations);
    }

    /**
     * Spawn the player in the world
     *
     * @param world the world
     * @param at    at
     */
    @Override
    public void spawnInWorld(LevelWorld world, Vector2 at) {
        worldIn = world;

        final BodyDef definition = new BodyDef();
        definition.type = BodyDef.BodyType.DynamicBody;
        definition.fixedRotation = true;
        definition.position.set(at);

        entityBody = world.box2dWorld().createBody(definition);
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(16, 16);

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.groupIndex = 0x1;
        fixtureDef.filter.maskBits = 0x1;
        fixtureDef.filter.categoryBits = 0x1;
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;

        entityBody.createFixture(fixtureDef);
        shape.dispose();

        previous = at;
        current = at;
    }

    @Override
    public void dispose() {
        if (controller != null) controller.dispose();
    }
}
