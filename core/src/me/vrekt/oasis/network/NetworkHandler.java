package me.vrekt.oasis.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import me.vrekt.oasis.Oasis;
import me.vrekt.oasis.entity.rotation.Rotation;
import me.vrekt.oasis.network.codec.ServerProtocolPacketDecoder;
import me.vrekt.oasis.network.handler.DefaultServerPacketHandler;
import protocol.Protocol;
import protocol.channel.ClientChannels;
import protocol.codec.ProtocolPacketEncoder;
import protocol.packet.client.*;

import java.util.concurrent.CompletableFuture;

/**
 * Handles networking within the game.
 */
public final class NetworkHandler implements Disposable {

    /**
     * Master server IP.
     * TODO: Only while dev!
     */
    private static final String MASTER_SERVER_IP = "localhost";

    /**
     * Master server PORT.
     * TODO: Only while dev!
     */
    private static final int MASTER_SERVER_PORT = 8090;

    /**
     * Logging tag
     */
    private static final String TAG = "NetworkHandler";

    /**
     * The netty bootstrap
     */
    private final Bootstrap bootstrap;

    /**
     * The main group
     */
    private final EventLoopGroup group;

    /**
     * The sending channel.
     */
    private Channel sendChannel;

    /**
     * Initialize the bootstrap
     */
    public NetworkHandler() {
        final ClientChannels channelConfig = ClientChannels.get();
        group = channelConfig.group();
        bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(channelConfig.channel())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        handleSocketConnection(channel);
                    }
                });
    }

    /**
     * Connect to the master server.
     *
     * @return the future result.
     */
    public CompletableFuture<Boolean> connectToMasterServer() {
        if (sendChannel != null && sendChannel.isActive()) return CompletableFuture.completedFuture(true);

        final CompletableFuture<Boolean> result = new CompletableFuture<>();
        group.execute(() -> connectInternal(result));
        return result;
    }

    /**
     * Connect internal
     *
     * @param result the result.
     */
    private void connectInternal(CompletableFuture<Boolean> result) {
        try {
            sendChannel = bootstrap.connect(MASTER_SERVER_IP, MASTER_SERVER_PORT).sync().channel();
            result.complete(true);
        } catch (Exception exception) {
            Gdx.app.log(TAG, "Failed to connect to " + MASTER_SERVER_IP + ":" + MASTER_SERVER_PORT, exception);
            result.completeExceptionally(exception);
        }
    }

    /**
     * Try to create a lobby
     */
    public void networkCreateLobby() {
        Gdx.app.log(TAG, "Requesting to create a new lobby.");
        sendChannel.writeAndFlush(new ClientCreateLobby(Oasis.get().thePlayer().entityName()));
    }

    /**
     * TODO: For now 9999
     */
    public void networkJoinLobby() {
        Gdx.app.log(TAG, "Attempting to join lobby 9999");
        sendChannel.writeAndFlush(new ClientJoinLobby(Oasis.get().thePlayer().entityName(), 9999));
    }

    /**
     * Send that we are loaded in
     */
    public void sendNetworkLoaded() {
        sendChannel.writeAndFlush(new ClientLevelLoaded());
    }

    /**
     * Send velocity over the network
     *
     * @param velocityX X vel
     * @param velocityY Y vel
     * @param rotation  the rotation
     */
    public void networkVelocity(float velocityX, float velocityY, Rotation rotation) {
        sendChannel.writeAndFlush(new ClientVelocity(velocityX, velocityY, rotation.ordinal()));
    }

    /**
     * Handle a new socket connection
     *
     * @param channel channel
     */
    private void handleSocketConnection(SocketChannel channel) {
        Protocol.initialize();

        channel.pipeline().addLast(new ServerProtocolPacketDecoder(new DefaultServerPacketHandler(this)));
        channel.pipeline().addLast(new ProtocolPacketEncoder());
        channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                handleChannelActive();
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                handleChannelException(cause);
            }
        });
    }

    /**
     * Handle channel active
     * Handles handshaking with the server once the connection is opened.
     */
    private void handleChannelActive() {
        Gdx.app.log(TAG, "Handshaking with remote server, game=" + Oasis.GAME_VERSION + " protocol=" + Protocol.PROTOCOL_VERSION);
        sendChannel.writeAndFlush(new ClientHandshake(Oasis.GAME_VERSION, Protocol.PROTOCOL_VERSION));
    }

    /**
     * Handle a channel exception
     *
     * @param any any
     */
    private void handleChannelException(Throwable any) {
        close();
        Gdx.app.log(TAG, "Channel exception caught", any);
        Oasis.get().showDialog("Network error", "An error occurred while communicating with the server.\n" + any.getMessage());
        Oasis.get().showMainMenuAsync();
    }

    /**
     * Close
     */
    public void close() {
        if (sendChannel != null) {
            sendChannel.pipeline().remove(ServerProtocolPacketDecoder.class);
            sendChannel.pipeline().remove(ProtocolPacketEncoder.class);

            sendChannel.close();
        }
    }

    /**
     * Shuts the network down.
     */
    @Override
    public void dispose() {
        close();
        group.shutdownGracefully();
    }

}
