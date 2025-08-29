package com.javaProjects.game;

import java.util.Vector;

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
import com.badlogic.gdx.utils.Array;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class Main extends ApplicationAdapter implements InputProcessor{
    //Model Variables
    private ModelBatch modelBatch;
    private ModelBuilder modelBuilder;
    private Model model;
    private Array<ModelInstance> instances;
    private ModelInstance ball, ground;

    //Model Control Variables
    private boolean collision;
    
    //Environment Variables
    private Environment environment;
    private DirectionalLight directionalLight;    

    //Camera Variables
    private PerspectiveCamera camera;
    private float cameraVelocity = 0.05f;
    private Vector3 cameraRotation = new Vector3(0f, 1f, 0f);

    @Override
    public void create() {
        //Setting the camera
        camera = new PerspectiveCamera(75, 
                                        Gdx.graphics.getWidth(),
                                        Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 3f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 300f;

        modelBatch = new ModelBatch();

        //Creating the models
        modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.node().id = "ground";
        modelBuilder.part("box", GL20.GL_TRIANGLES, Usage.Position|Usage.Normal, 
                          new Material(ColorAttribute.createDiffuse(Color.RED))).box(5f, 1f, 5f);

        modelBuilder.node().id = "ball";
        modelBuilder.part("sphere", GL20.GL_TRIANGLES, Usage.Position|Usage.Normal, 
                          new Material(ColorAttribute.createDiffuse(Color.GREEN))).sphere(1f, 1f, 1f, 10, 10);

        model = modelBuilder.end();

        //Instancing the models
        ground = new ModelInstance(model, "ground");
        ball = new ModelInstance(model, "ball");
        ball.transform.setToTranslation(0f, 9f, 0f);


        instances = new Array<ModelInstance>();
        instances.add(ground);
        instances.add(ball);
        
        //Setting the directional light
        directionalLight = new DirectionalLight();
        directionalLight.set(1f, 1f, 1f, 0f, 5f, 0f);

        //Setting environment and light
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
        modelBatch.render(instances, environment);
        modelBatch.end();


        //Camera Movement
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
        //Camera Rotation
        if(Gdx.input.isKeyPressed(Keys.E)){
            camera.rotate(cameraRotation, 1f);
        }else if(Gdx.input.isKeyPressed(Keys.Q)){
            camera.rotate(cameraRotation, -1f);
        }



        //Handling Planets Movement
        final float delta = Math.min(1f/30f, Gdx.graphics.getDeltaTime());

        if(!collision){
            ball.transform.translate(0f, -delta, 0f);
            collision = checkCollision();
        }
    }


    boolean checkCollision(){
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }


    @Override
    public void dispose() {
        model.dispose();
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