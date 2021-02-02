package me.vrekt.oasis.entity.player.render.player;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.player.EntityPlayer;
import me.vrekt.oasis.entity.player.render.EntityRenderer;
import me.vrekt.oasis.entity.rotation.Rotation;

import java.util.HashMap;
import java.util.Map;

/**
 * A renderer for a player
 */
public final class PlayerEntityRenderer extends EntityRenderer {

    /**
     * Animations by rotation
     */
    private final Map<Rotation, Animation<TextureRegion>> animationsByRotation = new HashMap<>();

    /**
     * Idle states by rotation
     */
    private final Map<Rotation, TextureRegion> idleStates = new HashMap<>();

    /**
     * The current animation
     */
    private Animation<TextureRegion> currentAnimation;

    /**
     * The current idle state
     */
    private TextureRegion currentIdleState;

    /**
     * The last rotation value
     */
    private Rotation lastAnimationState, lastIdleState;

    /**
     * If animation should play
     */
    private boolean shouldAnimate;

    /**
     * The animation time
     */
    private float animationTime;

    public PlayerEntityRenderer(TextureAtlas textures) {
        super(textures);

        idleStates.put(Rotation.FACING_UP, textures.findRegion("walking_up_idle"));
        idleStates.put(Rotation.FACING_DOWN, textures.findRegion("walking_down_idle"));
        idleStates.put(Rotation.FACING_LEFT, textures.findRegion("walking_left_idle"));
        idleStates.put(Rotation.FACING_RIGHT, textures.findRegion("walking_right_idle"));

        animationsByRotation.put(Rotation.FACING_UP, createAnimation("walking_up"));
        animationsByRotation.put(Rotation.FACING_DOWN, createAnimation("walking_down"));
        animationsByRotation.put(Rotation.FACING_LEFT, createAnimation("walking_left"));
        animationsByRotation.put(Rotation.FACING_RIGHT, createAnimation("walking_right"));

        currentIdleState = idleStates.get(Rotation.FACING_RIGHT);
        currentAnimation = animationsByRotation.get(Rotation.FACING_RIGHT);
    }

    /**
     * Update this renderer
     *
     * @param rotation    the current rotation
     * @param hasVelocity if we have velocity
     */
    public void update(Rotation rotation, boolean hasVelocity) {
        shouldAnimate = hasVelocity;

        if (!shouldAnimate) {
            animationTime = 0f;
        }

        if (!shouldAnimate && rotation != lastIdleState) {
            lastIdleState = rotation;
            currentIdleState = idleStates.get(rotation);
        }

        if (shouldAnimate && rotation != lastAnimationState) {
            lastAnimationState = rotation;
            currentAnimation = animationsByRotation.get(rotation);
        }

    }

    /**
     * Render the animation
     *
     * @param delta    the delta
     * @param location the location
     * @param batch    the batch
     */
    public void render(float delta, Vector2 location, SpriteBatch batch) {
        if (!shouldAnimate) {
            drawIdleState(location, batch);
        } else {
            // offset the player position to fit within the box2d bounds
            batch.draw(currentAnimation.getKeyFrame(animationTime),
                    location.x - (16 * EntityPlayer.PLAYER_SCALE) / 2f,
                    location.y - (18 * EntityPlayer.PLAYER_SCALE) / 2f,
                    (16 * EntityPlayer.PLAYER_SCALE),
                    (18 * EntityPlayer.PLAYER_SCALE));
            animationTime += delta;
        }
    }

    /**
     * Draw the idle state
     * Offsets the current location to draw within box2d bounds.
     *
     * @param location the location
     * @param batch    batch
     */
    private void drawIdleState(Vector2 location, SpriteBatch batch) {
        batch.draw(currentIdleState,
                location.x - (16 * EntityPlayer.PLAYER_SCALE) / 2f,
                location.y - (18 * EntityPlayer.PLAYER_SCALE) / 2f,
                (16 * EntityPlayer.PLAYER_SCALE),
                (18 * EntityPlayer.PLAYER_SCALE));
    }


    @Override
    public void dispose() {
        idleStates.clear();
        animationsByRotation.clear();
        currentAnimation = null;
    }
}
