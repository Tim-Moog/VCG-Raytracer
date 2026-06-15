/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    1. Send primary ray
    2. intersection test with all shapes
    3. if hit:
    3a: send secondary ray to the light source
    3b: 2
        3b.i: if hit:
            - Shape is in the shade
            - Pixel color = ambient value
        3b.ii: in NO hit:
            - calculate local illumination
    4. if NO hit:
        - set background color

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package raytracer;

import java.awt.image.BufferedImage;

import org.w3c.dom.css.RGBColor;

import scene.Scene;
import scene.camera.*;
import scene.light.Light;
import scene.material.Lambert;
import scene.material.Material;
import scene.material.Phong;
import scene.shape.Intersection;
import scene.shape.Shape;
import scene.shape.Sphere;
import ui.Window;
import utils.RgbColor;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.io.Log;

public class Raytracer {

    private BufferedImage mBufferedImage;

    private Scene mScene;
    private Window mRenderWindow;

    private int mMaxRecursions;

    private RgbColor mBackgroundColor;
    private RgbColor mAmbientLight;

    private int mAntiAliasingSamples;

    private boolean mDebug;
    private long tStart;

    /**  Constructor **/
    public Raytracer(Scene scene, Window renderWindow, int recursions, RgbColor backColor, RgbColor ambientLight, int antiAliasingSamples, boolean debugOn){
        Log.print(this, "Init");
        mMaxRecursions = recursions;

        mBufferedImage = renderWindow.getBufferedImage();

        mAntiAliasingSamples = antiAliasingSamples;

        mBackgroundColor = backColor;
        mAmbientLight = ambientLight;
        mScene = scene;
        mRenderWindow = renderWindow;
        mDebug = debugOn;
        tStart = System.currentTimeMillis();
    }

    /**  Send the created window to the frame delivered by JAVA to display our result **/
    public void exportRendering(){
        mRenderWindow.exportRendering(String.valueOf(stopTime(tStart)), mMaxRecursions, mAntiAliasingSamples, mDebug);
    }

    /**  Stop time of rendering **/
    private static double stopTime(long tStart){
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        return tDelta / 1000.0;
    }

    /**  This is where our scene is actually ray-traced **/
    public void renderScene(){
        Log.print(this, "Prepare rendering at " + String.valueOf(stopTime(tStart)));

        a3ScenePrep();
        //setupCornellBox();
        a3versionTeamPurple();
        System.out.println(mScene.getShapes().size());

        // Here trace the rays ...

        // Primary rays ...

        // Later secondary rays ...

        // Yeeaahhh, raytracing is so much fun ...
        this.exportRendering();
    }


    private void a4ScenePrep(){
        //create cornell box
        setupCornellBox();

        //add in our spheres with Phong material
        

    }

    //think this makes sense to put in its own method since it prob wont change in future
    private void setupCornellBox(){
        //create 5 plane's that are perpendicular(senkrecht) zu einander
        //4:3 as Width:Height relation
        //all plane's should have Lambert material
        //all walls should be white 
            //except for left(red) and right(blue)
        //there should be a pointlight at the top the box
            //there should be a square to make symbolise the lightsource position
        Material mat_lambert2 = new Lambert(RgbColor.WHITE);
        //left
        mScene.createPlane(new Vec3(-5,0,0), new Vec3(1f,0f,0f), new Lambert(RgbColor.RED));
        //right
        mScene.createPlane(new Vec3(5,0,0), new Vec3(-1f,0f,0f), new Lambert(RgbColor.BLUE));
        //bot, top, back
        mScene.createPlane(new Vec3(0,-5,0), new Vec3(0f,1f,0f), new Lambert(RgbColor.WHITE));
        mScene.createPlane(new Vec3(0,5,0), new Vec3(0f,-1f,0f), new Lambert(RgbColor.WHITE));
        mScene.createPlane(new Vec3(0,0,-5), new Vec3(0f,0f,1f), new Lambert(RgbColor.WHITE)); 
        //mScene.createPlane(new Vec3(1,0,0), new Vec3(-1f,0,0), mat_lambert2);
    }


    private void a3ScenePrep(){

        // Materials
        Material mat_lambert = new Lambert(new RgbColor(0.6f,0.2f,0.5f));
        Material mat_lambert2 = new Lambert(new RgbColor(0.8f,0.8f,0.8f));

        Material mat_phong = new Phong(RgbColor.PURPLE, new RgbColor(0.8f, 0.8f, 0.8f), 8);
        Material mat_phong2 = new Phong(RgbColor.GRAY, RgbColor.WHITE, 18);

        // Objects
        mScene.createSphere(new Vec3(1.5f,1,0), 1.25f, mat_phong);
        mScene.createSphere(new Vec3(-1.5f,-0.5f,0), 0.75f, mat_phong2);

        // Lights
        mScene.createPointLight(new Vec3 (-2.5f,2.5f,4), new RgbColor(0.3f,0.7f,0.3f));
        mScene.createPointLight(new Vec3 (3f,2.5f,3), new RgbColor(0.7f,0.3f,0.3f));
        mScene.createPointLight(new Vec3 (-2.5f,5f,2), new RgbColor(0.3f,0.3f,0.7f));
    }

    private void a3versionTeamPurple(){
        Vec2 windowSize = new Vec2 (mBufferedImage.getWidth(), mBufferedImage.getHeight());
        Vec2 currentPixel;

        PerspCam camera = new PerspCam (new Vec3(0,0,5),
                                        new Vec3(0,0,0),
                                        new Vec3(0,1,0),
                                        70,
                                        (windowSize.x/windowSize.y));
        mScene.setSceneCamera(camera);

        for (int y = 0; y < windowSize.y; y++) {
            for (int x = 0; x < windowSize.x; x++) {
                currentPixel = new Vec2(x, y);

                Ray ray = camera.getRayForPixel(currentPixel, windowSize);
                RgbColor outputColor = shade(ray, mScene);

                mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
            }
        }

    }

    private RgbColor shade (Ray _ray, Scene _scene) {

        Intersection hit = _scene.findClosestIntersection(_ray);

        if (hit == null) {
            return mBackgroundColor;
        }

        Material material = hit.getShape().getMaterial();
        RgbColor color = material.getColor(hit, _ray, _scene, mAmbientLight);

        
        return color;
    }
    
}


