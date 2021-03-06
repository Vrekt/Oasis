package me.vrekt.stresstest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import protocol.Protocol;

/**
 * Handles decoding local server packets
 */
public final class ServerProtocolPacketDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * The handler
     */
    private final DefaultServerPacketHandler handler;

    /**
     * Initialize this local decoder
     *
     * @param handler the handler
     */
    public ServerProtocolPacketDecoder(DefaultServerPacketHandler handler) {
        super(Integer.MAX_VALUE, 0, 4);
        this.handler = handler;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        final ByteBuf buf = (ByteBuf) super.decode(ctx, in);
        if (buf != null) {
            // ignore the length of the packet.
            buf.readInt();
            // retrieve packet from PID
            final int pid = buf.readByte() & 0xFF;
            Protocol.handleServerPacket(pid, buf, handler, ctx);

            buf.release();
        }
        return null;
    }
}

