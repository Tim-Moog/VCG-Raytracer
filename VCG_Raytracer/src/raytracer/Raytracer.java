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
import scene.Scene;
import scene.camera.*;
import scene.material.Material;
import scene.shape.Intersection;
import ui.Window;
import utils.RgbColor;
import utils.algebra.Vec2;
import utils.io.Log;

public class Raytracer {

    private BufferedImage mBufferedImage;

    private Scene mScene;
    private Window mRenderWindow;

    private int mMaxRecursions;

    private RgbColor mBackgroundColor;
    private RgbColor mAmbientLight;

    private int mAntiAliasingSamples;
    private short mLightSamples;

    private boolean mDebug;
    private long tStart;

    /** Constructor **/
    public Raytracer(Scene scene, Window renderWindow, int recursions, RgbColor backColor, RgbColor ambientLight,
            int antiAliasingSamples, short light_samples, boolean debugOn) {
        Log.print(this, "Init");
        mMaxRecursions = recursions;

        mBufferedImage = renderWindow.getBufferedImage();

        mAntiAliasingSamples = antiAliasingSamples;
        mLightSamples = light_samples;

        mBackgroundColor = backColor;
        mAmbientLight = ambientLight;
        mScene = scene;
        mRenderWindow = renderWindow;
        mDebug = debugOn;
        tStart = System.currentTimeMillis();
    }

    /**
     * Send the created window to the frame delivered by JAVA to display our result
     **/
    public void exportRendering() {
        mRenderWindow.exportRendering(String.valueOf(stopTime(tStart)), mMaxRecursions, mAntiAliasingSamples,
                mLightSamples, mDebug);
    }

    /** Stop time of rendering **/
    private static double stopTime(long tStart) {
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        return tDelta / 1000.0;
    }

    /** This is where our scene is actually ray-traced **/
    public void renderScene() {
        // Scene gets prepared inside of main.java
        Log.print(this, "Prepare rendering at " + String.valueOf(stopTime(tStart)));

        // INFO: moved everything into sub methods to adjust based on AA input
        if (Math.sqrt(mAntiAliasingSamples) % 1 == 0) {
            // decider for whether we use mid pixel(1xAA) or rdm position subpixel(any other
            // square rootable number xAA) now happens inside of the threads
            // multiThread4x();
            // multiThread9x();
            int threadcount = Runtime.getRuntime().availableProcessors();
            multiThreadAnyAmountHorizontal(threadcount);
        } else {
            System.err.println("INVALID AA AMOUNT REQUESTED");
            System.err.println("SQUARE ROOT OF VALUE MUST BE A WHOLE NUMBER(1,4,9,16 etc.)");
        }

        // Yeeaahhh, raytracing is so much fun ... d=====(￣▽￣*)b
        this.exportRendering();
    }

    public void multiThreadAnyAmountHorizontal(int threadCount) {
        Vec2 windowSize = new Vec2(mBufferedImage.getWidth(), mBufferedImage.getHeight());

        RenderThread[] threads = new RenderThread[threadCount];
        int sliceHeight = (int) windowSize.y / threadCount;

        for (int i = 0; i < threadCount; i++) {

            int startY = i * sliceHeight;
            int endY = 0;
            //if last thread then end is last Y
            if(i + 1 == threadCount){
                endY = (int) windowSize.y;
            }else{
                //else end is next starting point
                endY = (i + 1) * sliceHeight;
            }
                

            threads[i] = new RenderThread();
            threads[i].setup(new Vec2(0, windowSize.x), new Vec2(startY, endY));
            threads[i].start();
        }

        for (RenderThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the Intersection of Rays and the color of that Point including the
     * primary and secondary rays
     * 
     * @param _ray   Ray currently looked at (primary / secundary)
     * @param _depth Amount of times this Method has been called on in recursion
     * @param _scene Scene that is being traced and displayed
     * @return color of the Point
     */
    private RgbColor shade(Ray _ray, int _depth, Scene _scene) {
        // Method can only can be called so many Times in succession
        if (_depth >= this.mMaxRecursions) {
            return RgbColor.BLACK;
        }

        // The intersected point that is nearest
        Intersection hit = _scene.findClosestIntersection(_ray);

        // if no shape has been hit
        if (hit == null) {
            return mBackgroundColor;
        }

        Material material = hit.getShape().getMaterial();
        // basic coloring
        RgbColor diffuseColor = material.getColor(hit, _scene, _depth);
        RgbColor reflectColor = RgbColor.BLACK;
        float reflectionFactor = material.getReflection();
        RgbColor transmissColor = RgbColor.BLACK;
        float transmissionFactor = material.getTransmission();

        // Reflection
        if (hit.getReflectionRay() != null) {
            reflectColor = shade(hit.getReflectionRay(), _depth + 1, _scene);
        }

        // Transmission
        if (hit.getTransmissionRay() != null) {
            transmissColor = shade(hit.getTransmissionRay(), _depth + 1, _scene);
        }

        RgbColor color = diffuseColor.multScalar(Math.max(0, 1 - reflectionFactor - transmissionFactor))
                .add(reflectColor.multScalar(reflectionFactor))
                .add(transmissColor.multScalar(transmissionFactor));

        return color;
    }

    // this method is a remain of when we didnt use multithreading and did
    // everything here
    /** This is our default single ray Raytracing **/
    public void renderScene1xAA() {
        // INFO: if we adjust screensize we also gotta change it in main to not fuck
        // over the camera
        Vec2 windowSize = new Vec2(mBufferedImage.getWidth(), mBufferedImage.getHeight());
        Vec2 currentPixel;

        Camera camera = mScene.getSceneCamera();

        for (int y = 0; y < windowSize.y; y++) {
            for (int x = 0; x < windowSize.x; x++) {
                currentPixel = new Vec2(x, y);

                Ray ray = camera.getRayForPixel(currentPixel, windowSize);
                RgbColor outputColor = shade(ray, 0, mScene);

                mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
            }
        }
    }

    // this method is a remain of when we didnt do multithreading and did everything
    // here
    public void renderSceneAAsqrRootAbleNbr() {
        int samplesPerRowCol = (int) Math.sqrt(mAntiAliasingSamples);
        float subPixelSize = 1f / samplesPerRowCol;

        Vec2 windowSize = new Vec2(mBufferedImage.getWidth(), mBufferedImage.getHeight());
        Vec2 currentPixel;

        Camera camera = mScene.getSceneCamera();

        // seed for controlled testing
        Random numberGenerator = new Random(67);

        for (float y = 0; y < windowSize.y; y += 1) {
            for (float x = 0; x < windowSize.x; x += 1) {
                currentPixel = new Vec2(x, y); // we want to adjust the color for each pixel so we keep these as normal
                                               // pixel coordinates

                // then we prob have to calc color for each subpixel part and then do a mix for
                // output color
                // so we gotta create the color before the rays and then add the results onto em
                RgbColor outputColor = new RgbColor(0, 0, 0);

                for (float row = 0; row < samplesPerRowCol; row++) {
                    for (float col = 0; col < samplesPerRowCol; col++) {

                        // make new random values for ray position inside of subpixel
                        float randomX = numberGenerator.nextFloat();
                        float randomY = numberGenerator.nextFloat();

                        // get the actual offset of our subpixel using the rdm generated float and the
                        // current subpixel position based on AA sample amount
                        float offsetX = (col + randomX) * subPixelSize;
                        float offsetY = (row + randomY) * subPixelSize;

                        // adjust position for the subpixel and send ray there
                        Vec2 adjustedPixel = new Vec2(currentPixel.x + offsetX, currentPixel.y + offsetY);
                        Ray ray = camera.getRayForPixel(adjustedPixel, windowSize);
                        outputColor = outputColor.add(shade(ray, 0, mScene).multScalar(1f / mAntiAliasingSamples));
                        // mult the color we add by 1/sample amount cuz we want the sum of all samples
                        // to have the same brightness as our usual image
                    }
                }
                mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
            }
        }
    }

    // renders our scene with 4 Threads at once
    public void multiThread4x() {
        Vec2 windowSize = new Vec2(mBufferedImage.getWidth(), mBufferedImage.getHeight());
        // presetup all the x and y ranges to make the Thread setup cleaner
        Vec2[] xRanges = { new Vec2(0, windowSize.x / 2),
                new Vec2(0, windowSize.x / 2),
                new Vec2(windowSize.x / 2, windowSize.x),
                new Vec2(windowSize.x / 2, windowSize.x)
        };

        Vec2[] yRanges = { new Vec2(0, windowSize.y / 2),
                new Vec2(windowSize.y / 2, windowSize.y),
                new Vec2(0, windowSize.y / 2),
                new Vec2(windowSize.y / 2, windowSize.y),
        };

        // start all threads each responsible for their own section of our render
        RenderThread[] threads = new RenderThread[4];
        for (int index = 0; index < 4; index++) {
            threads[index] = new RenderThread();
            threads[index].setup(xRanges[index], yRanges[index]);
            threads[index].start();
        }

        // wait for all threads to be done before printing the info at the bottom of the
        // screen
        for (RenderThread renderThread : threads) {
            try {
                renderThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // renders our scene with 9 Threads at once(assuming the PC running it has that
    // many available)
    public void multiThread9x() {
        Vec2 windowSize = new Vec2(mBufferedImage.getWidth(), mBufferedImage.getHeight());
        // presetup all the x and y ranges to make the Thread setup cleaner
        Vec2[] xRanges = { new Vec2(0, windowSize.x / 3),
                new Vec2(0, windowSize.x / 3),
                new Vec2(0, windowSize.x / 3),
                new Vec2(windowSize.x / 3, (windowSize.x / 3) * 2),
                new Vec2(windowSize.x / 3, (windowSize.x / 3) * 2),
                new Vec2(windowSize.x / 3, (windowSize.x / 3) * 2),
                new Vec2((windowSize.x / 3) * 2, windowSize.x),
                new Vec2((windowSize.x / 3) * 2, windowSize.x),
                new Vec2((windowSize.x / 3) * 2, windowSize.x),
        };

        Vec2[] yRanges = { new Vec2(0, windowSize.y / 3),
                new Vec2(windowSize.y / 3, (windowSize.y / 3) * 2),
                new Vec2((windowSize.y / 3) * 2, windowSize.y),
                new Vec2(0, windowSize.y / 3),
                new Vec2(windowSize.y / 3, (windowSize.y / 3) * 2),
                new Vec2((windowSize.y / 3) * 2, windowSize.y),
                new Vec2(0, windowSize.y / 3),
                new Vec2(windowSize.y / 3, (windowSize.y / 3) * 2),
                new Vec2((windowSize.y / 3) * 2, windowSize.y)
        };

        // start all threads each responsible for their own section of our render
        RenderThread[] threads = new RenderThread[9];
        for (int index = 0; index < 9; index++) {
            threads[index] = new RenderThread();
            threads[index].setup(xRanges[index], yRanges[index]);
            threads[index].start();
        }

        // wait for all threads to be done before printing the info at the bottom of the
        // screen
        for (RenderThread renderThread : threads) {
            try {
                renderThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class RenderThread extends Thread {
        Vec2 fromToX, fromToY;

        public void setup(Vec2 minmaxX, Vec2 minmaxY) {
            this.fromToX = minmaxX;
            this.fromToY = minmaxY;
        }

        public void run() {
            // check if we want multiple samples or not
            // using this method with random position(due to us wanting to implement path
            // tracing) inside of subpixel for 1x AA makes weird results so we use old
            // method for 1x
            if (mAntiAliasingSamples != 1) {
                int samplesPerRowCol = (int) Math.sqrt(mAntiAliasingSamples);
                float subPixelSize = 1f / samplesPerRowCol;

                Vec2 windowSize = new Vec2(mBufferedImage.getWidth(), mBufferedImage.getHeight());
                Vec2 currentPixel;

                Camera camera = mScene.getSceneCamera();

                // seed for controlled testing
                Random numberGenerator = new Random(67);

                for (float y = fromToY.x; y < fromToY.y; y += 1) {
                    for (float x = fromToX.x; x < fromToX.y; x += 1) {
                        currentPixel = new Vec2(x, y); // we want to adjust the color for each pixel so we keep these as
                                                       // normal pixel coordinates

                        // then we prob have to calc color for each subpixel part and then do a mix for
                        // output color
                        // so we gotta create the color before the rays and then add the results onto em
                        RgbColor outputColor = new RgbColor(0, 0, 0);

                        for (float row = 0; row < samplesPerRowCol; row++) {
                            for (float col = 0; col < samplesPerRowCol; col++) {

                                // make new random values for ray position inside of subpixel
                                float randomX = (float) numberGenerator.nextGaussian();
                                float randomY = (float) numberGenerator.nextGaussian();

                                // get the actual offset of our subpixel using the rdm generated float and the
                                // current subpixel position based on AA sample amount
                                float offsetX = (col + randomX) * subPixelSize;
                                float offsetY = (row + randomY) * subPixelSize;

                                // adjust position for the subpixel and send ray there
                                Vec2 adjustedPixel = new Vec2(currentPixel.x + offsetX, currentPixel.y + offsetY);
                                Ray ray = camera.getRayForPixel(adjustedPixel, windowSize);
                                outputColor = outputColor
                                        .add(shade(ray, 0, mScene).multScalar(1f / mAntiAliasingSamples));
                                // mult the color we add by 1/sample amount cuz we want the sum of all samples
                                // to have the same brightness as our usual image
                            }
                        }

                        mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
                    }
                }
            } else {
                // else block is scenario for 1x AA where we shoot in the middle of each pixel
                // to avoid weird looking results

                // INFO: if we adjust screensize we also gotta change it in main to not fuck
                // over the camera
                Vec2 windowSize = new Vec2(mBufferedImage.getWidth(), mBufferedImage.getHeight());
                Vec2 currentPixel;

                Camera camera = mScene.getSceneCamera();

                for (float y = fromToY.x; y < fromToY.y; y++) {
                    for (float x = fromToX.x; x < fromToX.y; x++) {
                        currentPixel = new Vec2(x, y);

                        Ray ray = camera.getRayForPixel(currentPixel, windowSize);
                        RgbColor outputColor = shade(ray, 0, mScene);

                        mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
                    }
                }
            }

        }
    }

}
