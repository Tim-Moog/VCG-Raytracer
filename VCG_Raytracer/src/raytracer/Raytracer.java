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

    private RgbColor shade (Ray _ray, int _depth, Scene _scene) {
        if(_depth >= this.mMaxRecursions) {
            return RgbColor.BLACK;
        }

        Intersection hit = _scene.findClosestIntersection(_ray);

        if (hit == null) {
            return mBackgroundColor;
        }

        Material material = hit.getShape().getMaterial();
        RgbColor color = material.getColor(hit, _ray, _scene, mAmbientLight, _depth);

        if(hit.getOutRay() != null && material.getShinyness() != 0) {
            RgbColor reflectedColor = shade(hit.getOutRay(), _depth+1, _scene);
            color = color.multScalar(1-material.getShinyness()).add(reflectedColor.multScalar(material.getShinyness()));
        }

        return color;
    }
    
}


