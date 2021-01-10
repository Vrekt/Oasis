package server.game.lobby.impl;

import server.game.entity.player.EntityPlayer;

import java.util.Collection;

/**
 * Represents a lobby.
 */
public interface Lobby {

    /**
     * The upper limit for creating lobby IDs
     */
    int MAX_RANDOM_ID_VALUE = 999999;

    /**
     * The lower limit for creating lobby IDs
     */
    int MIN_RANDOM_ID_VALUE = 123456;

    /**
     * Creates a new lobby.
     *
     * @param lobbyId the lobby ID.
     * @return a new {@link Lobby}
     */
    static Lobby create(int lobbyId) {
        return new LobbyImpl(lobbyId);
    }

    /**
     * Add a player to this lobby
     *
     * @param player the player
     */
    void addPlayer(EntityPlayer player);

    /**
     * Remove a player from this lobby
     *
     * @param player the player
     */
    void removePlayer(EntityPlayer player);

    /**
     * @return the players within this lobby.
     */
    Collection<EntityPlayer> players();

    /**
     * The ID of the lobby
     *
     * @return the ID
     */
    int lobbyId();

}
