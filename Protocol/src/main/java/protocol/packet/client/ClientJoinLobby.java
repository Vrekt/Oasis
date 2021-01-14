package protocol.packet.client;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ClientPacketHandler;

/**
 * Sent by clients to join a lobby
 */
public final class ClientJoinLobby extends Packet {

    public static final int PID = 9;

    /**
     * The client username
     */
    private String username;

    /**
     * The lobby ID
     * The character
     */
    private int lobbyId, character;

    /**
     * Empty constructor
     *
     * @param username  the username
     * @param character the character
     * @param lobbyId   the lobby ID
     */
    public ClientJoinLobby(String username, int character, int lobbyId) {
        this.username = username;
        this.character = character;
        this.lobbyId = lobbyId;
    }

    /**
     * Initialize
     *
     * @param buffer  buffer
     * @param handler handler
     */
    public ClientJoinLobby(ByteBuf buffer, ClientPacketHandler handler) {
        super(buffer);
        handler.handleJoinLobby(this);
    }

    /**
     * @return the username
     */
    public String username() {
        return username;
    }

    /**
     * @return character
     */
    public int character() {
        return character;
    }

    /**
     * @return the lobby ID trying to join
     */
    public int lobbyId() {
        return lobbyId;
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
        writeInt(lobbyId);
    }

    @Override
    public void decode() {
        username = readString();
        character = readInt();
        lobbyId = readInt();
    }
}
