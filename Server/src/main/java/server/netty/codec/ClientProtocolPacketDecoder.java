package server.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import protocol.Protocol;
import protocol.packet.handlers.ClientPacketHandler;

/**
 * Handles decoding packets sent from clients
 */
public final class ClientProtocolPacketDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * The local session packet handler
     */
    private final ClientPacketHandler handler;

    public ClientProtocolPacketDecoder(ClientPacketHandler handler) {
        super(Integer.MAX_VALUE, 0, 4);
        this.handler = handler;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) {
        try {
            final ByteBuf buf = (ByteBuf) super.decode(ctx, in);
            if (buf != null) {
                // ignore the length of the packet.
                buf.readInt();
                // retrieve packet from PID
                final int pid = buf.readByte() & 0xFF;
                if (Protocol.isClientPacket(pid)) Protocol.handleClientPacket(pid, buf, handler, ctx);

                buf.release();
            }
        } catch (Exception any) {
            ctx.fireExceptionCaught(any);
        }
        return null;
    }
}
