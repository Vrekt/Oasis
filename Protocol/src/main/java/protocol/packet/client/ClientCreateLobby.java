package protocol.packet.client;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ClientPacketHandler;

/**
 * Sent by clients to create a new lobby
 */
public final class ClientCreateLobby extends Packet {

    public static final int PID = 5;

    /**
     * The client username
     */
    private String username;

    /**
     * The character type
     */
    private int character;

    /**
     * Initialize
     *
     * @param username  the username
     * @param character their character
     */
    public ClientCreateLobby(String username, int character) {
        this.username = username;
        this.character = character;
    }

    /**
     * Initialize
     *
     * @param buffer  buffer
     * @param handler handler
     */
    public ClientCreateLobby(ByteBuf buffer, ClientPacketHandler handler) {
        super(buffer);
        handler.handleCreateLobby(this);
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
    }

    @Override
    public void decode() {
        username = readString();
        character = readInt();
    }
}
