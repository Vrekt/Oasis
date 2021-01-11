package protocol.packet.server;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ServerPacketHandler;

/**
 * Sent by the server to have the client load a level
 */
public final class ServerLoadLevel extends Packet {

    public static final int PID = 11;

    /**
     * The level name
     */
    private String levelName;

    /**
     * Initialize
     *
     * @param levelName the level name
     */
    public ServerLoadLevel(String levelName) {
        this.levelName = levelName;
    }

    /**
     * Initialize
     *
     * @param buffer  buffer
     * @param handler handler
     */
    public ServerLoadLevel(ByteBuf buffer, ServerPacketHandler handler) {
        super(buffer);
        handler.handleLoadLevel(this);
    }

    /**
     * @return the level to load
     */
    public String levelName() {
        return levelName;
    }

    @Override
    public int pid() {
        return PID;
    }

    @Override
    public void encode() {
        createBuffer();
        writePid();
        writeString(levelName);
    }

    @Override
    public void decode() {
        levelName = readString();
    }
}
