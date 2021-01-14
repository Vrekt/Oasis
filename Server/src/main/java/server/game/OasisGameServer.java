package server.game;

import com.google.common.flogger.FluentLogger;
import server.game.lobby.Lobby;

import java.util.Map;
import java.util.concurrent.*;

/**
 * The game server
 */
final class OasisGameServer implements Server {

    /**
     * This server
     */
    private static final Server SERVER = new OasisGameServer();

    /**
     * Logging
     */
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

    /**
     * Max lobbies per {@link OasisGameServer} instance
     * {or} per dedicated server
     */
    private final int maxLobbiesPerInstance = 100;

    /**
     * Amount of lobbies per thread
     */
    private final int lobbiesPerThread = 25;

    /**
     * Map of lobbies in this server
     */
    private final Map<Integer, Lobby> lobbies = new ConcurrentHashMap<>();

    /**
     * Lobby ticking
     * TODO: In future skipping ticks when behind
     */
    private final BlockingQueue<Runnable> lobbyTickTasks = new LinkedBlockingDeque<>();

    /**
     * Executes tasks
     */
    private final ExecutorService service;

    /**
     * The last lobby tick
     */
    private long lobbyTickTime, lastLobbyTick;

    /**
     * The amount of ticks to skip.
     */
    private int ticksToSkip;

    /**
     * Running state
     */
    private volatile boolean running = true;

    /**
     * Initialize the server pool
     */
    public OasisGameServer() {
        service = Executors.newFixedThreadPool(2);
    }

    @Override
    public void start() {
        lobbies.put(999, Lobby.createLobby(999));
        lastLobbyTick = System.currentTimeMillis();

        service.execute(() -> {
            Thread.currentThread().setName("Oasis-Lobby-Tick");
            Thread.currentThread().setUncaughtExceptionHandler((t, e) -> e.printStackTrace());

            while (running) {
                try {
                    lobbyTick();
                    if (ticksToSkip != 0) {
                        while (ticksToSkip > 0) {
                            ticksToSkip--;
                            lobbyTick();
                        }
                    }
                    waitUntilNextTick();
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    @Override
    public void stop() {
        running = false;

        service.shutdownNow();
        lobbies.values().forEach(Lobby::dispose);
        lobbies.clear();
    }

    @Override
    public boolean canCreateNewLobby() {
        return lobbies.size() + 1 < maxLobbiesPerInstance;
    }

    @Override
    public int getNewLobbyId() {
        return 999;
    }

    @Override
    public void addLobby(Lobby lobby) {
        lobbies.put(lobby.lobbyId(), lobby);
    }

    @Override
    public void removeLobby(Lobby lobby) {
        lobby.dispose();
        lobbies.remove(lobby.lobbyId());
    }

    @Override
    public Lobby getLobby(int lobbyId) {
        return lobbies.get(lobbyId);
    }

    /**
     * Wait until the next tick
     * TODO: Not desirable but don't know what else to do.
     *
     * @throws InterruptedException e
     */
    private void waitUntilNextTick() throws InterruptedException {
        Thread.sleep(50);
    }

    /**
     * Update lobbies
     */
    private void lobbyTick() {
        final long now = System.currentTimeMillis();
        if (lobbyTickTime >= 50) {
            ticksToSkip += lobbyTickTime / 50 + 1;
            LOGGER.atWarning().every(2).log("Running %s ms behind, skipping %d ticks", lastLobbyTick, ticksToSkip);
        }

        lobbies.forEach((lobbyId, lobby) -> lobby.tick());
        lobbyTickTime = System.currentTimeMillis() - now;
    }

    /**
     * Update games
     */
    private void gameTick() {
        // TODO
    }

    /**
     * Get the internal server instance
     *
     * @return this
     */
    protected static Server get() {
        return SERVER;
    }

}
