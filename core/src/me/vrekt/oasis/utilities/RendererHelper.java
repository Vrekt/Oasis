package me.vrekt.oasis.utilities;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.vrekt.oasis.Oasis;

/**
 * RendererHelper util
 */
public final class RendererHelper {

    /**
     * Font
     */
    public static final BitmapFont FONT = Oasis.get().assets().font();

    /**
     * Draw text
     *
     * @param batch the batch
     * @param text  the text
     * @param x     the x
     * @param y     the y
     */
    public static void drawText(SpriteBatch batch, String text, float x, float y) {
        FONT.draw(batch, text, x, y);
    }

}
