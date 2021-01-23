package protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import protocol.packet.Packet;

/**
 * Encodes incoming packets then appends the length
 */
@ChannelHandler.Sharable
public final class ProtocolPacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        try {
            packet.encode();
            final int length = packet.buffer().readableBytes();
            out.writeInt(length);
            out.writeBytes(packet.buffer());
            packet.dispose();
        } catch (Exception exception) {
            ctx.fireExceptionCaught(exception);
        }
    }

}
