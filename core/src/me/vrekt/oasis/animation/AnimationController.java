package me.vrekt.oasis.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.entity.rotation.Rotation;

import java.util.HashMap;
import java.util.Map;

/**
 * Controls animations
 * Currently only for player animations
 */
public final class AnimationController implements Disposable {

    /**
     * The base animations
     */
    private final TextureAtlas animations;

    /**
     * Animations by rotation
     */
    private final Map<Rotation, Animation<TextureRegion>> animationsByRotation = new HashMap<>();

    /**
     * Idle states
     */
    private final TextureRegion idleUp, idleDown, idleLeft, idleRight;

    /**
     * The current animation
     */
    private Animation<TextureRegion> currentAnimation;

    /**
     * The last rotation value
     */
    private Rotation lastRotation;

    /**
     * If animation should play
     */
    private boolean shouldAnimate;

    /**
     * The animation time
     */
    private float animationTime;

    /**
     * Initialize this controller
     *
     * @param animations the animations set
     */
    public AnimationController(TextureAtlas animations) {
        this.animations = animations;

        idleUp = animations.findRegion("walking_up_idle");
        idleDown = animations.findRegion("walking_down_idle");
        idleLeft = animations.findRegion("walking_left_idle");
        idleRight = animations.findRegion("walking_right_idle");

        animationsByRotation.put(Rotation.FACING_UP, createAnimation(animations.findRegion("walking_up", 1), animations.findRegion("walking_up", 2)));
        animationsByRotation.put(Rotation.FACING_DOWN, createAnimation(animations.findRegion("walking_down", 1), animations.findRegion("walking_down", 2)));
        animationsByRotation.put(Rotation.FACING_LEFT, createAnimation(animations.findRegion("walking_left", 1), animations.findRegion("walking_left", 2)));
        animationsByRotation.put(Rotation.FACING_RIGHT, createAnimation(animations.findRegion("walking_right", 1), animations.findRegion("walking_right", 2)));

        currentAnimation = animationsByRotation.get(Rotation.FACING_RIGHT);
    }

    /**
     * Update this controller
     *
     * @param rotation     the current rotation
     * @param hasVelocity  if we have velocity
     * @param hasCollision if we have collision
     */
    public void update(Rotation rotation, boolean hasVelocity, boolean hasCollision) {
        shouldAnimate = hasVelocity && !hasCollision;

        if (!shouldAnimate) animationTime = 0f;

        if (shouldAnimate && rotation != lastRotation) {
            lastRotation = rotation;
            currentAnimation = animationsByRotation.get(rotation);
        }
    }

    /**
     * Render the animation
     *
     * @param delta    the delta
     * @param rotation the current rotation
     * @param batch    the batch
     */
    public void render(float delta, Rotation rotation, Vector2 location, SpriteBatch batch) {
        if (!shouldAnimate) {
            drawIdleState(rotation, location, batch);
        } else {
            batch.draw(currentAnimation.getKeyFrame(animationTime), location.x, location.y);
            animationTime += delta;
        }
    }

    /**
     * Draw the idle state
     *
     * @param batch batch
     */
    private void drawIdleState(Rotation rotation, Vector2 location, SpriteBatch batch) {
        switch (rotation) {
            case FACING_UP:
                batch.draw(idleUp, location.x, location.y);
                break;
            case FACING_DOWN:
                batch.draw(idleDown, location.x, location.y);
                break;
            case FACING_LEFT:
                batch.draw(idleLeft, location.x, location.y);
                break;
            case FACING_RIGHT:
                batch.draw(idleRight, location.x, location.y);
                break;
        }
    }

    /**
     * Create a new animation
     *
     * @param first  first region
     * @param second second region
     * @return a new {@link Animation}
     */
    private Animation<TextureRegion> createAnimation(TextureAtlas.AtlasRegion first, TextureAtlas.AtlasRegion second) {
        final Animation<TextureRegion> animation = new Animation<>(0.25f, first, second);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }

    @Override
    public void dispose() {
        animations.dispose();
        animationsByRotation.clear();
        currentAnimation = null;
    }
}
