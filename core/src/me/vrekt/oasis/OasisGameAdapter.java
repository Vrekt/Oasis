package me.vrekt.oasis;

import com.badlogic.gdx.Game;

/**
 * The game entry point
 * Provides just two methods for
 * Screening and config stuff.
 */
public class OasisGameAdapter extends Game {

    /**
     * Singleton
     */
    private static OasisGameAdapter thisInstance;

    /**
     * The game
     */
    private Oasis oasis;

    /**
     * Initialize this adapter
     */
    public OasisGameAdapter() {
        if (thisInstance != null) throw new UnsupportedOperationException();
        thisInstance = this;
    }

    /**
     * @return the instance
     */
    public static OasisGameAdapter get() {
        return thisInstance;
    }

    /**
     * Set the FPS
     *
     * @param fps the fps
     */
    public void setFps(int fps) {
        // supposed to be overridden.
    }

    @Override
    public void create() {
        oasis = new Oasis(this);
    }

    @Override
    public void dispose() {
        oasis.dispose();
    }
}
