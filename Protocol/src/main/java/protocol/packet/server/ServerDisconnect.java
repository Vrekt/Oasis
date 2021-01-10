package protocol.packet.server;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ServerPacketHandler;

/**
 * Sent by the server to disconnect a client
 */
public final class ServerDisconnect extends Packet<ServerPacketHandler> {

    /**
     * PID
     */
    public static final int PID = 3;

    /**
     * The reason for the disconnect
     */
    private String reason;

    /**
     * Initialize
     *
     * @param reason the reason
     */
    public ServerDisconnect(String reason) {
        this.reason = reason;
    }

    /**
     * Initialize
     *
     * @param buffer  buffer
     * @param handler handler
     */
    public ServerDisconnect(ByteBuf buffer, ServerPacketHandler handler) {
        super(buffer);
        handler.handleDisconnect(this);
    }

    /**
     * @return the reason
     */
    public String reason() {
        return reason;
    }

    @Override
    public int pid() {
        return PID;
    }

    @Override
    public void encode() {
        createBuffer();
        writePid();
        writeString(reason);
    }

    @Override
    public void decode() {
        reason = readString();
    }
}
