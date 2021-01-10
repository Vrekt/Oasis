package me.vrekt.oasis.network.session;

import com.badlogic.gdx.Gdx;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.vrekt.oasis.Oasis;
import protocol.Protocol;
import protocol.packet.client.ClientHandshake;

/**
 * A basic network session.
 */
public final class Session extends ChannelInboundHandlerAdapter {

    /**
     * The channel for this session.
     */
    private final Channel sessionChannel;

    /**
     * Initialize this session
     *
     * @param sessionChannel the channel.
     */
    public Session(Channel sessionChannel) {
        this.sessionChannel = sessionChannel;
    }

    /**
     * Handles handshaking with the server once connection is established.
     *
     * @param ctx context
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Gdx.app.log("Session", "Handshaking with server " + Oasis.GAME_VERSION + ":" + Protocol.PROTOCOL_VERSION);
        sessionChannel.writeAndFlush(new ClientHandshake(Oasis.get().thePlayer().entityName(), Oasis.GAME_VERSION, Protocol.PROTOCOL_VERSION));
    }

    /**
     * Handles removing this session
     *
     * @param ctx context
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ctx.pipeline().remove(this);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // close pipeline and session
        Gdx.app.log("Session", "Exception caught!", cause);
        ctx.pipeline().remove(this);
        sessionChannel.close();

        // alert.
        // TODO: Wow so helpful!
        //Oasis.get().screen().showMainMenuWithError("Network error", "Encountered a network error");
    }
}
