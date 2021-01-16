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
import me.vrekt.oasis.level.load.LevelLoadingScreen;
import me.vrekt.oasis.network.states.ConnectionState;
import protocol.Protocol;
import protocol.packet.Packet;
import protocol.packet.client.ClientCreateLobby;
import protocol.packet.client.ClientDisconnect;
import protocol.packet.client.ClientHandshake;
import protocol.packet.client.ClientJoinLobby;
import protocol.packet.handlers.ServerPacketHandler;
import protocol.packet.server.*;

import java.util.function.Consumer;

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
     * Connection state consumer
     */
    private final Consumer<ConnectionState> connectionStateConsumer;

    /**
     * Initialize this connection
     *
     * @param channel the channel
     */
    public Connection(Channel channel, Consumer<ConnectionState> connectionStateConsumer) {
        this.channel = channel;
        this.connectionStateConsumer = connectionStateConsumer;

        game = Oasis.get();
        thePlayer = game.thePlayer();
        thePlayer.connection(this);
    }

    /**
     * Handshake with the server
     */
    public void handshake() {
        connectionStateConsumer.accept(ConnectionState.HANDSHAKING);

        Gdx.app.log(TAG, "Handshaking with remote server, game=" + Oasis.GAME_VERSION + " protocol=" + Protocol.PROTOCOL_VERSION);
        channel.writeAndFlush(new ClientHandshake(Oasis.GAME_VERSION, Protocol.PROTOCOL_VERSION));
    }


    /**
     * Create a lobby
     */
    public void createLobby() {
        Gdx.app.log(TAG, "Attempting to create a new lobby");
        channel.writeAndFlush(new ClientCreateLobby(game.thePlayer().username(), game.thePlayer().character().ordinal()));
    }

    /**
     * Join a lobby
     */
    public void joinLobby() {
        Gdx.app.log(TAG, "Attempting to join lobby " + game.thePlayer().lobbyIn());
        channel.writeAndFlush(new ClientJoinLobby(game.thePlayer().username(), game.thePlayer().character().ordinal(), game.thePlayer().lobbyIn()));
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
            connectionStateConsumer.accept(ConnectionState.AUTHENTICATED);
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
            thePlayer.lobbyIn(reply.lobbyId());
        } else {
            game.showMainMenuWithError("Could not create lobby", "Could not create a new lobby! Reason:\n" + reply.notAllowedReason());
        }
    }

    @Override
    public void handleCreatePlayer(ServerCreatePlayer createPlayer) {
        Gdx.app.log(TAG, "Creating a new network player username=" + createPlayer.username() + ", eid=" + createPlayer.entityId());
        thePlayer.worldIn().createPlayer(new NetworkEntityPlayer(createPlayer.username(), createPlayer.character(), createPlayer.entityId()), createPlayer.x(), createPlayer.y());
    }

    @Override
    public void handleSpawnPlayer(ServerSpawnPlayer spawnPlayer) {
        Gdx.app.log(TAG, "Spawning a network player, username=" + spawnPlayer.username() + ", eid=" + spawnPlayer.entityId());
        thePlayer.worldIn().spawnPlayer(new NetworkEntityPlayer(spawnPlayer.username(), spawnPlayer.character(), spawnPlayer.entityId()));
    }

    @Override
    public void handleRemovePlayer(ServerRemovePlayer removePlayer) {
        Gdx.app.log(TAG, "Removing player " + removePlayer.entityId());
        thePlayer.worldIn().removePlayer(removePlayer.entityId());
    }

    @Override
    public void handleJoinLobbyReply(ServerJoinLobbyReply reply) {
        if (reply.allowed()) {
            Gdx.app.log(TAG, "Initializing local player with ID: " + reply.entityId() + " in lobby " + reply.lobbyId());
            thePlayer.entityId(reply.entityId());
            thePlayer.lobbyIn(reply.lobbyId());
        } else {
            game.showMainMenuWithError("Could not join lobby", "Could not join the lobby Reason:\n" + reply.notAllowedReason());
        }
    }

    @Override
    public void handleLoadLevel(ServerLoadLevel loadLevel) {
        final Level level = game.level().getLevel(loadLevel.levelName());
        // will automatically load the level and display it.
        Gdx.app.postRunnable(() -> {
            final LevelLoadingScreen screen = new LevelLoadingScreen(level);
            game.showSync(screen);
        });
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
        game.network().setConnected(false);
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
        game.network().setConnected(false);
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
