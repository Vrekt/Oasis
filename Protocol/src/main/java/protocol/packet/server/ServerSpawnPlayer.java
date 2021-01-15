package protocol.packet.server;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ServerPacketHandler;

/**
 * Like {@link ServerCreatePlayer} but instead local clients handle where to set this players position
 */
public final class ServerSpawnPlayer extends Packet {

    public static final int PID = 16;

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
     * Initialize
     *
     * @param username  username
     * @param character character
     * @param entityId  ID
     */
    public ServerSpawnPlayer(String username, int character, int entityId) {
        this.username = username;
        this.character = character;
        this.entityId = entityId;
    }

    /**
     * Initialize
     *
     * @param buffer  buffer
     * @param handler handler
     */
    public ServerSpawnPlayer(ByteBuf buffer, ServerPacketHandler handler) {
        super(buffer);
        handler.handleSpawnPlayer(this);
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
    }

    @Override
    public void decode() {
        username = readString();
        character = readInt();
        entityId = readInt();
    }

}
