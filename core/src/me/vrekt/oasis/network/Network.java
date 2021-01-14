package me.vrekt.oasis.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import me.vrekt.oasis.network.codec.ServerProtocolPacketDecoder;
import protocol.Protocol;
import protocol.channel.ClientChannels;
import protocol.codec.ProtocolPacketEncoder;

/**
 * Handles basic networking
 */
public final class Network implements Disposable {

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
    private static final String TAG = "Network";

    /**
     * The netty bootstrap
     */
    private final Bootstrap bootstrap;

    /**
     * The main group
     */
    private final EventLoopGroup group;

    /**
     * The connection
     */
    private Connection connection;

    /**
     * Initialize the bootstrap
     */
    public Network() {
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
     * Handle a new socket connection
     *
     * @param channel channel
     */
    private void handleSocketConnection(SocketChannel channel) {
        Protocol.initialize();

        connection = new Connection(channel);
        channel.pipeline().addLast(new ServerProtocolPacketDecoder(connection));
        channel.pipeline().addLast(new ProtocolPacketEncoder());
        channel.pipeline().addLast(connection);
    }

    /**
     * Connect to the server
     *
     * @return the result
     */
    public boolean connectToServer() {
        try {
            bootstrap.connect(MASTER_SERVER_IP, MASTER_SERVER_PORT).sync();
        } catch (Exception any) {
            Gdx.app.log(TAG, "Failed to connect to master server.", any);
            return false;
        }
        return true;
    }

    /**
     * @return the connection
     */
    public Connection connection() {
        return connection;
    }

    @Override
    public void dispose() {
        if (connection != null) connection.dispose();
        group.shutdownGracefully();
    }
}
