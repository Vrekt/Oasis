package server.game.entity.player;

import protocol.packet.Packet;
import server.game.entity.Entity;

import java.util.function.Consumer;

/**
 * Represents a player entity
 */
public final class EntityPlayer extends Entity {

    /**
     * Handles sending packets.
     */
    private final Consumer<Packet> packetHandler;

    /**
     * If the player is loaded.
     */
    private boolean isLoaded;

    /**
     * Initialize
     *
     * @param entityName name
     * @param entityId   ID
     */
    public EntityPlayer(String entityName, int entityId, Consumer<Packet> packetHandler) {
        super(entityName, entityId);
        this.packetHandler = packetHandler;
    }

    /**
     * Send a packet
     *
     * @param packet the packet
     */
    public void send(Packet packet) {
        packetHandler.accept(packet);
    }

    /**
     * @return if the player is loaded
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * @param loaded set loaded
     */
    public void isLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    @Override
    public void update() {

    }

    @Override
    public void dispose() {

    }
}
