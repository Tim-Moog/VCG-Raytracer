// ************************************************************ //
//                      Hochschule Duesseldorf                  //
//                                                              //
//                     Vertiefung Computergrafik                //
// ************************************************************ //

/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    1. Documentation:    Did you comment your code shortly but clearly?
    2. Structure:        Did you clean up your code and put everything into the right bucket?
    3. Performance:      Are all loops and everything inside really necessary?
    4. Theory:           Are you going the right way?

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 <<< YOUR TEAM NAME >>>

     Master of Documentation:
     Master of Structure:
     Master of Performance:
     Master of Theory:

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

import java.io.FileInputStream;
import java.io.InputStream;
import raytracer.Raytracer;
import scene.Scene;
import scene.camera.PerspCam;
import scene.material.Lambert;
import scene.material.Material;
import scene.material.Phong;
import scene.material.Solid;
import scene.shape.BoundingBox;
import ui.Window;
import utils.ObjImporter;
import utils.RgbColor;
import utils.algebra.Vec3;

/*
    - THE RAYTRACER -

    TEAM:

    1. Selina Ständer
    2. Tim Moog
    3. Paula Schäfer
    4.
 */

// Main application class. This is the routine called by the JVM to run the program.
public class Main {

    /** RESOLUTION **/

    static final int IMAGE_WIDTH = 800;
    static final int IMAGE_HEIGHT = 600;

    /** CORNELL_BOX_DIMENSION **/

    static final float BOX_DIMENSION = 4f;

    /** RAYTRACER **/

    static final int RECURSIONS = 4;
    static final int ANTI_ALIASING = (int) Math.pow(5, 2); // only valid AA values are 1 and any number whose sqr root
                                                           // results a whole number
    static final boolean USE_SOFT_SHADOWS = false;

    /** LIGHT **/
    static final short LIGHT_DENSITY = 20;
    static final short LIGHT_SAMPLES = (short) Math.pow(7, 2);// only valid AA values are 1 and any number whose sqr
                                                              // root results a whole number

    static final RgbColor BACKGROUND_COLOR = RgbColor.BLACK;

    // DONT SET HEIGHT TO EXACT SAME VALUE AS ROOF PLANE ELSE YOULL SUMMON A
    // DEMON!!!
    // careful: now that the square is set inside of AreaLight be careful not to put
    // the light too close to the ceiling, else the square might get placed outside
    // our cornell box
    static final Vec3 LIGHT_POSITION = new Vec3(0, 5.9f, 2);
    static final short AREA_LIGHT_SIZE = 1;

    /** GI **/
    static final boolean USE_GI = false;
    static final int GI_LEVEL = 0;
    static final int GI_SAMPLES = 0;

    static final RgbColor LIGHT_COLOR = new RgbColor(0.7f, 0.7f, 0.7f);
    static final RgbColor AMBIENT_LIGHT = RgbColor.PURPLE.multScalar(0.1f);//new RgbColor(0.12f, 0.1f, 0.15f);

    static final boolean USE_AO = false;
    static final int NUMBER_OF_AO_SAMPLES = 0;
    static final float AO_MAX_DISTANCE = 0f;

    /** CAMERA **/

    static final Vec3 CAM_POS = new Vec3(0, 3, 6);
    static final Vec3 LOOK_AT = new Vec3(0, 3, 0);
    static final Vec3 UP_VECTOR = new Vec3(0, 1, 0);

    static final float VIEW_ANGLE = 70;

    /** MATERIAL - REFRECTIVE INDICES **/

    protected static final float AIR = 1.0f;
    protected static final float WATER = 1.33f;
    protected static final float GLASS = 1.5f;
    protected static final float DIAMOND = 2.4f;

    /** DEBUG **/
    // this gets rid of the debug info on the bottom of the window
    static final boolean SHOW_PARAM_LABEL = true;

    /** Initial method. This is where the show begins. **/
    public static void main(String[] args) {
        Window renderWindow = new Window(IMAGE_WIDTH, IMAGE_HEIGHT);

        System.out.printf("Hello World! Again!");
        draw(renderWindow);
    }

    /** Draw the scene using our Raytracer **/
    private static void draw(Window renderWindow) {
        Scene renderScene = new Scene();

        setupScene(renderScene);

        raytraceScene(renderWindow, renderScene);
    }

    /** Setup all components that we want to see in our scene **/
    private static void setupScene(Scene renderScene) {
        // IMPORTANT: ALWAYS HAS TO BE SET ONCE
        Material.setAmbientK(AMBIENT_LIGHT);

        setupCameras(renderScene);

        setupCornellBox(renderScene);

        setupObjects(renderScene);

        //setupExtras(renderScene); //dont enable this unless you want slooooowwwwwww
        // load

        setupLights(renderScene);
    }

    private static void setupLights(Scene renderScene) {
        // Lights - task 3
        // renderScene.createPointLight(new Vec3 (-2.5f,2.5f,4), new
        // RgbColor(0.3f,0.7f,0.3f));
        // renderScene.createPointLight(new Vec3 (3f,2.5f,3), new
        // RgbColor(0.7f,0.3f,0.3f));
        // renderScene.createPointLight(new Vec3 (-2.5f,5f,2), new
        // RgbColor(0.3f,0.3f,0.7f));

        // Lights - task 4
        // renderScene.createPointLight(LIGHT_POSITION, LIGHT_COLOR);

        // Lights - task 6
        renderScene.createAreaLight(renderScene, LIGHT_SAMPLES, AREA_LIGHT_SIZE, LIGHT_POSITION, LIGHT_COLOR, 3.5f);

    }

    private static void setupCameras(Scene renderScene) {
        PerspCam camera = new PerspCam(CAM_POS,
                LOOK_AT,
                UP_VECTOR,
                VIEW_ANGLE,
                (float) IMAGE_WIDTH / IMAGE_HEIGHT);
        // casting needed for correct proportions and no we cant change the variables to
        // be type float since that doesnt work with the constructor for Window
        renderScene.setSceneCamera(camera);
    }

    private static void setupObjects(Scene renderScene) {

        Material test = new Lambert(RgbColor.PURPLE, 0f, 0f);

        Material glass = new Phong(RgbColor.RED, new RgbColor(1f, 1f, 1f), 20, 0f, 1f, GLASS);
        Material water = new Phong(RgbColor.PURPLE, new RgbColor(1f, 1f, 1f), 20, 0f, 0.9f, WATER);
        Material diamond = new Phong(RgbColor.BLUE, new RgbColor(1f, 1f, 1f), 20, 0f, 0.9f, DIAMOND);
        Material reflection = new Phong(RgbColor.PURPLE, new RgbColor(0.8f, 1f, 0.8f), 1, 1f, 0f);

        renderScene.createSphere(new Vec3(-1.2f, 1f, -2f), 1f, reflection);
        renderScene.createSphere(new Vec3(1f, 1f, -1f), 1f, glass);
        //renderScene.createSphere(new Vec3(-0.1f, 1.8f, 0f), 1f, diamond);
        //renderScene.createSphere(new Vec3(-1f, 0.75f, -1.5f), 0.75f, phong_blue);
        //renderScene.createCubeTower(1.2f, new Vec3(1,0.6f,-1), lambert_trans);

        /* 
        InputStream inStream;
        InputStream matInStream;
        try {
            inStream = new FileInputStream("VCG_Raytracer/models/duck_test2.obj");
            matInStream = new FileInputStream("VCG_Raytracer/models/mats/duck_test2.mtl");

            BoundingBox boxIncludingMesh;

            boxIncludingMesh = ObjImporter.loadWithTextures(
                    inStream,
                    matInStream, // new Lambert(RgbColor.CYAN),
                    new Vec3(0f, 0f, -2f),
                    new Vec3(-90, -45, 0),
                    2f);

            renderScene.addShape(boxIncludingMesh); //commented out to make the uploaded
            // version easier to run for now and totally not to hide our masterpiece hehehe
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println("Error: somethign went wrong at .obj import");
        }*/

        System.err.println(renderScene.getShapes().size());
    }

    // for live showcases change the materials to lambert gray or phong air(fastest
    // phong)
    public static void setupExtras(Scene renderScene) {
        InputStream inStream;
        try {
            // first we put suzanne in the back left corner and make her look slightly up
            //inStream = new FileInputStream("VCG_Raytracer/models/blender-suzanne.obj");

            BoundingBox boxIncludingMesh;

            /*boxIncludingMesh = ObjImporter.load(
                    inStream,
                    new Lambert(RgbColor.GREEN), // new Phong(RgbColor.PURPLE, new RgbColor(0.8f, 1f, 0.8f), 18, 0.0f,
                                                 // 0.8f, AIR),
                    new Vec3(-2.5f, 0.75f, -2.5f),
                    new Vec3(-45, 20, 0),
                    0.7f);

            renderScene.addShape(boxIncludingMesh);

            // then we add a floating utah teapot above her
            inStream = new FileInputStream("VCG_Raytracer/models/utah_teapot.obj");

            boxIncludingMesh = ObjImporter.load(
                    inStream,
                    new Lambert(RgbColor.GREEN), // new Phong(RgbColor.PURPLE, new RgbColor(0.8f, 1f, 0.8f), 18, 0.0f,
                                                 // 0.8f, AIR),
                    new Vec3(-2f, 3f, 1),
                    new Vec3(40, 0, -45),
                    0.5f);

            renderScene.addShape(boxIncludingMesh);

            // and finally we'll put a big stanford bunny to the right
            // notes for low poly bunny - rotate x-90 for it to sit upright and needs to be
            // scaled down to like 0.02f due to it being massive
            // normal bunny had too many faces sadly
            inStream = new FileInputStream("VCG_Raytracer/models/Bunny-LowPoly.obj");

            boxIncludingMesh = ObjImporter.load(
                    inStream,
                    new Lambert(RgbColor.GREEN), // new Phong(RgbColor.RED, new RgbColor(0.8f, 1f, 0.8f), 18, 0.0f,
                                                 // 0.8f, AIR),
                    new Vec3(1f, 0f, 0f),
                    new Vec3(-90, 0, 0),
                    0.03f);

            renderScene.addShape(boxIncludingMesh);*/

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println("Error: somethign went wrong at .obj import");
        }
    }

    private static void setupCornellBox(Scene renderScene) {
        // create 5 plane's that are perpendicular(senkrecht) zu einander
        // 4:3 as Width:Height relation
        // all plane's should have Lambert material
        // all walls should be white
        // except for left(red) and right(blue)
        // there should be a pointlight at the top the box
        // there should be a square to make symbolise the lightsource position

        // left
        renderScene.createPlane(new Vec3(-4, 0, 0), new Vec3(1f, 0f, 0f), new Lambert(RgbColor.RED));
        // right
        renderScene.createPlane(new Vec3(4, 0, 0), new Vec3(-1f, 0f, 0f), new Lambert(RgbColor.BLUE));
        // bot, top, back
        renderScene.createPlane(new Vec3(0, 0, 0), new Vec3(0f, 1f, 0f), new Lambert(RgbColor.WHITE));// , 0.5f, 0f));
        renderScene.createPlane(new Vec3(0, 6, 0), new Vec3(0f, -1f, 0f), new Lambert(RgbColor.WHITE));
        renderScene.createPlane(new Vec3(0, 0, -4), new Vec3(0f, 0f, 1f), new Lambert(RgbColor.WHITE));
        // behind cam
        renderScene.createPlane(new Vec3(0, 0, 6), new Vec3(0f, 0f, -1f), new Lambert(RgbColor.WHITE));
    }

    /**
     * Create our personal renderer and give it all of our items and prefs to
     * calculate our scene
     **/
    private static void raytraceScene(Window renderWindow, Scene renderScene) {
        Raytracer raytracer = new Raytracer(
                renderScene,
                renderWindow,
                RECURSIONS,
                BACKGROUND_COLOR,
                AMBIENT_LIGHT,
                ANTI_ALIASING,
                LIGHT_SAMPLES,
                SHOW_PARAM_LABEL);

        raytracer.renderScene();
    }
}