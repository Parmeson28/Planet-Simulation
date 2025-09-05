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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class Main extends ApplicationAdapter{

    public void CreatePlanets(float posx, float posz){

        float planetAngle = MathUtils.random(0f, 360f);

        planetAngles.add(planetAngle);

        modelBuilder.begin();

        modelBuilder.part("sphere", GL20.GL_TRIANGLES, Usage.Position|Usage.Normal, 
                          new Material(ColorAttribute.createDiffuse(Color.GREEN))).sphere(planetRadius, planetRadius, planetRadius, 40, 40);
        model = modelBuilder.end();

        planet = new ModelInstance(model);

        initialPosition = new Vector3(posx, 0f, posz);
        initialPositions.add(initialPosition);

        planet.transform.setToTranslation(initialPosition);
        planetInstances.add(planet);
        
    }

    public void MovePlanets(float delta, ModelInstance obj, int i){

        angle = planetAngles.get(i-1);

        float distanceFromOrigin = (float)Math.sqrt(Math.pow(initialPositions.get(i-1).x, 2) + Math.pow(initialPositions.get(i-1).z, 2));

        angle += (sunMass * delta)/(distanceFromOrigin/100);

        if (angle > 360f)
            angle -= 360f;

        planetAngles.set(i-1, angle);

        float x = MathUtils.cosDeg(angle) * (planetRadius + starRadius + distanceFromOrigin);
        float z = MathUtils.sinDeg(angle) * (planetRadius + starRadius + distanceFromOrigin);

        obj.transform.setToTranslation(x, 0f, z);
    }

    

    //Model Variables
    private ModelBatch modelBatch;
    private ModelBuilder modelBuilder;
    private Model model;
    private ModelInstance star, planet;
    private Array<ModelInstance> planetInstances, starInstance;

    //Orbit system variables
    private float angle = 0;
    private float sunMass = 5f;
    private float planetRadius = 1f;
    private float starRadius = 10f;
    private Vector3 initialPosition;
    private Array<Float> planetAngles = new Array<>();
    private Array<Vector3> initialPositions;

    //Environment Variables
    private Environment environment;
    private DirectionalLight directionalLight;

    //Camera Variables
    private PerspectiveCamera camera;
    private float cameraVel = 0.1f;
    private Vector3 cameraRotation = new Vector3(0f, 1f, 0f);

    //FPS counter
    private SpriteBatch batch;
    private BitmapFont font;
    private boolean showFPS = true;
    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {

        /******** === OVERALL VIEW OF THE CODE === ********/
        /******** ===== (CREATING THE CAMERA) ===== ********/

        //Setting the camera as PerspectiveCamera for a 3d impression
        camera = new PerspectiveCamera(75, 
                                       Gdx.graphics.getWidth(), 
                                       Gdx.graphics.getHeight());

        //Setting the camera position and where it looks at
        camera.position.set(0f, 50f, 0f);
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
        planetInstances = new Array<>();
        starInstance = new Array<>();
        initialPositions = new Array<>();

        modelBuilder.begin();
        modelBuilder.node().id = "sun";
        modelBuilder.part("sphere", GL20.GL_TRIANGLES, Usage.Position|Usage.Normal, 
                          new Material(ColorAttribute.createDiffuse(Color.YELLOW))).sphere(starRadius, starRadius, starRadius, 40, 40);
        model = modelBuilder.end();

        star = new ModelInstance(model);

        initialPosition = new Vector3(0f, 0f, 0f);

        star.transform.setToTranslation(initialPosition);
        starInstance.add(star);
        
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

        //FPS counter and UI stuff
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        
        
        /******** ===== (SETTING ENVIRONMENT AND LIGHTING) ===== ********/
        /******** ========== OVERALL VIEW OF THE CODE ========== ********/

        modelBatch = new ModelBatch();
    }


    //MAIN LOOP FOR THE APPLICATION TO RUN
    @Override
    public void render() {
        
        
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
        
       
        camera.update();

        int i = 0;

        float delta = Gdx.graphics.getDeltaTime();

        
        //Searching for planet models in the instances
        for(ModelInstance obj : planetInstances){
            i++;
            MovePlanets(delta, obj, i);
        }
    
        modelBatch.begin(camera);

        modelBatch.render(starInstance, environment);
        modelBatch.render(planetInstances);

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
        if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)){
            camera.translate(0f, cameraVel, 0f);
        }

        //Camera Rotation
        if(Gdx.input.isKeyPressed(Keys.E)){
            camera.rotate(cameraRotation, 1f);
        }else if(Gdx.input.isKeyPressed(Keys.Q)){
            camera.rotate(cameraRotation, -1f);
        }

        //Create new planet
        if(Gdx.input.isKeyJustPressed(Keys.ENTER)){
            float randX = MathUtils.random(10, 40);
            float randZ = MathUtils.random(10, 40);

            CreatePlanets(randX, randZ);
        }

        //Reduce and increase simulation speed
        if(Gdx.input.isKeyJustPressed(Keys.P)){
            sunMass += 1;
        }
        if(Gdx.input.isKeyJustPressed(Keys.O)){
            sunMass -= 1;
        }
        if(Gdx.input.isKeyJustPressed(Keys.SPACE)){
            sunMass = 0;
        }

         //FPS counter and UI stuff
         batch.begin();
         font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20);
         font.draw(batch, "Press W,A,S,D to move around", 10, 470);
         font.draw(batch, "Press E,Q to rotate camera", 10, 450);
         font.draw(batch, "Press ENTER to create a planet", 420, 20);
         font.draw(batch, "Press P to increase velocity", 450, 450);
         font.draw(batch, "Press O to decrease velocity", 444, 470);
         font.draw(batch, "Press SPACE to set velocity to 0", 423, 430);
         batch.end();
    }
    
    @Override
    public void dispose() {

        font.dispose();
        batch.dispose();

        modelBatch.dispose();
        model.dispose();
    }
    
}