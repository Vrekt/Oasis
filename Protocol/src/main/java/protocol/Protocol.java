package protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import protocol.packet.client.*;
import protocol.packet.handlers.ClientPacketHandler;
import protocol.packet.handlers.ServerPacketHandler;
import protocol.packet.server.*;

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
        SERVER_PACKETS.put(ServerHandshakeReply.PID, ServerHandshakeReply::new);
        SERVER_PACKETS.put(ServerDisconnect.PID, ServerDisconnect::new);
        SERVER_PACKETS.put(ServerCreateLobbyReply.PID, ServerCreateLobbyReply::new);
        SERVER_PACKETS.put(ServerCreatePlayer.PID, ServerCreatePlayer::new);
        SERVER_PACKETS.put(ServerRemovePlayer.PID, ServerRemovePlayer::new);
        SERVER_PACKETS.put(ServerJoinLobbyReply.PID, ServerJoinLobbyReply::new);
        SERVER_PACKETS.put(ServerLoadLevel.PID, ServerLoadLevel::new);
        SERVER_PACKETS.put(ServerPlayerVelocity.PID, ServerPlayerVelocity::new);
        SERVER_PACKETS.put(ServerPlayerPosition.PID, ServerPlayerPosition::new);
    }

    /**
     * Initialize client side
     */
    private static void initializeClient() {
        CLIENT_PACKETS.put(ClientHandshake.PID, ClientHandshake::new);
        CLIENT_PACKETS.put(ClientDisconnect.PID, ClientDisconnect::new);
        CLIENT_PACKETS.put(ClientCreateLobby.PID, ClientCreateLobby::new);
        CLIENT_PACKETS.put(ClientJoinLobby.PID, ClientJoinLobby::new);
        CLIENT_PACKETS.put(ClientLevelLoaded.PID, ClientLevelLoaded::new);
        CLIENT_PACKETS.put(ClientVelocity.PID, ClientVelocity::new);
        CLIENT_PACKETS.put(ClientPosition.PID, ClientPosition::new);
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
        } catch (Exception exception) {
            context.fireExceptionCaught(exception);
        }
    }

}
