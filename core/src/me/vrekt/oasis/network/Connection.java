package me.vrekt.oasis.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.vrekt.oasis.Oasis;
import me.vrekt.oasis.entity.player.local.LocalEntityPlayer;
import me.vrekt.oasis.entity.player.network.NetworkEntityPlayer;
import me.vrekt.oasis.level.Level;
import protocol.Protocol;
import protocol.packet.Packet;
import protocol.packet.client.ClientCreateLobby;
import protocol.packet.client.ClientDisconnect;
import protocol.packet.client.ClientHandshake;
import protocol.packet.client.ClientJoinLobby;
import protocol.packet.handlers.ServerPacketHandler;
import protocol.packet.server.*;

/**
 * Represents the player connection
 */
public final class Connection extends ChannelInboundHandlerAdapter implements ServerPacketHandler, Disposable {

    /**
     * Logging tag
     */
    private static final String TAG = "Connection";

    /**
     * The game
     */
    private final Oasis game;

    /**
     * The player
     */
    private final LocalEntityPlayer thePlayer;

    /**
     * Send channel
     */
    private final Channel channel;

    /**
     * Initialize this connection
     *
     * @param channel the channel
     */
    public Connection(Channel channel) {
        this.channel = channel;
        game = Oasis.get();
        thePlayer = game.thePlayer();
        thePlayer.connection(this);
    }

    /**
     * Handshake with the server
     */
    public void handshake() {
        Gdx.app.log(TAG, "Handshaking with remote server, game=" + Oasis.GAME_VERSION + " protocol=" + Protocol.PROTOCOL_VERSION);
        channel.writeAndFlush(new ClientHandshake(Oasis.GAME_VERSION, Protocol.PROTOCOL_VERSION));
    }

    /**
     * Create a lobby
     *
     * @param username the username
     */
    public void createLobby(String username) {
        Gdx.app.log(TAG, "Attempting to create a new lobby; username=" + username);
        channel.writeAndFlush(new ClientCreateLobby(username));
    }

    /**
     * Join a lobby
     *
     * @param username their username
     * @param lobbyId  the lobby ID
     */
    public void joinLobby(String username, int lobbyId) {
        Gdx.app.log(TAG, "Attempting to join lobby " + lobbyId);
        channel.writeAndFlush(new ClientJoinLobby(username, lobbyId));
    }

    /**
     * Send a packet
     *
     * @param packet the packet
     */
    public void send(Packet packet) {
        channel.writeAndFlush(packet);
    }

    @Override
    public void handleHandshakeReply(ServerHandshakeReply reply) {
        if (reply.allowed()) {
            Gdx.app.log(TAG, "Connected to server successfully.");
        } else {
            game.showMainMenuWithError("Could not connect", "Could not connect to the server! Reason:\n" + reply.notAllowedReason());
        }
    }

    @Override
    public void handleCreateLobbyReply(ServerCreateLobbyReply reply) {
        if (reply.allowed()) {
            Gdx.app.log(TAG, "Initializing local player with ID: " + reply.entityId() + " in lobby " + reply.lobbyId());
            thePlayer.entityId(reply.entityId());
        } else {
            game.showMainMenuWithError("Could not create lobby", "Could not create a new lobby! Reason:\n" + reply.notAllowedReason());
        }
    }

    @Override
    public void handleCreatePlayer(ServerCreatePlayer createPlayer) {
        Gdx.app.log(TAG, "Creating a new network player username=" + createPlayer.username() + ", eid=" + createPlayer.entityId());
        thePlayer.worldIn().spawnPlayer(new NetworkEntityPlayer(createPlayer.username(), createPlayer.entityId()), createPlayer.x(), createPlayer.y());
    }

    @Override
    public void handleRemovePlayer(ServerRemovePlayer removePlayer) {
        Gdx.app.log(TAG, "Removing player " + removePlayer.entityId());
        thePlayer.worldIn().removePlayer(removePlayer.entityId());
    }

    @Override
    public void handleJoinLobbyReply(ServerJoinLobbyReply reply) {
        if (reply.allowed()) {
            Gdx.app.log(TAG, "Initializing local player with ID: " + reply.entityId());
            thePlayer.entityId(reply.entityId());
        } else {
            game.showMainMenuWithError("Could not join lobby", "Could not join the lobby Reason:\n" + reply.notAllowedReason());
        }
    }

    @Override
    public void handleLoadLevel(ServerLoadLevel loadLevel) {
        final Level level = game.level().getLevel(loadLevel.levelName());

        try {
            final boolean result = game.level().startLevel(level).get();
            if (!result) {
                game.showMainMenuWithError("Could not load", "Failed to load the game!");
            } else {
                game.level().showLevel(level);
            }
        } catch (Exception any) {
            game.showMainMenuWithError("Could not load", "Failed to load the game! Cause:\n" + any.getMessage());
        }
    }

    @Override
    public void handlePlayerVelocity(ServerPlayerVelocity velocity) {
        thePlayer.worldIn().updatePlayerVelocity(velocity.entityId(), velocity.velocityX(), velocity.velocityY(), velocity.rotation());
    }

    @Override
    public void handlePlayerPosition(ServerPlayerPosition position) {
        thePlayer.worldIn().updatePlayerPosition(position.entityId(), position.x(), position.y(), position.rotation());
    }

    @Override
    public void handleDisconnect(ServerDisconnect disconnect) {
        Gdx.app.log(TAG, "Disconnected from the server: " + disconnect.reason());
        game.showMainMenuWithError("Disconnected", "You were disconnected from the server:\n" + disconnect.reason());
        dispose();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshake();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Gdx.app.log(TAG, "Exception caught: ", cause);
        game.showMainMenuWithError("Error", "Fatal error while trying to communicate with the server: \n" + cause.getMessage());
        dispose();
    }

    /**
     * TODO: Shareable
     */
    @Override
    public void dispose() {
        if (channel.isWritable()) {
            channel.writeAndFlush(new ClientDisconnect()).addListener(ChannelFutureListener.CLOSE);
        } else {
            channel.close();
        }
    }
}
