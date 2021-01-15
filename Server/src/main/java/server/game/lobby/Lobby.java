package server.game.lobby;

import server.game.entity.player.EntityPlayer;
import server.game.utilities.Disposable;

/**
 * Represents a game lobby
 */
public interface Lobby extends Disposable {

    /**
     * Create a new lobby
     *
     * @param lobbyId the ID
     * @return a new lobby
     */
    static Lobby createLobby(int lobbyId) {
        return new GameLobby(lobbyId);
    }

    /**
     * @return the ID of this lobby
     */
    int lobbyId();

    /**
     * @return a newly generated entity ID.
     */
    int getNewEntityId();

    /**
     * Check if this player can join the lobby
     * @param username their username
     * @return {@code true} if so
     */
    boolean canJoinLobby(String username);

    /**
     * Spawn a player in this lobby
     *
     * @param player the player
     */
    void spawnPlayerInLobby(EntityPlayer player);

    /**
     * Remove a player from this lobby
     *
     * @param player the player
     */
    void removePlayerFromLobby(EntityPlayer player);

    /**
     * Handle when a player is loaded.
     *
     * @param player the player
     */
    void handlePlayerLoaded(EntityPlayer player);

    /**
     * Handle a players velocity update
     *
     * @param player    the player
     * @param velocityX their X velocity
     * @param velocityY their Y velocity
     * @param rotation  the rotation
     */
    void handlePlayerVelocityUpdate(EntityPlayer player, float velocityX, float velocityY, int rotation);

    /**
     * Handle a players position update
     *
     * @param player   the player
     * @param x        their X velocity
     * @param y        their Y velocity
     * @param rotation the rotation
     */
    void handlePlayerPositionUpdate(EntityPlayer player, float x, float y, int rotation);

    /**
     * Tick this lobby
     */
    void tick();

}
