package server.game.entity.packet;

import io.netty.buffer.ByteBuf;

/**
 * A queued player update.
 */
public final class QueuedPlayerPacketUpdate {

    /**
     * The entity ID from.
     */
    private final int from;

    /**
     * Direct send buffer.
     */
    private final ByteBuf direct;

    /**
     * Initialize
     *
     * @param from   who its from
     * @param direct the direct
     */
    public QueuedPlayerPacketUpdate(int from, ByteBuf direct) {
        this.from = from;
        this.direct = direct;
    }

    /**
     * @return from
     */
    public int from() {
        return from;
    }

    /**
     * @return direct
     */
    public ByteBuf direct() {
        return direct;
    }

    /**
     * Release the update.
     */
    public void release() {
        direct.release();
    }

}
