package protocol.packet.client;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ClientPacketHandler;

/**
 * Basic client handshake packet
 */
public final class ClientHandshake extends Packet {

    /**
     * PID
     */
    public static final int PID = 1;

    /**
     * Versioning
     */
    private int gameVersion, protocolVersion;

    /**
     * Initialize
     *
     * @param gameVersion     the game version
     * @param protocolVersion the protocol version
     */
    public ClientHandshake(int gameVersion, int protocolVersion) {
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
        writeInt(gameVersion);
        writeInt(protocolVersion);
    }

    @Override
    public void decode() {
        gameVersion = readInt();
        protocolVersion = readInt();
    }
}
