package com.javaProjects.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.CollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionAlgorithm;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDispatcherInfo;
import com.badlogic.gdx.physics.bullet.collision.btManifoldResult;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class Main extends ApplicationAdapter implements InputProcessor{

    static class GameObject extends ModelInstance implements Disposable{
        public final btCollisionObject body;
        public boolean moving;

        public GameObject(Model model, String node, btCollisionShape shape){
            super(model, node);
            body = new btCollisionObject();
            body.setCollisionShape(shape);
        }

        @Override
        public void dispose(){
            body.dispose();
        }

        static class Constructor implements Disposable{
            public final Model model;
            public final String node;
            public final btCollisionShape shape;

            public Constructor(Model model, String node, btCollisionShape shape){
                this.model = model;
                this.node = node;
                this.shape = shape;
            }

            public GameObject construct(){
                return new GameObject(model, node, shape);
            }

            @Override
            public void dispose(){
                shape.dispose();
            }
        }
    }


    //Model Variables
    private ModelBatch modelBatch;
    private ModelBuilder modelBuilder;
    private Model model;
    private ModelInstance ball, ground;
    Array<GameObject> instances;
    ArrayMap<String, GameObject.Constructor> constructors;

    //Model Control Variables
    private boolean collision;
    
    //Environment Variables
    private Environment environment;
    private DirectionalLight directionalLight;    

    //Camera Variables
    private PerspectiveCamera camera;
    private CameraInputController camController;
    private float cameraVelocity = 0.05f;
    private Vector3 cameraRotation = new Vector3(0f, 1f, 0f);

    //Bullet Object/Shape Variables
    btCollisionShape groundShape, ballShape;
    btCollisionObject groundObject, ballObject;

    //Bullet Collision Variables
    btCollisionConfiguration collisionConfig;
    btDispatcher dispatcher;



    @Override
    public void create() {
        //Initializing Bullet
        Bullet.init();

        //Setting the camera
        camera = new PerspectiveCamera(75, 
                                        Gdx.graphics.getWidth(),
                                        Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 3f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 300f;

        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);

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

        modelBuilder.node().id = "box";
        modelBuilder.part("box", GL20.GL_TRIANGLES, Usage.Position|Usage.Normal, 
                          new Material(ColorAttribute.createDiffuse(Color.PINK))).box(1f, 1f, 1f);

        modelBuilder.node().id = "cone";
        modelBuilder.part("cone", GL20.GL_TRIANGLES, Usage.Position|Usage.Normal, 
                          new Material(ColorAttribute.createDiffuse(Color.CYAN))).cone(1f, 2f, 1f, 10);

        modelBuilder.node().id = "capsule";
        modelBuilder.part("capsule", GL20.GL_TRIANGLES, Usage.Position|Usage.Normal, 
                          new Material(ColorAttribute.createDiffuse(Color.MAGENTA))).capsule(0.5f, 2f, 10);
        model = modelBuilder.end();

        //Instancing the models into the constructor class
        constructors = new ArrayMap<String, GameObject.Constructor>(String.class, 
                                                                    GameObject.Constructor.class);

        constructors.put("ground", new GameObject.Constructor(model, "ground", new btBoxShape(new Vector3(2.5f, 0.5f, 2.5f))));
        constructors.put("sphere", new GameObject.Constructor(model, "ball", new btSphereShape(0.5f)));
        constructors.put("box", new GameObject.Constructor(model, "ball", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f))));
        constructors.put("cone", new GameObject.Constructor(model, "cone", new btConeShape(0.5f, 2f)));
        constructors.put("capsule", new GameObject.Constructor(model, "capsule", new btCapsuleShape(0.5f, 1f)));

        instances = new Array<GameObject>();
        instances.add(constructors.get("ground").construct());

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);

        //Setting the directional light
        directionalLight = new DirectionalLight();
        directionalLight.set(1f, 1f, 1f, 0f, 5f, 0f);

        //Setting environment and light
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(directionalLight);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
        
        camera.update();

        modelBatch.begin(camera);
        modelBatch.render(instances, environment);
        modelBatch.end();


        /* 
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
        */
        camController.update();

        //Handling Planets Movement
        final float delta = Math.min(1f/30f, Gdx.graphics.getDeltaTime());

        //Check the collision between ball and ground
        if(!collision){
            collision = checkCollision(ballObject, groundObject);
        }
    }


    boolean checkCollision(btCollisionObject obj0, btCollisionObject obj1){
        CollisionObjectWrapper co0 = new CollisionObjectWrapper(obj0);
		CollisionObjectWrapper co1 = new CollisionObjectWrapper(obj1);
		
		btCollisionAlgorithm algorithm = dispatcher.findAlgorithm(co0.wrapper, co1.wrapper, null, 0);

		btDispatcherInfo info = new btDispatcherInfo();
		btManifoldResult result = new btManifoldResult(co0.wrapper, co1.wrapper);
		
		algorithm.processCollision(co0.wrapper, co1.wrapper, info, result);
		
		boolean r = result.getPersistentManifold().getNumContacts() > 0;
		
        dispatcher.freeCollisionAlgorithm(algorithm.getCPointer());
		result.dispose();
		info.dispose();
		algorithm.dispose();
		co1.dispose();
		co0.dispose();

		return r;
    }

    @Override
    public void dispose() {
        for(GameObject obj : instances)
            obj.dispose();
        
        for(GameObject.Constructor ctor : constructors.values())
            ctor.dispose();
        constructors.clear();
		
		dispatcher.dispose();
		collisionConfig.dispose();
		
		modelBatch.dispose();
		model.dispose();
    }
    
    @Override
    public boolean keyDown(int keycode) {
        return false;
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