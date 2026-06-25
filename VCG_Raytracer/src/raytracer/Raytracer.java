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
import java.util.Random;

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

        //INFO: moved everything into sub methods to adjust based on AA input
        switch(mAntiAliasingSamples){
            case 1:
                //single ray we use the same method as before
                renderScene1xAA();
                break;
            case 4,9,16,25,36,49,64,81,100: //thats sqr root results from 2-10 so should be plenty
                //if AA amount is sqr Root-abl
                //renderSceneAAsqrRootAbleNbr();
                multiThread4x();
                break;
            default:
                //else print error to avoid confusion when nothing happens
                System.err.println("INVALID AA AMOUNT REQUESTED");
        }

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


    /**  This is our default single ray Raytracing **/
    public void renderScene1xAA(){
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
    }


    public void renderSceneAAsqrRootAbleNbr(){
        int samplesPerRowCol = (int)Math.sqrt(mAntiAliasingSamples);
        float subPixelSize = 1f/samplesPerRowCol;

        Vec2 windowSize = new Vec2 (mBufferedImage.getWidth(), mBufferedImage.getHeight());
        Vec2 currentPixel;

        Camera camera = mScene.getSceneCamera();

        //seed for controlled testing
        Random numberGenerator = new Random(67);

        for (float y = 0; y < windowSize.y; y+=1) {
            for (float x = 0; x < windowSize.x; x+=1) {
                currentPixel = new Vec2(x, y); //we want to adjust the color for each pixel so we keep these as normal pixel coordinates

                //then we prob have to calc color for each subpixel part and then do a mix for output color
                //so we gotta create the color before the rays and then add the results onto em
                RgbColor outputColor = new RgbColor(0,0,0);

                for(float row = 0; row < samplesPerRowCol; row++){
                    for(float col = 0; col < samplesPerRowCol; col++){

                        //make new random values for ray position inside of subpixel
                        float randomX = numberGenerator.nextFloat();
                        float randomY = numberGenerator.nextFloat();

                        //get the actual offset of our subpixel using the rdm generated float and the current subpixel position based on AA sample amount
                        float offsetX = (col + randomX) * subPixelSize;
                        float offsetY = (row + randomY) * subPixelSize;

                        //adjust position for the subpixel and send ray there
                        Vec2 adjustedPixel = new Vec2(currentPixel.x + offsetX, currentPixel.y + offsetY);
                        Ray ray = camera.getRayForPixel(adjustedPixel, windowSize);
                        outputColor = outputColor.add(shade(ray,0, mScene).multScalar(1f/mAntiAliasingSamples));
                        //mult the color we add by 1/sample amount cuz we want the sum of all samples to have the same brightness as our usual image
                    }
                }
                

                mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
            }
        }
    }

    public void multiThread4x(){
        Vec2 windowSize = new Vec2(mBufferedImage.getWidth(), mBufferedImage.getHeight());

        RenderThread one = new RenderThread();
        one.setup(new Vec2(0,windowSize.x/2), new Vec2(0, windowSize.y/2));
        one.start();

        RenderThread two = new RenderThread();
        two.setup(new Vec2(0,windowSize.x/2), new Vec2(windowSize.y/2, windowSize.y));
        two.start();

        RenderThread three = new RenderThread();
        three.setup(new Vec2(windowSize.x/2, windowSize.x), new Vec2(0, windowSize.y/2));
        three.start();

        RenderThread four = new RenderThread();
        four.setup(new Vec2(windowSize.x/2, windowSize.x), new Vec2(windowSize.y/2, windowSize.y));
        four.start();

        try {
            //this is to wait for all the threads to be done before we print the info of time it took to render
            //without it it insta printed with 0.006sec rendertime and then got overwritten sdhfgsdfgshdgf
            one.join();
            two.join();
            three.join();
            four.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private class RenderThread extends Thread{
        Vec2 fromToX, fromToY;

        public void setup(Vec2 minmaxX, Vec2 minmaxY){
            this.fromToX = minmaxX;
            this.fromToY = minmaxY;
        }

        public void run() {
            int samplesPerRowCol = (int)Math.sqrt(mAntiAliasingSamples);
            float subPixelSize = 1f/samplesPerRowCol;

            Vec2 windowSize = new Vec2 (mBufferedImage.getWidth(), mBufferedImage.getHeight());
            Vec2 currentPixel;

            Camera camera = mScene.getSceneCamera();

            //seed for controlled testing
            Random numberGenerator = new Random(67);

            for (float y = fromToY.x; y < fromToY.y; y+=1) {
                for (float x = fromToX.x; x < fromToX.y; x+=1) {
                    currentPixel = new Vec2(x, y); //we want to adjust the color for each pixel so we keep these as normal pixel coordinates

                    //then we prob have to calc color for each subpixel part and then do a mix for output color
                    //so we gotta create the color before the rays and then add the results onto em
                    RgbColor outputColor = new RgbColor(0,0,0);

                    for(float row = 0; row < samplesPerRowCol; row++){
                        for(float col = 0; col < samplesPerRowCol; col++){

                            //make new random values for ray position inside of subpixel
                            float randomX = numberGenerator.nextFloat();
                            float randomY = numberGenerator.nextFloat();

                            //get the actual offset of our subpixel using the rdm generated float and the current subpixel position based on AA sample amount
                            float offsetX = (col + randomX) * subPixelSize;
                            float offsetY = (row + randomY) * subPixelSize;

                            //adjust position for the subpixel and send ray there
                            Vec2 adjustedPixel = new Vec2(currentPixel.x + offsetX, currentPixel.y + offsetY);
                            Ray ray = camera.getRayForPixel(adjustedPixel, windowSize);
                            outputColor = outputColor.add(shade(ray,0, mScene).multScalar(1f/mAntiAliasingSamples));
                            //mult the color we add by 1/sample amount cuz we want the sum of all samples to have the same brightness as our usual image
                        }
                    }
                    

                    mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
                }
            }
        }
    }
    
}


