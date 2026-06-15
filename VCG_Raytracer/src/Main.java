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
import scene.material.Lambert;
import scene.material.Material;
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

    static final int RECURSIONS = 0;
    static final int ANTI_ALIASING = 1;
    static final boolean USE_SOFT_SHADOWS = false;

    /** LIGHT **/
    static final short LIGHT_DENSITY = 20;
    static final short LIGHT_SAMPLES = 40;

    static final RgbColor BACKGROUND_COLOR = RgbColor.BLACK;

    static final Vec3 LIGHT_POSITION = null;
    static final short AREA_LIGHT_SIZE = 2;

    /** GI **/
    static final boolean USE_GI = false;
    static final int GI_LEVEL = 0;
    static final int GI_SAMPLES = 0;

    static final RgbColor LIGHT_COLOR = null;
    static final RgbColor AMBIENT_LIGHT = null;

    static final boolean USE_AO = false;
    static final int NUMBER_OF_AO_SAMPLES = 0;
    static final float AO_MAX_DISTANCE = 0f;

    /** CAMERA **/

    static final Vec3 CAM_POS = null;
    static final Vec3 LOOK_AT = null;
    static final Vec3 UP_VECTOR = null;

    static final float VIEW_ANGLE = 0;

    /** DEBUG **/

    static final boolean SHOW_PARAM_LABEL = true;


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
    }

    private static void setupCameras(Scene renderScene) {
    }

    private static void setupObjects(Scene renderScene) {
    }

    private static void setupCornellBox(Scene renderScene) {
        //create 5 plane's that are perpendicular(senkrecht) zu einander
        //4:3 as Width:Height relation
        //all plane's should have Lambert material
        //all walls should be white 
            //except for left(red) and right(blue)
        //there should be a pointlight at the top the box
            //there should be a square to make symbolise the lightsource position
        Material mat_lambert2 = new Lambert(RgbColor.WHITE);
        //left
        renderScene.createPlane(new Vec3(-5,0,0), new Vec3(1f,0f,0f), new Lambert(RgbColor.RED));
        //right
        renderScene.createPlane(new Vec3(5,0,0), new Vec3(-1f,0f,0f), new Lambert(RgbColor.BLUE));
        //bot, top, back
        renderScene.createPlane(new Vec3(0,-5,0), new Vec3(0f,1f,0f), new Lambert(RgbColor.WHITE));
        renderScene.createPlane(new Vec3(0,5,0), new Vec3(0f,-1f,0f), new Lambert(RgbColor.WHITE));
        renderScene.createPlane(new Vec3(0,0,-5), new Vec3(0f,0f,1f), new Lambert(RgbColor.WHITE)); 
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