package me.vrekt.oasis.entity.player.render;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/**
 * An entity controller
 */
public abstract class EntityRenderer implements Disposable {

    /**
     * Textures
     */
    protected final TextureAtlas textures;

    /**
     * Initialize
     *
     * @param textures the texture(s) for the entity
     */
    public EntityRenderer(TextureAtlas textures) {
        this.textures = textures;
    }

    /**
     * Create a new animation
     *
     * @param region the region name
     * @return a new {@link Animation}
     */
    protected Animation<TextureRegion> createAnimation(String region) {
        final Animation<TextureRegion> animation = new Animation<>(0.25f, textures.findRegion(region, 1), textures.findRegion(region, 2));
        animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }

}
