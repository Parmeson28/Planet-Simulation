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

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class Main extends ApplicationAdapter implements InputProcessor{
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private ModelBuilder modelBuilder;
    private Model sphere;
    private ModelInstance modelInstance;
    private Environment environment;

    private DirectionalLight directionalLight;    

    private boolean isLeftPressed;
    private boolean isRightPressed;
    private boolean isFrontPressed;
    private boolean isBackPressed;
    private boolean isUpPressed;
    private boolean isDownPressed;

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

        sphere = modelBuilder.createSphere(2f, 2f, 2f, 60, 60, 
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

        isLeftPressed = Gdx.input.isKeyPressed(Keys.A);
        isRightPressed = Gdx.input.isKeyPressed(Keys.D);
        isFrontPressed = Gdx.input.isKeyPressed(Keys.W);
        isBackPressed = Gdx.input.isKeyPressed(Keys.S);
        isUpPressed = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT);
        isDownPressed = Gdx.input.isKeyPressed(Keys.SPACE);

        if(isLeftPressed){
            camera.translate(-cameraVelocity, 0f, 0f);
        }
        if(isRightPressed){
            camera.translate(cameraVelocity, 0f, 0f);
        }
        if(isFrontPressed){
            camera.translate(0f, 0f, -cameraVelocity);
        }
        if(isBackPressed){
            camera.translate(0f, 0f, cameraVelocity);
        }
        if(isDownPressed){
            camera.translate(0f, cameraVelocity, 0f);
        }
        if(isUpPressed){
            camera.translate(0f, -cameraVelocity, 0f);
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