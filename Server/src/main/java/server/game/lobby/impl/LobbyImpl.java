package server.game.lobby.impl;

import server.game.entity.player.EntityPlayer;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lobby implementation
 */
final class LobbyImpl implements Lobby {

    /**
     * Map of players within this lobby
     */
    private final Map<Integer, EntityPlayer> players = new ConcurrentHashMap<>();

    /**
     * The ID of this lobby.
     */
    private final int lobbyId;

    /**
     * Initialize
     *
     * @param lobbyId the lobby ID.
     */
    public LobbyImpl(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    @Override
    public void addPlayer(EntityPlayer player) {
        if (players.size() > 1) {
            // notify other players of join.
        }
        players.put(player.entityId(), player);
    }

    @Override
    public void removePlayer(EntityPlayer player) {
        players.remove(player.entityId());
    }

    @Override
    public Collection<EntityPlayer> players() {
        return players.values();
    }

    @Override
    public int lobbyId() {
        return lobbyId;
    }
}
