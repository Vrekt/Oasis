package me.vrekt.oasis.network.handler;

import com.badlogic.gdx.Gdx;
import me.vrekt.oasis.Oasis;
import protocol.packet.handlers.ServerPacketHandler;
import protocol.packet.server.ServerCreateLobbyResponse;
import protocol.packet.server.ServerDisconnect;
import protocol.packet.server.ServerHandshakeResponse;

/**
 * Handles packets from the server
 */
public final class FromServerPacketHandler implements ServerPacketHandler {

    /**
     * Logging tag
     */
    private static final String TAG = "FromServerPacketHandler";

    /**
     * The game.
     */
    private final Oasis game;

    public FromServerPacketHandler() {
        game = Oasis.get();
    }

    @Override
    public void handleHandshakeResponse(ServerHandshakeResponse response) {
        Gdx.app.log(TAG, "Server accepted handshake, initializing local player.");
        game.networkLocalPlayer(response.entityId());
    }

    @Override
    public void handleCreateLobbyResponse(ServerCreateLobbyResponse response) {
        final boolean allowed = response.allowed();
        Gdx.app.log(TAG, "Create lobby response: " + allowed + ", id=" + response.lobbyId());

        if (allowed) {
            game.loadIntoLocalLobby();
        } else {
            game.showErrorDialog("Server error", "Cannot create lobby! \n" + response.notAllowedReason());
        }
    }

    @Override
    public void handleDisconnect(ServerDisconnect disconnect) {
        Gdx.app.log(TAG, "Server disconnected us with reason: " + disconnect.reason());
        game.showErrorDialog("Disconnected", "Disconnected from the server for: \n" + disconnect.reason());
    }

}
