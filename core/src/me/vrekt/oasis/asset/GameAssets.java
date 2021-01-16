package me.vrekt.oasis.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
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

        manager.load("characters/healer_female/HealerFemale.atlas", TextureAtlas.class);
        manager.load("characters/healer_male/HealerMale.atlas", TextureAtlas.class);
        manager.load("player/nametag.png", Texture.class);

        characters.put(CharacterType.ATHENA, () -> manager.get("characters/healer_female/HealerFemale.atlas"));
        characters.put(CharacterType.CRIMSON, () -> manager.get("characters/healer_male/HealerMale.atlas"));

        font = new BitmapFont(Gdx.files.internal("ui/UserInterfaceFont.fnt"), Gdx.files.internal("ui/UserInterfaceFont.png"), false);

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
     * Get a texture
     *
     * @param texture the texture
     * @return the texture
     */
    public Texture getTexture(String texture) {
        return manager.get(texture);
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
