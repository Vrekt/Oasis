package protocol.packet.client;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ClientPacketHandler;

/**
 * Basic client handshake packet
 */
public final class ClientHandshake extends Packet<ClientPacketHandler> {

    /**
     * PID
     */
    public static final int PID = 1;

    /**
     * Username
     */
    private String username;

    /**
     * Versioning
     */
    private int gameVersion, protocolVersion;

    /**
     * Initialize
     *
     * @param username        the username
     * @param gameVersion     the game version
     * @param protocolVersion the protocol version
     */
    public ClientHandshake(String username, int gameVersion, int protocolVersion) {
        this.username = username;
        this.gameVersion = gameVersion;
        this.protocolVersion = protocolVersion;
    }

    /**
     * Initialize
     *
     * @param buffer  buffer
     * @param handler handler
     */
    public ClientHandshake(ByteBuf buffer, ClientPacketHandler handler) {
        super(buffer);
        handler.handleHandshake(this);
    }

    /**
     * @return the client username
     */
    public String username() {
        return username;
    }

    /**
     * @return the client version
     */
    public int gameVersion() {
        return gameVersion;
    }

    /**
     * @return the protocol version
     */
    public int protocolVersion() {
        return protocolVersion;
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
        writeInt(gameVersion);
        writeInt(protocolVersion);
    }

    @Override
    public void decode() {
        username = readString();
        gameVersion = readInt();
        protocolVersion = readInt();
    }
}
