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

    static final int RECURSIONS = 8;
    static final int ANTI_ALIASING = (int) Math.pow(1, 2); // only valid AA values are 1 and any number whose sqr root
                                                           // results a whole number
    static final boolean USE_SOFT_SHADOWS = false;

    /** LIGHT **/
    static final short LIGHT_DENSITY = 20;
    static final short LIGHT_SAMPLES = (short) Math.pow(1, 2);// only valid AA values are 1 and any number whose sqr
                                                              // root results a whole number

    static final RgbColor BACKGROUND_COLOR = RgbColor.BLACK;

    // DONT SET HEIGHT TO EXACT SAME VALUE AS ROOF PLANE ELSE YOULL SUMMON A
    // DEMON!!!
    // careful: now that the square is set inside of AreaLight be careful not to put
    // the light too close to the ceiling, else the square might get placed outside
    // our cornell box
    static final Vec3 LIGHT_POSITION = new Vec3(0, 5.9f, -1);
    static final short AREA_LIGHT_SIZE = 1;

    /** GI **/
    static final boolean USE_GI = false;
    static final int GI_LEVEL = 0;
    static final int GI_SAMPLES = 0;

    static final RgbColor LIGHT_COLOR = new RgbColor(0.8f, 0.8f, 0.8f);
    static final RgbColor AMBIENT_LIGHT = new RgbColor(0.12f, 0.1f, 0.15f);

    static final boolean USE_AO = false;
    static final int NUMBER_OF_AO_SAMPLES = 0;
    static final float AO_MAX_DISTANCE = 0f;

    /** CAMERA **/

    static final Vec3 CAM_POS = new Vec3(0, 3, 6);
    static final Vec3 LOOK_AT = new Vec3(0, 3, 0);
    static final Vec3 UP_VECTOR = new Vec3(0, 1, 0);

    static final float VIEW_ANGLE = 60;

    /** MATERIAL - REFRECTIVE INDICES **/

    protected static final float AIR = 1.0f;
    protected static final float WATER = 1.33f;
    protected static final float GLASS = 1.5f;
    protected static final float DIAMOND = 2.4f;

    /** DEBUG **/
    // this gets rid of the debug info on the bottom of the window
    static final boolean SHOW_PARAM_LABEL = false;

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

        //setupExtras(renderScene); //dont enable this unless you want slooooowwwwwww load

        setupLights(renderScene);
    }

    private static void setupLights(Scene renderScene) {

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
        renderScene.createPlane(new Vec3(-4, 0, 0), new Vec3(1f, 0f, 0f), new Lambert(RgbColor.RED, 0f, 0));
        // right
        renderScene.createPlane(new Vec3(4, 0, 0), new Vec3(-1f, 0f, 0f), new Lambert(RgbColor.BLUE, 0f, 0));
        // bot, top, back
        renderScene.createPlane(new Vec3(0, 0, 0), new Vec3(0f, 1f, 0f), new Lambert(RgbColor.WHITE, 0f, 0f));//, 0.5f, 0f));
        renderScene.createPlane(new Vec3(0, 6, 0), new Vec3(0f, -1f, 0f), new Lambert(RgbColor.WHITE));
        renderScene.createPlane(new Vec3(0, 0, -4), new Vec3(0f, 0f, 1f), new Lambert(RgbColor.WHITE, 0f, 0));
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