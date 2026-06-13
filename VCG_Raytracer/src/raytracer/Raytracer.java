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

        //colorGradient();
        //colorCircle();
        //raytrace_backgroundOnly();
        //circle_radius1();
        //a3_Beleuchtung();
        a3ScenePrep();
        setupCornellBox();
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

        //create materials
        Material mat_lambert = new Lambert(new RgbColor(0.6f,0.2f,0.5f));
        Material mat_lambert2 = new Lambert(new RgbColor(0.8f,0.8f,0.8f));

        Material mat_phong = new Phong(RgbColor.PURPLE, new RgbColor(0.8f, 0.8f, 0.8f), 8);
        Material mat_phong2 = new Phong(RgbColor.GRAY, RgbColor.WHITE, 18);


        //create spheres
        mScene.createSphere(new Vec3(1.5f,1,0), 1.25f, mat_phong);
        mScene.createSphere(new Vec3(-1.5f,-0.5f,0), 0.75f, mat_phong2);


        //mScene.createSphere(new Vec3(1f,1,-2.5f), 1.25f, mat_phong); // -> testing shadow 25
        //mScene.createSphere(new Vec3(-2f,1f,-2.5f), 0.75f, mat_phong2);

        //create lights
        //mScene.createPointLight(new Vec3(-4,1,-0f), RgbColor.WHITE); //->testing shadow 25
        mScene.createPointLight(new Vec3 (-2.5f,2.5f,4), new RgbColor(0.3f,0.7f,0.3f));
        //mScene.createPointLight(new Vec3 (2.5f,2.5f,4), new RgbColor(0.7f,0.3f,0.3f));
        //mScene.createPointLight(new Vec3(0, -2.5f, 4), new RgbColor(0.3f,0.3f,0.7f));
        mScene.setAmbientFactor(new RgbColor(0.1f,0.1f,0.1f));
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

        Intersection clostest_intersection;

        for (int y = 0; y < windowSize.y; y++) {
            for (int x = 0; x < windowSize.x; x++) {
                currentPixel = new Vec2(x, y);

                Ray ray = camera.getRayForPixel(currentPixel, windowSize);

                //create intersection data and update it based on our sphere and ray
                clostest_intersection = mScene.findClosestIntersection(ray);
                

                RgbColor outputColor;
                if (clostest_intersection.isHit()) {
                    //System.out.println("SHAPE HIT");
                    outputColor = mScene.shade(clostest_intersection);
                } else {
                    outputColor = mScene.getAmbientFactor();
                }

                //set the pixel with the adjusted color :)
                mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
            }
        }

    }

    private void a3_Beleuchtung(){
        Vec2 windowSize = new Vec2 (mBufferedImage.getWidth(), mBufferedImage.getHeight());
        Vec2 currentPixel;

        PerspCam camera = new PerspCam (new Vec3(0,0,5),
                                        new Vec3(0,0,0),
                                        new Vec3(0,1,0),
                                        70,
                                        (windowSize.x/windowSize.y));


                                
        for (int y = 0; y < windowSize.y; y++) {
            for (int x = 0; x < windowSize.x; x++) {
                currentPixel = new Vec2(x, y);
                RgbColor outputColor = RgbColor.BLUE;

                //run a for each loop over our shapelist //we'll prob have another foreach loop in this or around later for all lights
                for (Shape currentShape : mScene.getShapes()) {
                    Ray ray = camera.getRayForPixel(currentPixel, windowSize);
                    Intersection currentIntersection;
                    currentShape.intersect(ray);
                    currentIntersection = currentShape.getIntersectionData();
                    if(currentIntersection.isHit())
                        outputColor = mScene.shade(currentIntersection); //shade method instead
                } 

                //set the pixel with the adjusted color :)
                mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
            }
        }
    }
    
    /* auskommentiert to avoid errors from changed code in inner workings 
    private void circle_radius1 () {

        Vec2 windowSize = new Vec2 (mBufferedImage.getWidth(), mBufferedImage.getHeight());
        Vec2 currentPixel;

        PerspCam camera = new PerspCam (new Vec3(0,0,4),
                                        new Vec3(0,0,0),
                                        new Vec3(0,1,0),
                                        70,
                                        (windowSize.x/windowSize.y)
                                        );
        
        Sphere mySphere = new Sphere(new Vec3(0,0,0), 1);
        Intersection intersectionData;

        for (int y = 0; y < windowSize.y; y++) {
            for (int x = 0; x < windowSize.x; x++) {
                currentPixel = new Vec2(x, y);

                Ray ray = camera.getRayForPixel(currentPixel, windowSize);

                //get the intersection info of our sphere object
                intersectionData = new Intersection();
                mySphere.intersect(intersectionData, ray);


                //check if we hit the sphere make pixel green, else make it blue
                RgbColor outputColor;
                if(intersectionData.isHit()){
                    outputColor = RgbColor.GREEN;
                }else{
                    outputColor = RgbColor.BLUE;
                }
                

                //set the pixel with the adjusted color :)
                mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
            }
        }
    } */

    private void raytrace_backgroundOnly () {

        Vec2 windowSize = new Vec2 (mBufferedImage.getWidth(), mBufferedImage.getHeight());
        Vec2 currentPixel;

        PerspCam camera = new PerspCam (new Vec3(0,0,17),
                                        new Vec3(0,0,0),
                                        new Vec3(0,1,0),
                                        170,
                                        (windowSize.x/windowSize.y)
                                        );

        for (int y = 0; y < windowSize.y; y++) {
            for (int x = 0; x < windowSize.x; x++) {
                currentPixel = new Vec2(x, y);

                Ray ray = camera.getRayForPixel(currentPixel, windowSize);
                Vec3 ray_RGB_values = ray.ray_colorRGB();

                //convert Vec3 to RgbColor
                RgbColor outputColor = new RgbColor(ray_RGB_values);

                //set the pixel with the adjusted color :)
                mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
            }
        }
    }



    private void colorGradient(){
        // Base Color
        int windowWidth = mBufferedImage.getWidth();
        int windowHeight = mBufferedImage.getHeight();

        Vec2 currentPixel;
        RgbColor currentGradiantRGB;
        float currentGradiant;

        for(int x = 1; x < windowWidth; x++){

            currentGradiant = (float) (x) / windowWidth;
            currentGradiantRGB = new RgbColor((1f-currentGradiant), 0f, currentGradiant);

            for(int y = 1; y < windowHeight; y++){

                currentPixel = new Vec2(x, y);
                mRenderWindow.setPixel(mBufferedImage, currentGradiantRGB, currentPixel);

            }
        }
    }


    private void colorCircle(){
    // Screen Info
        int windowWidth = mBufferedImage.getWidth();
        int windowHeight = mBufferedImage.getHeight();

        // Color and Position variables
        Vec3 startPoint = new Vec3(windowWidth/2, windowHeight/2, 0);
        Vec3 normStart = startPoint.normalize();
        Vec3 currentPoint;
        Vec3 direction;
        RgbColor outputColor;


        //this is just a helper for understanding how the numbers look in top left quadrant
        Vec2 currentPixel;
        System.out.println(new Vec3(10, 10, 0).sub(startPoint).normalize().toString());

        for(int x = 1; x < windowWidth; x++){

            for(int y = 1; y < windowHeight; y++){
                //save current pixel position in a Vec2 and Vec3
                currentPixel = new Vec2(x, y);
                currentPoint = new Vec3(x, y, 0);
                //setup direction vector from starting middle point to our current pixel
                direction = currentPoint.sub(startPoint);
                direction = direction.normalize(); 
                

                //adjust z depending on position in correlation to middlepoint
                if(direction.x < 0 && direction.y < 0){
                    direction.z += direction.x * -1 + direction.y *-1;
                }else{
                    if(direction.x < 0){
                        direction.z += direction.x *-1;
                    }
                    if(direction.y < 0){
                        direction.z += direction.y *-1;
                    }
                }

                //readjusting offset!
                direction.x += normStart.x;
                direction.y += normStart.y;
                direction = direction.normalize(); //yes 2nd .normalize is needed!

                direction = direction.add(new Vec3(0.1f,0,0.2f));
                
                //convert Vec3 to RgbColor
                outputColor = new RgbColor(direction);

                //set the pixel with the adjusted color :)
                mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
            }
        }
    }
}


