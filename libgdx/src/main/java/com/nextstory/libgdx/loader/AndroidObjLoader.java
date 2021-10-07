package com.nextstory.libgdx.loader;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;

/**
 * @author troy
 * @since 2.1
 */
public class AndroidObjLoader extends ObjLoader {
    public AndroidObjLoader() {
        super();
    }

    public AndroidObjLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public Model loadModel(FileHandle fileHandle, boolean flipV) {
        return loadModel(
                fileHandle,
                new AndroidTextureProvider(),
                new ObjLoader.ObjLoaderParameters(flipV));
    }
}
