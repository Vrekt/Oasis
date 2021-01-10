package server.game.level;

import server.game.entity.player.EntityPlayer;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default level implementation
 */
final class LevelImpl implements Level {

    /**
     * Map of player entities within this level.
     */
    private final Map<Integer, EntityPlayer> playerEntities = new ConcurrentHashMap<>();

    @Override
    public Collection<EntityPlayer> players() {
        return playerEntities.values();
    }
}
