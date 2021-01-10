package me.vrekt.oasis.entity.player;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.animated.AnimationController;

/**
 * Represents an entity that is a player.
 */
public abstract class EntityPlayer extends Entity {

    /**
     * Controller
     */
    protected AnimationController controller;

    /**
     * Used for interpolation
     */
    protected Vector2 previous, current;

    /**
     * Initialize
     *
     * @param entityName name
     * @param entityId   ID
     */
    public EntityPlayer(String entityName, int entityId) {
        super(entityName, entityId);
    }

    /**
     * Create player animations
     *
     * @param animations the animations
     */
    public void createPlayerAnimations(TextureAtlas animations) {
        controller = new AnimationController(animations);
    }

    @Override
    public void spawnEntityInWorld(World world, Vector2 position) {
        final BodyDef definition = new BodyDef();
        definition.type = BodyDef.BodyType.DynamicBody;
        definition.fixedRotation = true;
        definition.position.set(position);

        entityBody = world.createBody(definition);
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(16, 16);

        entityBody.createFixture(shape, 1.0f);
        shape.dispose();

        previous = position;
        current = position;

    }

    @Override
    public void dispose() {
        if(controller != null) controller.dispose();
    }
}
