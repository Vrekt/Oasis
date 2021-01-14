package protocol.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * A protocol packet.
 */
public abstract class Packet {

    /**
     * The buffer for this packet
     */
    protected ByteBuf buffer;

    /**
     * Encode a packet
     *
     * @param packet the packet
     * @return the byte buf
     */
    public static ByteBuf encodeDirect(Packet packet) {
        packet.encode();

        final int length = packet.buffer.readableBytes();
        final ByteBuf direct = Unpooled.buffer();
        direct.writeInt(length);
        direct.writeBytes(packet.buffer);
        packet.dispose();
        return direct;
    }

    public Packet(ByteBuf buffer) {
        this.buffer = buffer;
        decode();
    }

    public Packet() {
    }

    /**
     * @return the packet ID.
     */
    public abstract int pid();

    /**
     * Encode this packet
     */
    public abstract void encode();

    /**
     * Decode this packet.
     */
    public void decode() {

    }

    /**
     * Create the internal buffer
     */
    protected void createBuffer() {
        buffer = Unpooled.buffer();
    }

    /**
     * @return retrieve the buffer
     */
    public ByteBuf buffer() {
        return buffer;
    }

    /**
     * Dispose this packet
     */
    public void dispose() {
        buffer.release();
        buffer = null;
    }

    /**
     * Read bytes
     *
     * @param length the length
     * @return the bytes
     */
    protected byte[] readBytes(int length) {
        final byte[] contents = new byte[length];
        buffer.readBytes(contents, 0, length);
        return contents;
    }

    /**
     * Read a string
     *
     * @return the string
     */
    protected String readString() {
        final int length = buffer.readInt();
        final byte[] contents = readBytes(length);
        return new String(contents, StandardCharsets.UTF_8);
    }

    /**
     * Read an int
     *
     * @return the int
     */
    protected int readInt() {
        return buffer.readInt();
    }

    /**
     * Write a string.
     *
     * @param value the value
     */
    protected void writeString(String value) {
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
    }

    /**
     * Write PID
     */
    protected void writePid() {
        buffer.writeByte(pid());
    }

    /**
     * Write int
     *
     * @param i i
     */
    protected void writeInt(int i) {
        buffer.writeInt(i);
    }

    /**
     * write a double
     *
     * @param d d
     */
    protected void writeDouble(double d) {
        buffer.writeDouble(d);
    }

    /**
     * write a boolean
     *
     * @param b b
     */
    protected void writeBoolean(boolean b) {
        buffer.writeBoolean(b);
    }

    /**
     * Read a double
     *
     * @return double
     */
    protected double readDouble() {
        return buffer.readDouble();
    }

    /**
     * Write a float
     *
     * @param f float
     */
    protected void writeFloat(float f) {
        buffer.writeFloat(f);
    }

    /**
     * @return the float
     */
    protected float readFloat() {
        return buffer.readFloat();
    }

    /**
     * Read a boolean
     *
     * @return the bool
     */
    protected boolean readBoolean() {
        return buffer.readBoolean();
    }

}
