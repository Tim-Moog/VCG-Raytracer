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
import scene.material.Material;
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
        //Scene gets prepared inside of main.java
        Log.print(this, "Prepare rendering at " + String.valueOf(stopTime(tStart)));

        //INFO: if we adjust screensize we also gotta change it in main to not fuck over the camera
        Vec2 windowSize = new Vec2 (mBufferedImage.getWidth(), mBufferedImage.getHeight());
        Vec2 currentPixel;

        Camera camera = mScene.getSceneCamera();

        for (int y = 0; y < windowSize.y; y++) {
            for (int x = 0; x < windowSize.x; x++) {
                currentPixel = new Vec2(x, y);

                Ray ray = camera.getRayForPixel(currentPixel, windowSize);
                RgbColor outputColor = shade(ray,0, mScene);

                mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
            }
        }
        // Here trace the rays ...

        // Primary rays ...

        // Later secondary rays ...

        // Yeeaahhh, raytracing is so much fun ...  d=====(￣▽￣*)b
        this.exportRendering();
    }

    /**
     * Handles the Intersection of Rays and the color of that Point including the primary and secondary rays
     * @param _ray Ray currently looked at (primary / secundary)
     * @param _depth Amount of times this Method has been called on in recursion
     * @param _scene Scene that is being traced and displayed
     * @return color of the Point
     */
    private RgbColor shade (Ray _ray, int _depth, Scene _scene) {
        //Method can only can be called so many Times in succession
        if(_depth >= this.mMaxRecursions) {
            return RgbColor.BLACK;
        }

        //The intersected point that is nearest
        Intersection hit = _scene.findClosestIntersection(_ray);

        //if no shape has been hit
        if (hit == null) {
            return mBackgroundColor;
        }

        Material material = hit.getShape().getMaterial();
        //basic coloring
        RgbColor color = material.getColor(hit, _ray, _scene, mAmbientLight, _depth);

        //getOutRay is the ReflectionRay; if one has been set, the material reflects and needs to cast secondaryRays
        if(hit.getOutRay() != null && material.getShinyness() != 0) {
            //recursive shade call for secondaryRays (what color is the environment that is reflected)
            RgbColor reflectedColor = shade(hit.getOutRay(), _depth+1, _scene);
            //shinyness from 0-1 (float) determines how much diffuse / specular coloring is added
            color = color.multScalar(1-material.getShinyness()).add(reflectedColor.multScalar(material.getShinyness()));
        }

        return color;
    }


    /**  This is where our scene is actually ray-traced **/
    public void renderSceneWithAA(int amountAA){
        //for now simply trying this with a variable will prob adjust to a param if it works
        int rowColStepCount = amountAA/2;

        //Scene gets prepared inside of main.java
        Log.print(this, "Prepare rendering at " + String.valueOf(stopTime(tStart)));

        //INFO: if we adjust screensize we also gotta change it in main to not fuck over the camera
        Vec2 windowSize = new Vec2 (mBufferedImage.getWidth(), mBufferedImage.getHeight());
        Vec2 currentPixel;

        Camera camera = mScene.getSceneCamera();

        for (float y = 0; y < windowSize.y; y+=1) {
            for (float x = 0; x < windowSize.x; x+=1) {
                currentPixel = new Vec2(x, y); //we want to adjust the color for each pixel so we keep these as normal pixel coordinates

                //then we prob have to calc color for each subpixel part and then do a mix for output color
                //so we gotta create the color before the rays and then add the results onto em
                RgbColor outputColor = new RgbColor(0,0,0);

                for(float row = 1f/rowColStepCount; row < 1; row += 1f/rowColStepCount){
                    for(float col = 1f/rowColStepCount; col < 1; col += 1f/rowColStepCount){
                        Vec2 adjustedPixel = new Vec2(currentPixel.x + row, currentPixel.y + col);
                        Ray ray = camera.getRayForPixel(adjustedPixel, windowSize);
                        outputColor = outputColor.add(shade(ray,0, mScene).multScalar(1f/amountAA));
                    }
                }
                

                mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
            }
        }
        // Yeeaahhh, raytracing is so much fun ...  d=====(￣▽￣*)b
        this.exportRendering();
    }
    
}


