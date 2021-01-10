package server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import protocol.Protocol;
import protocol.channel.ServerChannels;
import protocol.codec.ProtocolPacketEncoder;
import server.game.GameServer;
import server.netty.codec.ClientProtocolPacketDecoder;

public class DedicatedNettyServer {

    /**
     * The netty bootstrap.
     */
    private final ServerBootstrap bootstrap;

    /**
     * The netty group
     */
    private final EventLoopGroup parent, child;

    /**
     * the game server
     */
    private final GameServer gameServer;

    public static void main(String[] args) throws InterruptedException {
        new DedicatedNettyServer().bind(args[0], Integer.parseInt(args[1]));
    }

    /**
     * Initialize the server.
     */
    public DedicatedNettyServer() {
        final ServerChannels channels = ServerChannels.get();
        bootstrap = new ServerBootstrap();

        parent = channels.group();
        child = channels.newGroup();

        bootstrap.group(parent, child)
                .channel(channels.channel())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        handleSocketConnection(channel);
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);

        gameServer = new GameServer();
        Protocol.initialize();
    }

    /**
     * Handle a new socket channel
     *
     * @param channel the channel
     */
    private void handleSocketConnection(SocketChannel channel) {
        final GameServer.LocalGameServerSession session = gameServer.createSession(channel);

        channel.pipeline().addLast(new ClientProtocolPacketDecoder(session));
        channel.pipeline().addLast(new ProtocolPacketEncoder());
        channel.pipeline().addLast(session);
    }

    /**
     * Bind
     *
     * @param address the address
     * @param port    the port
     * @throws InterruptedException if an error occurred
     */
    public void bind(String address, int port) throws InterruptedException {
        System.err.println("Attempting to bind to " + address + ":" + port);

        try {
            final ChannelFuture future = bootstrap.bind(address, port).sync();
            System.err.println("Bounded to " + address + ":" + port + " successfully.");

            future.channel().closeFuture().sync();
        } finally {
            child.shutdownGracefully();
            parent.shutdownGracefully();
        }
    }

}
