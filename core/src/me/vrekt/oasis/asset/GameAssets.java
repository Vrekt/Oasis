package me.vrekt.oasis.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

/**
 * Handles storing game assets
 */
public final class GameAssets implements Disposable {

    /**
     * Asset manager
     */
    private final AssetManager manager = new AssetManager();

    /**
     * UI Skin atlas
     */
    private final TextureAtlas defaultUiSkinAtlas;

    /**
     * The global UI skin
     */
    private final Skin defaultUiSkin;

    /**
     * Loads the assets needed
     */
    public GameAssets() {
        defaultUiSkinAtlas = new TextureAtlas("ui/UserInterface.atlas");
        defaultUiSkin = new Skin(Gdx.files.internal("ui/UserInterface.json"), defaultUiSkinAtlas);

        manager.load("player/Character.atlas", TextureAtlas.class);
        manager.finishLoading();
    }

    /**
     * Get an atlas
     *
     * @param atlas the atlas
     * @return the atlas
     */
    public TextureAtlas getAtlas(String atlas) {
        return manager.get(atlas);
    }

    /**
     * @return the UI skin
     */
    public Skin defaultUiSkin() {
        return defaultUiSkin;
    }

    @Override
    public void dispose() {
        manager.dispose();
        defaultUiSkinAtlas.dispose();
        defaultUiSkin.dispose();
    }
}
