package server.game;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.ThreadLocalRandom;
import protocol.Protocol;
import protocol.packet.Packet;
import protocol.packet.client.ClientCreateLobbyRequest;
import protocol.packet.client.ClientHandshake;
import protocol.packet.handlers.ClientPacketHandler;
import protocol.packet.server.ServerCreateLobbyResponse;
import protocol.packet.server.ServerDisconnect;
import protocol.packet.server.ServerHandshakeResponse;
import server.game.entity.player.EntityPlayer;
import server.game.level.Level;
import server.game.lobby.impl.Lobby;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The game server
 */
public final class GameServer {

    /**
     * The game version
     */
    public static final int GAME_VERSION = 10;

    /**
     * Amount to split the level instances by for multi threading.
     */
    private final int threadLevelChunkingAmount = 50;

    /**
     * Max dungeons allowed for this server.
     */
    private final int maxLevelInstancesAllowed = 100;

    /**
     * Max lobby instances allowed for this server.
     */
    private final int maxLobbyInstancesAllowed = 100;

    /**
     * A map of all active levels
     */
    private final Map<Integer, Level> levels = new ConcurrentHashMap<>();

    /**
     * A map of all lobbies
     */
    private final Map<Integer, Lobby> lobbies = new ConcurrentHashMap<>();

    /**
     * Create a new session
     *
     * @param sessionChannel the channel for the session
     * @return the session
     */
    public LocalGameServerSession createSession(Channel sessionChannel) {
        return new LocalGameServerSession(sessionChannel);
    }

    /**
     * Assign a hopefully unique entity ID.
     *
     * @return the unique entity ID.
     */
    public int assignUniqueEntityId() {
        final int modifier = (levels.size() + 1) + ThreadLocalRandom.current().nextInt(100, 900);
        return ThreadLocalRandom.current().nextInt(123456, 999999) + modifier;
    }

    /**
     * Assign a hopefully unique lobby ID.
     *
     * @return the unique lobby ID.
     */
    public int assignUniqueLobbyId() {
        final int modifier = (lobbies.size() + 1) + ThreadLocalRandom.current().nextInt(100, 900);
        return ThreadLocalRandom.current().nextInt(123456, 900000) + modifier;
    }

    /**
     * Represents a local game server session
     */
    public final class LocalGameServerSession extends ChannelInboundHandlerAdapter implements ClientPacketHandler {

        /**
         * Send channel
         */
        private final Channel sessionChannel;

        /**
         * The player for this session
         */
        private EntityPlayer player;

        /**
         * Initialize
         *
         * @param sessionChannel the session channel.
         */
        public LocalGameServerSession(Channel sessionChannel) {
            this.sessionChannel = sessionChannel;
        }

        @Override
        public void handleHandshake(ClientHandshake packet) {
            // TODO: Some kind of username validation.
            if (packet.gameVersion() != GAME_VERSION) {
                sessionChannel.writeAndFlush(new ServerDisconnect("Game version out of date!"));
                handleDisconnect();
            } else if (packet.protocolVersion() != Protocol.PROTOCOL_VERSION) {
                sessionChannel.writeAndFlush(new ServerDisconnect("Protocol version out of date!"));
                handleDisconnect();
            }

            final int entityId = assignUniqueEntityId();
            player = new EntityPlayer(packet.username(), entityId, this::send);
            sessionChannel.writeAndFlush(new ServerHandshakeResponse(entityId));
        }

        @Override
        public void handleCreateLobbyRequest(ClientCreateLobbyRequest request) {
            if (lobbies.size() > maxLobbyInstancesAllowed) {
                // TODO: Just for now, obviously in the future
                // TODO: We want to re-reroute to a less full server or something
                sessionChannel.writeAndFlush(new ServerCreateLobbyResponse(false, "Too many lobbies in server."));
            } else {
                // assign a new lobby.
                final int lobbyId = assignUniqueLobbyId();
                final Lobby lobby = Lobby.create(lobbyId);
                lobbies.put(lobbyId, lobby);

                // send them the new lobby.
                sessionChannel.writeAndFlush(new ServerCreateLobbyResponse(lobbyId));
            }
        }

        @Override
        public void handleDisconnect() {
            // close and remove.
            if (player != null) {
                // TODO: Dispose
            }

            sessionChannel.pipeline().remove(this);
            sessionChannel.close();
        }

        /**
         * Send a packet
         *
         * @param packet the packet
         */
        private void send(Packet<?> packet) {
            sessionChannel.writeAndFlush(packet);
        }

    }

}
