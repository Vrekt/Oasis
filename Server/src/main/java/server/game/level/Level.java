package server.game.level;

import server.game.entity.player.EntityPlayer;

import java.util.Collection;

/**
 * Represents a dungeon/level
 */
public interface Level {

    /**
     * @return the players within this level.
     */
    Collection<EntityPlayer> players();

}
