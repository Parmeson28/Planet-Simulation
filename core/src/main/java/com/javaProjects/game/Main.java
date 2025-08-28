package com.javaProjects.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class Main extends ApplicationAdapter implements InputProcessor{
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private ModelBuilder modelBuilder;
    private Model sphere;
    private ModelInstance modelInstance;
    private Environment environment;

    private DirectionalLight directionalLight;    

    private float cameraVelocity = 0.05f;

    @Override
    public void create() {
        camera = new PerspectiveCamera(75, 
                                        Gdx.graphics.getWidth(),
                                        Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 3f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 300f;

        modelBatch = new ModelBatch();
        modelBuilder = new ModelBuilder();

        sphere = modelBuilder.createSphere(2f, 2f, 2f, 4, 4, 
                                            new Material(ColorAttribute.createDiffuse(Color.BLUE)), 
                                            Usage.Position|Usage.Normal);
        
        modelInstance = new ModelInstance(sphere, 0f, 5f, 0f);

        directionalLight = new DirectionalLight();
        directionalLight.set(1f, 1f, 1f, 0f, 5f, 0f);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(directionalLight);
        
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
        
        camera.update();

        modelBatch.begin(camera);
        modelBatch.render(modelInstance, environment);
        modelBatch.end();

        if(Gdx.input.isKeyPressed(Keys.A)){
            camera.translate(-cameraVelocity, 0f, 0f);
        }
        if(Gdx.input.isKeyPressed(Keys.D)){
            camera.translate(cameraVelocity, 0f, 0f);
        }
        if(Gdx.input.isKeyPressed(Keys.W)){
            camera.translate(0f, 0f, -cameraVelocity);
        }
        if(Gdx.input.isKeyPressed(Keys.S)){
            camera.translate(0f, 0f, cameraVelocity);
        }
        if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
            camera.translate(0f, -cameraVelocity, 0f);
        }
        if(Gdx.input.isKeyPressed(Keys.SPACE)){
            camera.translate(0f, cameraVelocity, 0f);
        }

        if(Gdx.input.isKeyPressed(Keys.Q)){
            modelInstance.transform.rotate(new Vector3(0f, 1f, 0f), 1f);
        }else if(Gdx.input.isKeyPressed(Keys.E)){
            modelInstance.transform.rotate(new Vector3(0f, 1f, 0f), -1f);
        }

    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }


    @Override
    public void dispose() {
        sphere.dispose();
        modelBatch.dispose();
    }

    
    

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}