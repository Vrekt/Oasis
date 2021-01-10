package protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import protocol.packet.client.ClientCreateLobbyRequest;
import protocol.packet.client.ClientDisconnect;
import protocol.packet.client.ClientHandshake;
import protocol.packet.handlers.ClientPacketHandler;
import protocol.packet.handlers.ServerPacketHandler;
import protocol.packet.server.ServerCreateLobbyResponse;
import protocol.packet.server.ServerDisconnect;
import protocol.packet.server.ServerHandshakeResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * The base protocol class.
 */
public final class Protocol {

    /**
     * Protocol version
     */
    public static final int PROTOCOL_VERSION = 1;

    /**
     * A map register of all server packets
     */
    private static final Map<Integer, BiConsumer<ByteBuf, ServerPacketHandler>> SERVER_PACKETS = new HashMap<>();

    /**
     * A map register of all client packets
     */
    private static final Map<Integer, BiConsumer<ByteBuf, ClientPacketHandler>> CLIENT_PACKETS = new HashMap<>();

    /**
     * Initialize this protocol
     */
    public static void initialize() {
        initializeServer();
        initializeClient();
    }

    /**
     * Initialize server side
     */
    private static void initializeServer() {
        SERVER_PACKETS.put(ServerHandshakeResponse.PID, ServerHandshakeResponse::new);
        SERVER_PACKETS.put(ServerDisconnect.PID, ServerDisconnect::new);
        SERVER_PACKETS.put(ServerCreateLobbyResponse.PID, ServerCreateLobbyResponse::new);
    }

    /**
     * Initialize client side
     */
    private static void initializeClient() {
        CLIENT_PACKETS.put(ClientHandshake.PID, ClientHandshake::new);
        CLIENT_PACKETS.put(ClientDisconnect.PID, ClientDisconnect::new);
        CLIENT_PACKETS.put(ClientCreateLobbyRequest.PID, ClientCreateLobbyRequest::new);
    }

    /**
     * Check if the packet exists
     *
     * @param pid the packet ID
     * @return {@code true} if so
     */
    public static boolean isClientPacket(int pid) {
        return CLIENT_PACKETS.containsKey(pid);
    }

    /**
     * Handle a server packet
     *
     * @param pid     the packet ID
     * @param in      the ByteBuf in
     * @param handler the handler
     * @param context local context
     */
    public static void handleServerPacket(int pid, ByteBuf in, ServerPacketHandler handler, ChannelHandlerContext context) {
        try {
            SERVER_PACKETS.get(pid).accept(in, handler);
            in.release();
        } catch (Exception exception) {
            context.fireExceptionCaught(exception);
        }
    }

    /**
     * Handle a client packet
     *
     * @param pid     the packet ID
     * @param in      the ByteBuf in
     * @param handler the handler
     * @param context local context
     */
    public static void handleClientPacket(int pid, ByteBuf in, ClientPacketHandler handler, ChannelHandlerContext context) {
        try {
            CLIENT_PACKETS.get(pid).accept(in, handler);
            in.release();
        } catch (Exception exception) {
            context.fireExceptionCaught(exception);
        }
    }

}
