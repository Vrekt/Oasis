package protocol.packet.server;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ServerPacketHandler;

/**
 * Sent in response to {@link protocol.packet.client.ClientJoinLobby}
 */
public final class ServerJoinLobbyReply extends Packet {

    public static final int PID = 10;

    /**
     * Lobby ID
     * entity ID
     */
    private int lobbyId, entityId;

    /**
     * If allowed
     */
    private boolean allowed;

    /**
     * Not allowed reason
     */
    private String notAllowedReason;

    /**
     * Initialize
     *
     * @param lobbyId  the lobby ID
     * @param entityId the entity ID
     */
    public ServerJoinLobbyReply(int lobbyId, int entityId) {
        this.lobbyId = lobbyId;
        this.entityId = entityId;
        this.allowed = true;
        this.notAllowedReason = "";
    }

    /**
     * Initialize
     *
     * @param allowed          if allowed
     * @param notAllowedReason the reason
     */
    public ServerJoinLobbyReply(boolean allowed, String notAllowedReason) {
        this.allowed = allowed;
        this.notAllowedReason = notAllowedReason;
    }

    /**
     * Initialize
     *
     * @param buffer  buffer
     * @param handler handler
     */
    public ServerJoinLobbyReply(ByteBuf buffer, ServerPacketHandler handler) {
        super(buffer);
        handler.handleJoinLobbyReply(this);
    }

    /**
     * @return lobby ID
     */
    public int lobbyId() {
        return lobbyId;
    }

    /**
     * @return entity ID
     */
    public int entityId() {
        return entityId;
    }

    /**
     * @return if allowed
     */
    public boolean allowed() {
        return allowed;
    }

    /**
     * @return the reason
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
        writeInt(entityId);
        writeBoolean(allowed);
        writeString(notAllowedReason);
    }

    @Override
    public void decode() {
        lobbyId = readInt();
        entityId = readInt();
        allowed = readBoolean();
        notAllowedReason = readString();
    }
}
