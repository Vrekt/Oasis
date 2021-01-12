package server.game.entity.player;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import protocol.packet.Packet;
import server.game.entity.Entity;


/**
 * Represents a player entity
 */
public final class EntityPlayer extends Entity {

    /**
     * Sending channel
     */
    private final Channel channel;

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
    public EntityPlayer(String entityName, int entityId, Channel channel) {
        super(entityName, entityId);
        this.channel = channel;
    }

    /**
     * Send a packet
     *
     * @param packet the packet
     */
    public void send(Packet packet) {
        channel.writeAndFlush(packet);
    }

    /**
     * Send a direct buffer
     *
     * @param direct direct
     */
    public void queue(ByteBuf direct) {
       channel.writeAndFlush(direct);
    }

    public void flush() {
     //   channel.flush();
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
