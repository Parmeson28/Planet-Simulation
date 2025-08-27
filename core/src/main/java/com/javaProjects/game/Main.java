package com.javaProjects.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter{

    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Model model;
    private ModelInstance modelInstance;
    private Environment environment;
    private AnimationController controller;

    @Override
    public void create() {
        camera = new PerspectiveCamera(75, 
                                        Gdx.graphics.getWidth(), 
                                        Gdx.graphics.getHeight());
        camera.position.set(0f, 100f, 100f);
        camera.lookAt(0f, 100f, 0f);

        camera.near = 0.1f;
        camera.far = 300f;
        
        modelBatch = new ModelBatch();

        ObjLoader modelLoader = new ObjLoader();
        model = modelLoader.loadModel(Gdx.files.getFileHandle("walking_3.obj", Files.FileType.Internal));
        
        modelInstance = new ModelInstance(model);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f));

        controller = new AnimationController(modelInstance);
        //controller.setAnimation("Mixamo.com", -1);
    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);

        camera.update();
        controller.update(Gdx.graphics.getDeltaTime());

        modelBatch.begin(camera);
        modelBatch.render(modelInstance, environment);
        modelBatch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }
   
}
