package server.game.lobby;

import protocol.packet.Packet;
import protocol.packet.server.ServerCreatePlayer;
import protocol.packet.server.ServerPlayerVelocity;
import protocol.packet.server.ServerRemovePlayer;
import server.game.entity.player.EntityPlayer;
import server.game.entity.velocity.QueuedVelocityUpdate;

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
     * Velocity updates
     */
    private final Queue<QueuedVelocityUpdate> queuedVelocityUpdates = new ConcurrentLinkedQueue<>();

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
        queuedVelocityUpdates.add(new QueuedVelocityUpdate(player.entityId(), velocityX, velocityY, rotation));
    }

    /**
     * Tick this lobby
     */
    public void tick() {
        while (queuedVelocityUpdates.peek() != null) {
            final QueuedVelocityUpdate update = queuedVelocityUpdates.poll();
            broadcast(update.entityId(), new ServerPlayerVelocity(update.entityId(), update.velocityX(), update.velocityY(), update.rotationIndex()));
        }
    }

    /**
     * Broadcast
     *
     * @param exceptFor exception
     * @param packet    the packet
     */
    public void broadcast(int exceptFor, Packet packet) {
        players.forEach((i, player) -> {
            if (i != exceptFor && player.isLoaded()) player.send(packet);
        });
    }

}
