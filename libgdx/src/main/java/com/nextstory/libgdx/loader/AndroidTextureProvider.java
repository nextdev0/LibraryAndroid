package com.nextstory.libgdx.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;

/**
 * @author troy
 * @since 2.1
 */
public class AndroidTextureProvider implements TextureProvider {
    private final Texture.TextureFilter minFilter;
    private final Texture.TextureFilter magFilter;
    private final Texture.TextureWrap uWrap;
    private final Texture.TextureWrap vWrap;
    private final boolean useMipMaps;

    public AndroidTextureProvider() {
        minFilter = magFilter = Texture.TextureFilter.Linear;
        uWrap = vWrap = Texture.TextureWrap.Repeat;
        useMipMaps = false;
    }

    public AndroidTextureProvider(Texture.TextureFilter minFilter,
                                  Texture.TextureFilter magFilter,
                                  Texture.TextureWrap uWrap,
                                  Texture.TextureWrap vWrap,
                                  boolean useMipMaps) {
        this.minFilter = minFilter;
        this.magFilter = magFilter;
        this.uWrap = uWrap;
        this.vWrap = vWrap;
        this.useMipMaps = useMipMaps;
    }

    @Override
    public Texture load(String fileName) {
        Texture result = new Texture(Gdx.files.absolute(fileName), useMipMaps);
        result.setFilter(minFilter, magFilter);
        result.setWrap(uWrap, vWrap);
        return result;
    }
}
