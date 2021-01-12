package server.game.lobby;

import protocol.packet.server.ServerCreatePlayer;
import protocol.packet.server.ServerPlayerPosition;
import protocol.packet.server.ServerPlayerVelocity;
import protocol.packet.server.ServerRemovePlayer;
import server.game.entity.packet.QueuedPlayerPacketUpdate;
import server.game.entity.player.EntityPlayer;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a game lobby.
 */
public final class Lobby {

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
    public Lobby(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    /**
     * @return the lobby ID
     */
    public int lobbyId() {
        return lobbyId;
    }

    /**
     * Create a new unique entity ID
     */
    public int assignUniqueEntityId() {
        return players.size() + 1 + ThreadLocalRandom.current().nextInt(111, 999);
    }

    /**
     * Spawn a player in the lobby
     * TODO: Send direct without encoding the packet each time.
     * TODO: For now 420, 544
     * TODO: Bulk entity send?
     *
     * @param player the player
     */
    public void spawnPlayerInLobby(EntityPlayer player) {
        if (players.size() > 0) {
            // create the packet
            // iterate through all existing players and send them the new player,
            final ServerCreatePlayer packetPlayer = new ServerCreatePlayer(player.entityName(), player.entityId(), player.x(), player.y());
            players.forEach((id, existingPlayer) -> existingPlayer.send(packetPlayer));
        }

        players.put(player.entityId(), player);
    }

    /**
     * Remove a player
     *
     * @param entityId the entity ID
     */
    public void removePlayer(int entityId) {
        players.remove(entityId);

        final ServerRemovePlayer packet = new ServerRemovePlayer(entityId);
        players.forEach((id, player) -> player.send(packet));
    }

    /**
     * Invoked when the player loads in.
     *
     * @param player the player
     */
    public void onPlayerLoaded(EntityPlayer player) {
        player.isLoaded(true);

        players.values().forEach(existingPlayer -> {
            if (existingPlayer.entityId() != player.entityId()) {
                player.send(new ServerCreatePlayer(existingPlayer.entityName(), existingPlayer.entityId(), existingPlayer.x(), existingPlayer.y()));
            }
        });
    }

    /**
     * Invoked when a velocity is received
     *
     * @param player    the player
     * @param velocityX the X velocity
     * @param velocityY the Y velocity
     * @param rotation  the rotation
     */
    public void onPlayerVelocity(EntityPlayer player, float velocityX, float velocityY, int rotation) {
        queuedPlayerUpdates.add(new QueuedPlayerPacketUpdate(player.entityId(), ServerPlayerVelocity.encodeDirect(player.entityId(), velocityX, velocityY, rotation)));
    }

    /**
     * Invoked when position is received
     *
     * @param player   player
     * @param x        x
     * @param y        y
     * @param rotation rotation
     */
    public void onPlayerPosition(EntityPlayer player, float x, float y, int rotation) {
        player.location().set(x, y);
        queuedPlayerUpdates.add(new QueuedPlayerPacketUpdate(player.entityId(), ServerPlayerPosition.encodeDirect(player.entityId(), rotation, x, y)));
    }

    /**
     * Tick this lobby
     */
    public void tick() {
        while (queuedPlayerUpdates.peek() != null) {
            final QueuedPlayerPacketUpdate update = queuedPlayerUpdates.poll();
            broadcastUpdate(update);
        }
    }

    /**
     * Broadcast update packet
     * Players will miss updates while loading in.
     *
     * @param update the update packet
     */
    public void broadcastUpdate(QueuedPlayerPacketUpdate update) {
        players.forEach((entityId, player) -> {
            if (entityId != update.from() && player.isLoaded()) player.queue(update.direct());
        });
        // update.release(); causes problems
    }
}
