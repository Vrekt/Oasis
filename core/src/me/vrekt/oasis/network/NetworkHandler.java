package me.vrekt.oasis.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import me.vrekt.oasis.network.codec.ServerProtocolPacketDecoder;
import me.vrekt.oasis.network.handler.FromServerPacketHandler;
import me.vrekt.oasis.network.session.Session;
import protocol.Protocol;
import protocol.channel.ClientChannels;
import protocol.codec.ProtocolPacketEncoder;
import protocol.packet.client.ClientCreateLobbyRequest;

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
        final CompletableFuture<Boolean> result = new CompletableFuture<>();
        group.execute(() -> connectInternal(result));
        return result;
    }

    /**
     * Attempt to create a lobby.
     */
    public void networkCreateLobby() {
        sendChannel.writeAndFlush(new ClientCreateLobbyRequest());
    }

    /**
     * Connect internal
     *
     * @param result the result.
     */
    private void connectInternal(CompletableFuture<Boolean> result) {
        Gdx.app.log("NetworkHandler", "Connecting to master server at " + MASTER_SERVER_IP + ":" + MASTER_SERVER_PORT);
        try {
            sendChannel = bootstrap.connect(MASTER_SERVER_IP, MASTER_SERVER_PORT).sync().channel();
            result.complete(true);

            Gdx.app.log("NetworkHandler", "Connected to master server!");
        } catch (Exception exception) {
            Gdx.app.log("NetworkHandler", "Failed to connect to " + MASTER_SERVER_IP + ":" + MASTER_SERVER_PORT, exception);
            result.completeExceptionally(exception);
        }
    }

    /**
     * Handle a new socket connection
     *
     * @param channel channel
     */
    private void handleSocketConnection(SocketChannel channel) {
        Protocol.initialize();

        channel.pipeline().addLast(new ServerProtocolPacketDecoder(new FromServerPacketHandler()));
        channel.pipeline().addLast(new ProtocolPacketEncoder());
        channel.pipeline().addLast(new Session(channel));
    }

    /**
     * Shuts the network down.
     */
    @Override
    public void dispose() {
        if (sendChannel != null) sendChannel.close();
        group.shutdownGracefully();
    }

}
