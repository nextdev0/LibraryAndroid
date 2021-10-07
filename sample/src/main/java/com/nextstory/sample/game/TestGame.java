package com.nextstory.sample.game;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.nextstory.libgdx.Libgdx;
import com.nextstory.libgdx.loader.AndroidObjLoader;
import com.nextstory.sample.R;

/**
 * @author troy
 * @since 1.0
 */
public class TestGame extends Libgdx {
    ModelBatch modelBatch = null;
    ModelInstance model = null;
    Camera camera = null;
    Environment environment = null;

    @Override
    public boolean isScreenOrientationPortrait() {
        return true;
    }

    @Override
    public boolean isFullscreen() {
        return false;
    }

    @Override
    public void onLibgdxNotSupported() {

    }

    @Override
    public void create() {
        super.create();

        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(1f, 1f, 1f);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 300f;

        String path = Utils.getModelPathByRawResources(requireContext(), R.raw.test);
        ObjLoader objLoader = new AndroidObjLoader();
        model = new ModelInstance(objLoader.loadModel(
                Gdx.files.getFileHandle(path, Files.FileType.Absolute), true));
    }

    @Override
    public void render() {
        super.render();

        Gdx.gl.glClearColor(0, 1, 0, 1);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
        modelBatch.render(model, environment);
        modelBatch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
