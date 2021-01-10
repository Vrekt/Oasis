package protocol.packet.server;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ServerPacketHandler;

/**
 * Sent by the server in response to {@link protocol.packet.client.ClientCreateLobbyRequest}
 */
public final class ServerCreateLobbyResponse extends Packet<ServerPacketHandler> {

    /**
     * PID
     */
    public static final int PID = 6;

    /**
     * The lobby ID.
     */
    private int lobbyId = 0;

    /**
     * If the request is allowed;
     */
    private boolean allowed;

    /**
     * The not allowed reason
     */
    private String notAllowedReason;

    /**
     * Initialize
     *
     * @param allowed          if its allowed
     * @param notAllowedReason the reason if not
     */
    public ServerCreateLobbyResponse(boolean allowed, String notAllowedReason) {
        this.allowed = allowed;
        this.notAllowedReason = notAllowedReason;
    }

    /**
     * Initialize
     *
     * @param lobbyId the lobby ID
     */
    public ServerCreateLobbyResponse(int lobbyId) {
        this(true, "");
        this.lobbyId = lobbyId;
    }

    /**
     * Initialize
     *
     * @param buffer  buffer
     * @param handler handler
     */
    public ServerCreateLobbyResponse(ByteBuf buffer, ServerPacketHandler handler) {
        super(buffer);
        handler.handleCreateLobbyResponse(this);
    }

    /**
     * @return the lobby ID.
     */
    public int lobbyId() {
        return lobbyId;
    }

    /**
     * @return if the request is allowed
     */
    public boolean allowed() {
        return allowed;
    }

    /**
     * @return the not allowed reason.
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
        writeInt(lobbyId);
        writeBoolean(allowed);
        writeString(notAllowedReason);
    }

    @Override
    public void decode() {
        lobbyId = readInt();
        allowed = readBoolean();
        notAllowedReason = readString();
    }
}
