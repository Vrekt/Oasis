package me.vrekt.oasis.utilities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/**
 * A custom implementation of {@link com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer}
 */
public final class CustomOrthoTiledMapRenderer extends OrthogonalTiledMapRenderer {

    public CustomOrthoTiledMapRenderer(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
    }

    @Override
    public void render() {
        beginRender();
        for (MapLayer layer : map.getLayers()) {
            renderMapLayer(layer);
        }
        // do not end render
    }
}
