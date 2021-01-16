package me.vrekt.oasis.network.states;

/**
 * A connection state
 */
public enum ConnectionState {

    /**
     * When the first socket connection is made
     */
    CONNECTED,
    /**
     * When the client is handshaking
     */
    HANDSHAKING,
    /**
     * When the server accepts the client
     */
    AUTHENTICATED

}
