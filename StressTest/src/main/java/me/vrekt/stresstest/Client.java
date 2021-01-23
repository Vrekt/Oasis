package me.vrekt.stresstest;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import protocol.Protocol;
import protocol.channel.ClientChannels;
import protocol.codec.ProtocolPacketEncoder;
import protocol.packet.client.ClientHandshake;

/**
 * Kind of like a DDOS stress test
 */
public final class Client {


    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 500; i++) {
            new Client();
        }

    }

    private Client() {
        new Thread(() -> {
            final ClientChannels channelConfig = ClientChannels.get();
            final EventLoopGroup group = channelConfig.group();
            final Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(group)
                    .channel(channelConfig.channel())
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            Protocol.initialize();

                            ch.pipeline().addLast(new ServerProtocolPacketDecoder(new DefaultServerPacketHandler(ch)));
                            ch.pipeline().addLast(new ProtocolPacketEncoder());
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) {
                                    System.err.println("Con");
                                    ch.writeAndFlush(new ClientHandshake(10, Protocol.PROTOCOL_VERSION));
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                    cause.printStackTrace();
                                }
                            });
                        }
                    });

            try {
                bootstrap.connect("localhost", 8090).sync().channel();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }


        }).start();
    }


}
