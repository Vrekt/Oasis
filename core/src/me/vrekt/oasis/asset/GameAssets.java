package me.vrekt.oasis.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.asset.character.CharacterType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Handles storing game assets
 */
public final class GameAssets implements Disposable {

    /**
     * Asset manager
     */
    private final AssetManager manager = new AssetManager();

    /**
     * A map of characters in the game
     */
    private final Map<CharacterType, Supplier<TextureAtlas>> characters = new HashMap<>();

    /**
     * UI Skin atlas
     */
    private final TextureAtlas defaultUiSkinAtlas;

    /**
     * The global UI skin
     */
    private final Skin defaultUiSkin;

    /**
     * The default font
     */
    private final BitmapFont font;

    /**
     * Loads the assets needed
     */
    public GameAssets() {
        defaultUiSkinAtlas = new TextureAtlas("ui/UserInterface.atlas");
        defaultUiSkin = new Skin(Gdx.files.internal("ui/UserInterface.json"), defaultUiSkinAtlas);

        for (CharacterType characterType : CharacterType.values()) {
            final String asset = characterType.assetPath();
            manager.load(asset, TextureAtlas.class);

            characters.put(characterType, () -> manager.get(asset));
        }

        font = new BitmapFont(Gdx.files.internal("ui/UserInterfaceFont.fnt"), Gdx.files.internal("ui/UserInterfaceFont.png"), false);
        manager.finishLoading();
    }

    /**
     * @return the UI skin
     */
    public Skin defaultUiSkin() {
        return defaultUiSkin;
    }

    /**
     * @return the font
     */
    public BitmapFont font() {
        return font;
    }

    /**
     * Get a character
     *
     * @param type the type
     * @return the {@link Supplier} with the contained character atlas.
     */
    public Supplier<TextureAtlas> getCharacter(CharacterType type) {
        return characters.get(type);
    }

    @Override
    public void dispose() {
        manager.dispose();
        defaultUiSkinAtlas.dispose();
        defaultUiSkin.dispose();
        characters.clear();
        font.dispose();
    }
}
