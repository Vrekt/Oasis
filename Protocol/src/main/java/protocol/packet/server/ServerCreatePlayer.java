package protocol.packet.server;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ServerPacketHandler;

/**
 * Sent by the server to create a player client side
 */
public final class ServerCreatePlayer extends Packet {

    public static final int PID = 7;

    /**
     * Username
     */
    private String username;

    /**
     * Entity ID
     * Character
     */
    private int entityId, character;

    /**
     * Spawning location
     */
    private float x, y;

    /**
     * Initialize
     *
     * @param username  username
     * @param character character
     * @param entityId  ID
     * @param x         starting X
     * @param y         starting Y
     */
    public ServerCreatePlayer(String username, int character, int entityId, float x, float y) {
        this.username = username;
        this.character = character;
        this.entityId = entityId;
        this.x = x;
        this.y = y;
    }

    /**
     * Initialize
     *
     * @param buffer  buffer
     * @param handler handler
     */
    public ServerCreatePlayer(ByteBuf buffer, ServerPacketHandler handler) {
        super(buffer);
        handler.handleCreatePlayer(this);
    }

    /**
     * @return the username
     */
    public String username() {
        return username;
    }

    /**
     * @return the character
     */
    public int character() {
        return character;
    }

    /**
     * @return ID
     */
    public int entityId() {
        return entityId;
    }

    /**
     * @return X
     */
    public float x() {
        return x;
    }

    /**
     * @return Y
     */
    public float y() {
        return y;
    }

    @Override
    public int pid() {
        return PID;
    }

    @Override
    public void encode() {
        createBuffer();
        writePid();
        writeString(username);
        writeInt(character);
        writeInt(entityId);
        writeFloat(x);
        writeFloat(y);
    }

    @Override
    public void decode() {
        username = readString();
        character = readInt();
        entityId = readInt();
        x = readFloat();
        y = readFloat();
    }
}
