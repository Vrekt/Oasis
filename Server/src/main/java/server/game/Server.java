package server.game;

import server.game.lobby.Lobby;

/**
 * Represents a game server
 */
public interface Server {

    /**
     * The game version
     */
    int GAME_VERSION = 10;

    /**
     * Start the server
     */
    void start();

    /**
     * Stop the server
     */
    void stop();

    /**
     * @return {@code true} if a new lobby can be created
     */
    boolean canCreateNewLobby();

    /**
     * @return a new lobby ID
     */
    int getNewLobbyId();

    /**
     * Add a lobby
     *
     * @param lobby the lobby
     */
    void addLobby(Lobby lobby);

    /**
     * Remove a lobby
     *
     * @param lobby the lobby
     */
    void removeLobby(Lobby lobby);

    /**
     * Get a lobby
     *
     * @param lobbyId the ID
     * @return the lobby
     */
    Lobby getLobby(int lobbyId);

    /**
     * @return the server
     */
    static Server getServer() {
        return OasisGameServer.get();
    }

}
