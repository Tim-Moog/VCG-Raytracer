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

import raytracer.Raytracer;
import ui.Window;
import scene.Scene;
import scene.camera.PerspCam;
import scene.material.Lambert;
import scene.material.LambertLamp;
import scene.material.Material;
import scene.material.Phong;
import utils.RgbColor;
import utils.algebra.Vec2;
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
    static final int ANTI_ALIASING = 1;
    static final boolean USE_SOFT_SHADOWS = false;

    /** LIGHT **/
    static final short LIGHT_DENSITY = 20;
    static final short LIGHT_SAMPLES = 40;

    static final RgbColor BACKGROUND_COLOR = RgbColor.BLACK;

    //DONT SET HEIGHT TO EXACT SAME VALUE AS ROOF PLANE ELSE YOULL SUMMON A DEMON!!!
    static final Vec3 LIGHT_POSITION = new Vec3(0,5.9f,0); 
    static final short AREA_LIGHT_SIZE = 2;

    /** GI **/
    static final boolean USE_GI = false;
    static final int GI_LEVEL = 0;
    static final int GI_SAMPLES = 0;

    static final RgbColor LIGHT_COLOR = null;
    static final RgbColor AMBIENT_LIGHT = new RgbColor(0.1f, 0.1f, 0.15f);

    static final boolean USE_AO = false;
    static final int NUMBER_OF_AO_SAMPLES = 0;
    static final float AO_MAX_DISTANCE = 0f;

    /** CAMERA **/

    static final Vec3 CAM_POS = new Vec3(0,3,6);
    static final Vec3 LOOK_AT = new Vec3(0,3,0);
    static final Vec3 UP_VECTOR = new Vec3(0,1,0);

    static final float VIEW_ANGLE = 70;

    /** DEBUG **/
    //this gets rid of the debug info on the bottom of the window
    static final boolean SHOW_PARAM_LABEL = false;


    /** Initial method. This is where the show begins. **/
    public static void main(String[] args){
        Window renderWindow = new Window(IMAGE_WIDTH, IMAGE_HEIGHT);

        System.out.printf("Hello World! Again!");
        draw(renderWindow);
    }

    /**  Draw the scene using our Raytracer **/
    private static void draw(Window renderWindow){
        Scene renderScene = new Scene();

        setupScene(renderScene);

        raytraceScene(renderWindow, renderScene);
    }

    /** Setup all components that we want to see in our scene **/
    private static void setupScene(Scene renderScene){
        setupCameras(renderScene);

        setupCornellBox(renderScene);

        setupObjects(renderScene);

        setupLights(renderScene);
    }

    private static void setupLights(Scene renderScene) {
        // Lights - task 3
        //renderScene.createPointLight(new Vec3 (-2.5f,2.5f,4), new RgbColor(0.3f,0.7f,0.3f));
        //renderScene.createPointLight(new Vec3 (3f,2.5f,3), new RgbColor(0.7f,0.3f,0.3f));
        //renderScene.createPointLight(new Vec3 (-2.5f,5f,2), new RgbColor(0.3f,0.3f,0.7f));

        // Lights - task 4
        renderScene.createPointLight(LIGHT_POSITION, new RgbColor(0.7f,0.7f,0.7f));


        //testing shadow projection
        //renderScene.createPointLight(new Vec3(50,0,0), RgbColor.WHITE);
    }

    private static void setupCameras(Scene renderScene) {
        PerspCam camera = new PerspCam (CAM_POS,
                                        LOOK_AT,
                                        UP_VECTOR,
                                        VIEW_ANGLE,
                                        (float)IMAGE_WIDTH/IMAGE_HEIGHT);
                                        //casting needed for correct proportions and no we cant change the variables to be type float since that doesnt work with the constructor for Window
        renderScene.setSceneCamera(camera);
    }

    private static void setupObjects(Scene renderScene) {
         // Materials
        Material mat_lambert = new Lambert(new RgbColor(0.6f,0.2f,0.5f));
        Material mat_lambert_shiny = new Lambert(RgbColor.PURPLE,0.6f);

        Material mat_phong = new Phong(RgbColor.PURPLE, new RgbColor(0.8f, 0.8f, 0.8f), 8);
        Material mat_phong_shiny = new Phong(RgbColor.GREEN, new RgbColor(0.8f, 1f, 0.8f), 18, 0.2f);

        Material mat_lamp = new LambertLamp();

        // Objects
        renderScene.createSphere(new Vec3(1.2f,1f,-1), 1f, mat_lambert_shiny);
        renderScene.createSphere(new Vec3(-1f,0.5f,0.1f), 0.5f, mat_phong_shiny);

        //roof  square
        renderScene.createSquare(1, new Vec3(0f, 5.99999f, 0f), new Vec3(0f, -1f, 0f), mat_lamp);

        //testing square
        //renderScene.createSquare(2, new Vec3(-1.5f, 3.5f, 0f), new Vec3(1f, 0.5f, 0.5f), new Phong(RgbColor.RED, new RgbColor(0.8f, 0.8f, 0.8f), 8));
        //renderScene.createCube(1f, new Vec3(-2.5f, 0.5f, -2f),new Phong(RgbColor.RED, new RgbColor(0.8f, 0.8f, 0.8f), 8));
        //renderScene.createCube(1f, new Vec3(-2.5f, 1.5f, -2f),mat_phong);
        //renderScene.createCube(1f, new Vec3(-2.5f, 2.5f, -2f),new Phong(RgbColor.CYAN, new RgbColor(0.8f, 0.8f, 0.8f), 8));
        //renderScene.createCube(1f, new Vec3(-2.5f, 0.5f, -2.5f),mat_shiny);

        //testing shadow projection
        //renderScene.createSphere(new Vec3(1f, 0,0), 0.75f, mat_phong);
        //renderScene.createSphere(new Vec3(-2f, 0,0), 1, mat_phong);
        
    }

    private static void setupCornellBox(Scene renderScene) {
        //create 5 plane's that are perpendicular(senkrecht) zu einander
        //4:3 as Width:Height relation
        //all plane's should have Lambert material
        //all walls should be white 
            //except for left(red) and right(blue)
        //there should be a pointlight at the top the box
            //there should be a square to make symbolise the lightsource position
        

        //left
        renderScene.createPlane(new Vec3(-4,0,0), new Vec3(1f,0f,0f), new Lambert(RgbColor.RED));
        //right
        renderScene.createPlane(new Vec3(4,0,0), new Vec3(-1f,0f,0f), new Lambert(RgbColor.BLUE));
        //bot, top, back
        renderScene.createPlane(new Vec3(0,0,0), new Vec3(0f,1f,0f), new Lambert(RgbColor.WHITE));
        renderScene.createPlane(new Vec3(0,6,0), new Vec3(0f,-1f,0f), new Lambert(RgbColor.WHITE));
        renderScene.createPlane(new Vec3(0,0,-4), new Vec3(0f,0f,1f), new Lambert(RgbColor.WHITE)); 
        //behind cam
        renderScene.createPlane(new Vec3(0,0,6), new Vec3(0f,0f,-1f), new Lambert(RgbColor.WHITE)); 
    }

    /** Create our personal renderer and give it all of our items and prefs to calculate our scene **/
    private static void raytraceScene(Window renderWindow, Scene renderScene){
        Raytracer raytracer = new Raytracer(
                renderScene,
                renderWindow,
                RECURSIONS,
                BACKGROUND_COLOR,
                AMBIENT_LIGHT,
                ANTI_ALIASING,
                SHOW_PARAM_LABEL);

        raytracer.renderScene();
    }
}