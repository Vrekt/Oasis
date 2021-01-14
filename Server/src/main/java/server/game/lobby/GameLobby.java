package server.game.lobby;

import com.google.common.flogger.FluentLogger;
import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.server.ServerCreatePlayer;
import protocol.packet.server.ServerPlayerPosition;
import protocol.packet.server.ServerPlayerVelocity;
import protocol.packet.server.ServerRemovePlayer;
import server.game.Server;
import server.game.entity.packet.QueuedPlayerPacketUpdate;
import server.game.entity.player.EntityPlayer;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a game lobby
 */
final class GameLobby implements Lobby {

    /**
     * Logging
     */
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

    /**
     * Max packets to process per tick;
     */
    private static final int MAX_PACKETS_PER_TICK = 50;

    /**
     * The players in this lobby
     */
    private final Map<Integer, EntityPlayer> players = new ConcurrentHashMap<>();

    /**
     * Queued player updates
     */
    private final Queue<QueuedPlayerPacketUpdate> queuedPlayerUpdates = new ConcurrentLinkedQueue<>();

    /**
     * The lobby ID.
     */
    private final int lobbyId;

    /**
     * Initialize
     *
     * @param lobbyId the lobby ID
     */
    GameLobby(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    @Override
    public int lobbyId() {
        return lobbyId;
    }

    @Override
    public int getNewEntityId() {
        return players.size() + 1 + ThreadLocalRandom.current().nextInt(111, 999);
    }

    @Override
    public boolean canJoinLobby(String username) {
        return players.values().stream().noneMatch(player -> player.entityName().equalsIgnoreCase(username));
    }

    @Override
    public void spawnPlayerInLobby(EntityPlayer player) {
        if (!players.isEmpty()) {
            final ByteBuf packet = Packet.encodeDirect(new ServerCreatePlayer(player.entityName(), player.entityId(), player.x(), player.y()));
            broadcast(packet);
        }

        players.put(player.entityId(), player);
    }

    @Override
    public void removePlayerFromLobby(EntityPlayer player) {
        players.remove(player.entityId());
        // TODO: Client issues?
        player.flush();

        if (players.size() == 0) {
            // destroy this lobby.
            // TODO: Destroy lobby in other means too.
            Server.getServer().removeLobby(this);
        }
        final ByteBuf packet = Packet.encodeDirect(new ServerRemovePlayer(player.entityId()));
        broadcast(packet);
    }

    @Override
    public void handlePlayerLoaded(EntityPlayer player) {
        players.forEach((entityId, existing) -> {
            if (existing.entityId() != player.entityId()) {
                player.queue(new ServerCreatePlayer(existing.entityName(), existing.entityId(), existing.x(), existing.y()));
            }
        });
    }

    @Override
    public void handlePlayerVelocityUpdate(EntityPlayer player, float velocityX, float velocityY, int rotation) {
        queuedPlayerUpdates.add(new QueuedPlayerPacketUpdate(player.entityId(), Packet.encodeDirect(new ServerPlayerVelocity(player.entityId(), velocityX, velocityY, rotation))));
    }

    @Override
    public void handlePlayerPositionUpdate(EntityPlayer player, float x, float y, int rotation) {
        player.setLocation(x, y);

        queuedPlayerUpdates.add(new QueuedPlayerPacketUpdate(player.entityId(), Packet.encodeDirect(new ServerPlayerPosition(player.entityId(), rotation, x, y))));
    }

    @Override
    public void tick() {
        int packets = 0;
        // flush queued player packets
        players.forEach((i, p) -> p.flush());

        while (queuedPlayerUpdates.peek() != null) {
            final QueuedPlayerPacketUpdate update = queuedPlayerUpdates.poll();
            broadcast(update.from(), update.direct());
            packets++;

            if (packets >= MAX_PACKETS_PER_TICK) {
                LOGGER.atInfo().log("Reached max packets to process per tick, %s", packets);
                break;
            }
        }

        if (packets > 0) LOGGER.atInfo().every(50).log("Wrote %s packets", packets);
    }

    @Override
    public void dispose() {
        players.clear();
        queuedPlayerUpdates.clear();
    }

    /**
     * Broadcast a packet to all players
     *
     * @param entityIdExcluded the entity ID to exclude
     * @param direct           the direct
     */
    private void broadcast(int entityIdExcluded, ByteBuf direct) {
        players.forEach((entityId, player) -> {
            if (player.isLoaded() && player.entityId() != entityIdExcluded) player.queue(direct.retain().duplicate());
        });

        direct.release();
    }

    /**
     * Broadcast a packet to all players
     *
     * @param direct the direct
     */
    private void broadcast(ByteBuf direct) {
        players.forEach((entityId, player) -> {
            if (player.isLoaded()) player.queue(direct.retain().duplicate());
        });

        direct.release();
    }
}
