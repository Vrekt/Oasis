package me.vrekt.stresstest;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.RandomStringUtils;
import protocol.packet.client.ClientJoinLobby;
import protocol.packet.client.ClientLevelLoaded;
import protocol.packet.client.ClientPosition;
import protocol.packet.client.ClientVelocity;
import protocol.packet.handlers.ServerPacketHandler;
import protocol.packet.server.*;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Handles packets from the server
 */
public final class DefaultServerPacketHandler extends ChannelInboundHandlerAdapter implements ServerPacketHandler {

    private final Channel sendChannel;

    public DefaultServerPacketHandler(Channel sendChannel) {
        this.sendChannel = sendChannel;
    }

    @Override
    public void handleHandshakeReply(ServerHandshakeReply reply) {
        final String user = RandomStringUtils.randomAlphabetic(8);
        System.err.println("Handshaking " + user);
        sendChannel.writeAndFlush(new ClientJoinLobby(user, 999));
    }

    @Override
    public void handleCreateLobbyReply(ServerCreateLobbyReply reply) {

    }

    @Override
    public void handleCreatePlayer(ServerCreatePlayer createPlayer) {

    }

    @Override
    public void handleRemovePlayer(ServerRemovePlayer removePlayer) {

    }

    @Override
    public void handleJoinLobbyReply(ServerJoinLobbyReply reply) {

    }

    @Override
    public void handleLoadLevel(ServerLoadLevel loadLevel) {
        sendChannel.writeAndFlush(new ClientLevelLoaded());

        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            sendChannel.writeAndFlush(new ClientVelocity(0.5f, 0.5f, 1));
            sendChannel.writeAndFlush(new ClientPosition(1, 0, 0));
        }, 0, 150, TimeUnit.MILLISECONDS);
    }

    @Override
    public void handlePlayerVelocity(ServerPlayerVelocity velocity) {

    }

    @Override
    public void handlePlayerPosition(ServerPlayerPosition position) {

    }

    @Override
    public void handleDisconnect(ServerDisconnect disconnect) {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
