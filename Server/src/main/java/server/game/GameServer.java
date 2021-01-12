package server.game;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import protocol.Protocol;
import protocol.codec.ProtocolPacketEncoder;
import protocol.packet.Packet;
import protocol.packet.client.*;
import protocol.packet.handlers.ClientPacketHandler;
import protocol.packet.server.ServerCreateLobbyReply;
import protocol.packet.server.ServerHandshakeReply;
import protocol.packet.server.ServerJoinLobbyReply;
import protocol.packet.server.ServerLoadLevel;
import server.game.entity.player.EntityPlayer;
import server.game.lobby.Lobby;
import server.netty.codec.ClientProtocolPacketDecoder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The game server
 */
public final class GameServer {

    /**
     * The game version
     */
    public static final int GAME_VERSION = 10;

    /**
     * Max lobbies per {@link GameServer} instance
     * {or} per dedicated server
     */
    private final int maxLobbiesPerInstance = 100;

    /**
     * Map of lobbies in this server
     */
    private final Map<Integer, Lobby> lobbies = new ConcurrentHashMap<>();

    public GameServer() {
        // tick lobbies every 35 milliseconds
        // TODO: Maybe a better way
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(this::tickLobbies, 1000, 50, TimeUnit.MILLISECONDS);
    }

    /**
     * Tick lobbies
     */
    private void tickLobbies() {
        lobbies.values().forEach(lobby -> {
            try {
                lobby.tick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Tick actual games
     */
    private void tickGames() {

    }

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
     * Assign a unique lobby ID.
     * TODO: For now 9999
     */
    private int assignUniqueLobbyId() {
        return 9999;
        // return lobbies.size() + 1 + ThreadLocalRandom.current().nextInt(123456, 999000);
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
         * Lobby in
         */
        private Lobby lobbyIn;

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
            if (packet.gameVersion() != GAME_VERSION) {
                send(new ServerHandshakeReply(false, "Game version out of date!"));
                close();

                return;
            } else if (packet.protocolVersion() != Protocol.PROTOCOL_VERSION) {
                send(new ServerHandshakeReply(false, "Protocol version out of date!"));
                close();

                return;
            }

            send(new ServerHandshakeReply(true, ""));
        }

        @Override
        public void handleCreateLobby(ClientCreateLobby createLobby) {
            if (lobbies.size() >= maxLobbiesPerInstance) {
                send(new ServerCreateLobbyReply(false, "Too many lobby instances in server."));
            } else {
                // create a new lobby and assign IDs
                final int lobbyId = assignUniqueLobbyId();
                final Lobby lobby = new Lobby(lobbyId);
                lobbies.put(lobbyId, lobby);

                lobbyIn = lobby;

                final int entityId = lobby.assignUniqueEntityId();

                // create the player and spawn them
                player = new EntityPlayer(createLobby.username(), entityId, sessionChannel);
                player.location().set(420, 544);

                lobby.spawnPlayerInLobby(player);

                // send them reply
                send(new ServerLoadLevel("PreLobby"));
                send(new ServerCreateLobbyReply(lobbyId, entityId));
            }
        }

        @Override
        public void handleJoinLobby(ClientJoinLobby joinLobby) {
            final int lobbyId = joinLobby.lobbyId();
            final Lobby lobby = lobbies.get(lobbyId);
            if (lobby == null) {
                send(new ServerJoinLobbyReply(false, "Lobby does not exist."));
            } else {
                lobbyIn = lobby;

                // send they joined the lobby
                final int entityId = lobby.assignUniqueEntityId();
                send(new ServerLoadLevel("PreLobby"));
                send(new ServerJoinLobbyReply(lobbyId, entityId));

                // initialize a new player and spawn them.
                player = new EntityPlayer(joinLobby.username(), entityId, sessionChannel);
                player.location().set(420, 544);

                lobby.spawnPlayerInLobby(player);
            }
        }

        @Override
        public void handleVelocity(ClientVelocity velocity) {
            if (lobbyIn != null)
                lobbyIn.onPlayerVelocity(player, velocity.velocityX(), velocity.velocityY(), velocity.rotationIndex());
        }

        @Override
        public void handleLevelLoaded() {
            if (lobbyIn != null) lobbyIn.onPlayerLoaded(player);
        }

        @Override
        public void handlePosition(ClientPosition position) {
            if (lobbyIn != null) lobbyIn.onPlayerPosition(player, position.x(), position.y(), position.rotation());
        }

        @Override
        public void handleDisconnect() {
            if (player != null) {
                if (lobbyIn != null) {
                    lobbyIn.removePlayer(player.entityId());
                    lobbyIn = null;
                }
            }

            player = null;
            close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            if (!(cause instanceof IOException)) {
                cause.printStackTrace();
            }

            handleDisconnect();
        }

        /**
         * Send a packet
         *
         * @param packet the packet
         */
        private void send(Packet packet) {
            sessionChannel.writeAndFlush(packet);
        }

        /**
         * Close
         */
        private void close() {
            sessionChannel.pipeline().remove(ClientProtocolPacketDecoder.class);
            sessionChannel.pipeline().remove(ProtocolPacketEncoder.class);
            sessionChannel.pipeline().remove(this);
            sessionChannel.close();
        }

    }

}
