package server.game.entity.player;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import protocol.packet.Packet;
import protocol.packet.server.ServerDisconnect;
import server.game.entity.Entity;
import server.game.lobby.Lobby;


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
     * The lobby the player is in.
     */
    private Lobby lobbyIn;

    /**
     * The character type
     */
    protected int character;

    /**
     * Initialize
     *
     * @param entityName name
     * @param entityId   ID
     * @param character  the character
     * @param channel    the sending channel
     */
    public EntityPlayer(String entityName, int entityId, int character, Channel channel) {
        super(entityName, entityId);
        this.character = character;
        this.channel = channel;
    }

    /**
     * @return the character type
     */
    public int character() {
        return character;
    }

    /**
     * Send a packet now
     *
     * @param packet the packet
     */
    public void sendNow(Packet packet) {
        channel.writeAndFlush(packet);
    }

    /**
     * Queue a packet
     *
     * @param packet the packet
     */
    public void queue(Packet packet) {
        channel.write(packet);
    }

    /**
     * Queue a direct buffer
     *
     * @param direct buffer
     */
    public void queue(ByteBuf direct) {
        channel.write(direct);
    }

    /**
     * Flush
     */
    public void flush() {
        channel.flush();
    }

    /**
     * @return the lobby the player is in.
     */
    public Lobby lobbyIn() {
        return lobbyIn;
    }

    /**
     * @return {@code true} if this player is in a lobby
     */
    public boolean inLobby() {
        return lobbyIn != null;
    }

    /**
     * Set this player in a lobby
     *
     * @param lobbyIn the lobby
     */
    public void setInLobby(Lobby lobbyIn) {
        this.lobbyIn = lobbyIn;
    }

    /**
     * @return if this player is loaded.
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Set if this player is loaded
     *
     * @param loaded loaded
     */
    public void setLoaded(boolean loaded) {
        this.isLoaded = loaded;
    }

    /**
     * Kick this player
     *
     * @param reason the reason
     */
    public void kick(String reason) {
        sendNow(new ServerDisconnect(reason));
        disconnect();
        channel.close();
        dispose();
    }

    /**
     * Disconnect this player
     */
    public void disconnect() {
        if (lobbyIn != null) lobbyIn.removePlayerFromLobby(this);
    }

    @Override
    public void update() {

    }

    @Override
    public void dispose() {
        lobbyIn = null;
    }
}
