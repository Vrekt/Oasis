package me.vrekt.oasis.entity.player;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

/**
 * A player controller
 */
public final class PlayerController implements Disposable {

    /**
     * Player animations
     */
    private final TextureAtlas playerAnimations;

    /**
     * Initializes the animations
     */
    public PlayerController() {
        playerAnimations = new TextureAtlas("player\\Character.atlas");
    }

    /**
     * Set player animations
     *
     * @param player the player
     */
    public void setPlayerAnimations(EntityPlayer player) {
        player.createPlayerAnimations(playerAnimations);
    }

    @Override
    public void dispose() {
        playerAnimations.dispose();
    }
}
