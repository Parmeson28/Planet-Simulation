package com.javaProjects.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class Main extends ApplicationAdapter{

    //Model Variables
    private ModelBatch modelBatch;
    private ModelBuilder modelBuilder;
    private Model model;
    private ModelInstance star, planet;
    private Array<ModelInstance> instances;

    //Orbit system variables
    private float planetAngle = 0f;
    private float planetSpeed = 20f;
    private float planetRadius = 5f;
    private float starRadius = 10f;
    private Vector3 initialPosition;
    private Array<Vector3> initialPositions;

    //Environment Variables
    private Environment environment;
    private DirectionalLight directionalLight;

    //Camera Variables
    private PerspectiveCamera camera;
    private float cameraVel = 0.05f;
    private Vector3 cameraRotation = new Vector3(0f, 1f, 0f);

    //FPS counter
    private SpriteBatch batch;
    private BitmapFont font;
    private boolean showFPS = true;

    @Override
    public void create() {

        /******** === OVERALL VIEW OF THE CODE === ********/
        /******** ===== (CREATING THE CAMERA) ===== ********/

        //Setting the camera as PerspectiveCamera for a 3d impression
        camera = new PerspectiveCamera(75, 
                                       Gdx.graphics.getWidth(), 
                                       Gdx.graphics.getHeight());

        //Setting the camera position and where it looks at
        camera.position.set(0f, 30f, 0f);
        camera.lookAt(0f, 0f, 0f);
        //Setting how far and how near we need 
        //to be to stop rendering the models
        camera.near = 0.1f;
        camera.far = 300f;

        /******** ===== (CREATING THE CAMERA) ===== ********/
        /******** === OVERALL VIEW OF THE CODE === ********/


        /******** ============ OVERALL VIEW OF THE CODE ============ ********/
        /******** ===== (BUILDING THE PLANET AND STAR MODELS) ===== ********/

        //The modelBuilder is used to build the sphere model and passes the node id 
        //which later will be used to identify the type of body it is

        modelBuilder = new ModelBuilder();
        instances = new Array<>();
        initialPositions = new Array<>();

        modelBuilder.begin();
        modelBuilder.node().id = "sun";
        modelBuilder.part("sphere", GL20.GL_TRIANGLES, Usage.Position|Usage.Normal, 
                          new Material(ColorAttribute.createDiffuse(Color.YELLOW))).sphere(starRadius, starRadius, starRadius, 40, 40);
        model = modelBuilder.end();

        star = new ModelInstance(model);

        initialPosition = new Vector3(0f, 0f, 0f);
        initialPositions.add(initialPosition);

        star.transform.setToTranslation(initialPosition);
        instances.add(star);

        modelBuilder.begin();
        modelBuilder.node().id = "planet";
        modelBuilder.part("sphere", GL20.GL_TRIANGLES, Usage.Position|Usage.Normal, 
                          new Material(ColorAttribute.createDiffuse(Color.GREEN))).sphere(planetRadius, planetRadius, planetRadius, 40, 40);
        model = modelBuilder.end();

        planet = new ModelInstance(model);

        initialPosition = new Vector3(5f, 0f, 15f);
        initialPositions.add(initialPosition);

        planet.transform.setToTranslation(initialPosition);
        instances.add(planet);

        modelBuilder.begin();
        modelBuilder.node().id = "planet";
        modelBuilder.part("sphere", GL20.GL_TRIANGLES, Usage.Position|Usage.Normal, 
                          new Material(ColorAttribute.createDiffuse(Color.BLUE))).sphere(planetRadius, planetRadius, planetRadius, 40, 40);
        model = modelBuilder.end();
        
        planet = new ModelInstance(model);

        initialPosition = new Vector3(15f, 0f, 2f);
        initialPositions.add(initialPosition);

        planet.transform.setToTranslation(initialPosition);
        instances.add(planet);

        System.out.println(instances.size);
        
        /******** ===== (BUILDING THE PLANET AND STAR MODELS) ===== ********/
        /******** ============ OVERALL VIEW OF THE CODE ============ ********/


        /******** ========== OVERALL VIEW OF THE CODE ========== ********/
        /******** ===== (SETTING ENVIRONMENT AND LIGHTING) ===== ********/

        //Setting the directional light
        directionalLight = new DirectionalLight();
        directionalLight.set(1f, 1f, 1f, 0f, -2f, 0f);

        //Setting environment and passing the 
        //directional light created before
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(directionalLight);

        //FPS counter
        batch = new SpriteBatch();
        font = new BitmapFont();
        
        /******** ===== (SETTING ENVIRONMENT AND LIGHTING) ===== ********/
        /******** ========== OVERALL VIEW OF THE CODE ========== ********/

        modelBatch = new ModelBatch();
    }


    //MAIN LOOP FOR THE APPLICATION TO RUN
    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        planetAngle += planetSpeed * delta;

        if (planetAngle > 360f)
            planetAngle -= 360f;
        
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);

        //Drawing FPS
        batch.begin();
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20);
        batch.end();

        camera.update();

        int i = 0;

        
        //Searching for planet models in the instances
        for(ModelInstance obj : instances){

            i++;

            if(obj.getNode("planet") != null){
                

                float x = MathUtils.cosDeg(planetAngle) * (planetRadius + starRadius + initialPositions.get(i-1).x);
                float z = MathUtils.sinDeg(planetAngle) * (planetRadius + starRadius + initialPositions.get(i-1).z);

                obj.transform.setToTranslation(x, 0f, z);
            }

        }
        

        modelBatch.begin(camera);
        modelBatch.render(instances, environment);
        modelBatch.end();

        //Camera Movement
        if(Gdx.input.isKeyPressed(Keys.A)){
            camera.translate(-cameraVel, 0f, 0f);
        }
        if(Gdx.input.isKeyPressed(Keys.D)){
            camera.translate(cameraVel, 0f, 0f);
        }
        if(Gdx.input.isKeyPressed(Keys.W)){
            camera.translate(0f, 0f, -cameraVel);
        }
        if(Gdx.input.isKeyPressed(Keys.S)){
            camera.translate(0f, 0f, cameraVel);
        }
        if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
            camera.translate(0f, -cameraVel, 0f);
        }
        if(Gdx.input.isKeyPressed(Keys.SPACE)){
            camera.translate(0f, cameraVel, 0f);
        }

        //Camera Rotation
        if(Gdx.input.isKeyPressed(Keys.E)){
            camera.rotate(cameraRotation, 1f);
        }else if(Gdx.input.isKeyPressed(Keys.Q)){
            camera.rotate(cameraRotation, -1f);
        }

    }
    
    @Override
    public void dispose() {

        font.dispose();
        batch.dispose();

        modelBatch.dispose();
        model.dispose();
    }
    
}