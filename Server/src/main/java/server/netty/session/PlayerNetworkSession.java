package server.netty.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import protocol.Protocol;
import protocol.packet.Packet;
import protocol.packet.client.*;
import protocol.packet.handlers.ClientPacketHandler;
import protocol.packet.server.*;
import server.game.Server;
import server.game.entity.player.EntityPlayer;
import server.game.lobby.Lobby;

import java.io.IOException;

/**
 * Represents a players netty/network session
 * TODO: Security --
 * TODO Packet Spamming
 * TODO Packets at wrong times (loaded when not in lobby, position, etc)
 * TODO Player already created
 * TODO Already in lobbies, etc
 */
public final class PlayerNetworkSession extends ChannelInboundHandlerAdapter implements ClientPacketHandler {

    /**
     * The channel for this session
     */
    private final Channel channel;

    /**
     * The player for this network session
     */
    private EntityPlayer player;

    /**
     * Initialize a new session
     *
     * @param channel the channel
     */
    public PlayerNetworkSession(Channel channel) {
        this.channel = channel;
    }

    /**
     * Send a packet now
     *
     * @param packet the packet
     */
    private void sendNow(Packet packet) {
        channel.writeAndFlush(packet);
    }

    /**
     * Disconnect this player
     */
    private void disconnect() {
        if (player != null) player.disconnect();
        player = null;
    }

    /**
     * Close this session
     */
    private void close() {
        if (channel.isOpen()) {
            disconnect();
            channel.close();
        }
    }

    /**
     * Create a player and spawn them in the lobby
     *
     * @param username the username
     * @param lobby    the lobby
     * @return the player
     */
    private EntityPlayer createPlayerAndSpawnInLobby(String username, Lobby lobby) {
        final int eid = lobby.getNewEntityId();
        player = new EntityPlayer(username, eid, channel);
        player.setInLobby(lobby);
        player.setLocation(Lobby.SPAWN_X, Lobby.SPAWN_Y);
        lobby.spawnPlayerInLobby(player);
        return player;
    }

    @Override
    public void handleHandshake(ClientHandshake handshake) {
        if (handshake.gameVersion() != Server.GAME_VERSION
                || handshake.protocolVersion() != Protocol.PROTOCOL_VERSION) {
            sendNow(new ServerHandshakeReply(false, "Game/Protocol version out of date."));
            close();
        } else {
            sendNow(new ServerHandshakeReply(true));
        }
    }

    @Override
    public void handleCreateLobby(ClientCreateLobby createLobby) {
        if (Server.getServer().canCreateNewLobby()) {
            // create the lobby in server
            final int newLobbyId = Server.getServer().getNewLobbyId();
            final Lobby lobby = Lobby.createLobby(newLobbyId);
            Server.getServer().addLobby(lobby);

            // spawn them in
            player = createPlayerAndSpawnInLobby(createLobby.username(), lobby);

            // have them load the level
            sendNow(new ServerLoadLevel("PreLobby"));
            sendNow(new ServerCreateLobbyReply(newLobbyId, player.entityId()));
        } else {
            sendNow(new ServerCreateLobbyReply(false, "Server is full."));
        }
    }

    @Override
    public void handleJoinLobby(ClientJoinLobby joinLobby) {
        final Lobby lobby = Server.getServer().getLobby(joinLobby.lobbyId());
        if (lobby == null) {
            sendNow(new ServerJoinLobbyReply(false, "Lobby does not exist."));
        } else {
            final String username = joinLobby.username();

            if (lobby.canJoinLobby(username)) {
                player = createPlayerAndSpawnInLobby(username, lobby);
                sendNow(new ServerLoadLevel("PreLobby"));
                sendNow(new ServerJoinLobbyReply(lobby.lobbyId(), player.entityId()));
            } else {
                sendNow(new ServerJoinLobbyReply(false, "Username already taken."));
            }
        }
    }

    @Override
    public void handleLevelLoaded() {
        player.setLoaded(true);
        if (player.inLobby()) {
            player.lobbyIn().handlePlayerLoaded(player);
        } else {
            player.kick("Invalid packet");
        }
    }

    @Override
    public void handleVelocity(ClientVelocity velocity) {
        if (player.inLobby()) {
            player.lobbyIn().handlePlayerVelocityUpdate(player, velocity.velocityX(), velocity.velocityY(), velocity.rotation());
        } else {
            player.kick("Invalid packet");
        }
    }

    @Override
    public void handlePosition(ClientPosition position) {
        if (player.inLobby()) {
            player.lobbyIn().handlePlayerPositionUpdate(player, position.x(), position.y(), position.rotation());
        } else {
            player.kick("Invalid packet");
        }
    }

    @Override
    public void handleDisconnect() {
        close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // disconnect this player and clean up their resources.
        // log if this is not an IOError we were expecting.
        if (!(cause instanceof IOException)) {
            cause.printStackTrace();
            player.kick(cause.getMessage());
        } else {
            close();
        }
    }
}
