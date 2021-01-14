package protocol.packet.server;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ServerPacketHandler;

/**
 * A response to {@link protocol.packet.client.ClientHandshake}
 */
public final class ServerHandshakeReply extends Packet {

    /**
     * PID
     */
    public static final int PID = 2;

    /**
     * The entity ID
     */
    private boolean allowed;

    /**
     * The not allowed reason.
     */
    private String notAllowedReason;

    /**
     * Initialize
     *
     * @param allowed          if the client is allowed to join the server
     * @param notAllowedReason the reason if not
     */
    public ServerHandshakeReply(boolean allowed, String notAllowedReason) {
        this.allowed = allowed;
        this.notAllowedReason = notAllowedReason;
    }

    /**
     * Initialize
     *
     * @param allowed          if the client is allowed to join the server
     */
    public ServerHandshakeReply(boolean allowed) {
        this(allowed, "");
    }

    /**
     * Initialize
     *
     * @param buffer  buffer
     * @param handler handler
     */
    public ServerHandshakeReply(ByteBuf buffer, ServerPacketHandler handler) {
        super(buffer);
        handler.handleHandshakeReply(this);
    }

    /**
     * @return if allowed
     */
    public boolean allowed() {
        return allowed;
    }

    /**
     * @return the not allowed reason
     */
    public String notAllowedReason() {
        return notAllowedReason;
    }

    @Override
    public int pid() {
        return PID;
    }

    @Override
    public void encode() {
        createBuffer();
        writePid();
        writeBoolean(allowed);
        writeString(notAllowedReason);
    }

    @Override
    public void decode() {
        allowed = readBoolean();
        notAllowedReason = readString();
    }
}
