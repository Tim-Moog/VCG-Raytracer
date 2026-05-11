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
        raytraceV2();


    
        

        // Here trace the rays ...

        // Primary rays ...

        // Later secondary rays ...

        // Yeeaahhh, raytracing is so much fun ...
        this.exportRendering();
    }


    private void raytraceV2(){
        Camera cam = new PerspCam(
            new Vec3(0, 0, 4), 
            new Vec3(0, 0,0), 
            new Vec3(0, 1, 0),
            70f);

        Vec2 screensize = new Vec2(mBufferedImage.getWidth(), mBufferedImage.getHeight());

        Sphere mySphere = new Sphere(new Vec3(screensize.x/2, screensize.y/2, 0), 3.99f); //idk if local or global coords should be given
        //like soll die kugel bei 0,0 sein oder bei der mitte unseres fensters?

        /*
        wir brauchen koordinaten schnittpunkt auf der kugel von camera aus
        pSurface

        Kugelgleichung x^2+y^2+z^2 = r^2 -bei uns r = 1

        Ray Strahlengleichung
        R = p0 + t * D

            ----
        
        müssen dann die beiden gleichungen gleich setzen und nach t umformen
        A = xD^2 + yD^2 + zD^2 = 1 -> Richtung normalisieren!
        B = 2(x0 *xd + y0 *yd + z0 *zd)
        C = x0^2 + y0^2 + z0^2 - r^2
        t0 = (-B -root(B^2 - 4C))/2
        t1 = (-B +root(B^2 - 4C))/2

        innerhalb von t0 und t1 ist die diskriminante
        d = B^2 - 4C
        check d < 0 -> keine reelle Lösung
            kein schnitt -> no clip dont need to do more
        check d = 0 -> eine reelle Doppellösung
            strahl tangiert die kugel -> genau an ecke der kugel
        check d > 0 -> 2 reelle Lösungen -> 3 optionen
            1. ++ strahl trifft kugel
            2. -- strahl hinter kugel
            3. -+ strahl in Kugel

        
            ----

        schnittpunkt ist der ort wo der strahl die kugel trifft
        Normale dort ist Schnittpunkt - Kugelzentrum -> N = xyz1 - xyzC
        -> normalisiert durch geteilt durch radius

        #hiernach würd an sich noch das ganze transformationen zeug kommen aber that seems complicated rn and i dont think thats needed for this? i hope?
        */
       // Color and Position variables
        Vec3 startPoint = new Vec3(screensize.x/2, screensize.y/2, 0);
        Vec3 normStart = startPoint.normalize();
        Vec3 currentPoint;
        Vec3 direction;
        Vec3 currentPoint3D;
        RgbColor outputColor;
        Vec2 currentPixel;

        for(int x = 1; x < screensize.x; x++){

            for(int y = 1; y < screensize.y; y++){
                //save current pixel position in a Vec2 and Vec3
                currentPixel = new Vec2(x, y);
                currentPoint = new Vec3(x, y, 0);

                //get Pixel position in 3D using the camera
                currentPoint3D = cam.calculateDestinationPoint(currentPoint, screensize);
                Ray eyeRay = new Ray(cam.cameraPos_P0, currentPoint3D);
                Vec3 pixelHitByRay = eyeRay.calculateRayAt(currentPoint3D, cam.viewVector, currentPoint, screensize);
                currentPoint3D = pixelHitByRay;


                //setup direction vector from starting middle point to our current pixel
                direction = currentPoint3D.normalize();


                //prob input sphere stuff here --------------
                if(mySphere.intersect(direction, new Vec3(0,0,4), currentPixel)){
                    outputColor = RgbColor.GREEN;
                    mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
                }else{
                    outputColor = RgbColor.BLUE;
                    mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
                }



                
                /* 
                //readjusting offset!
                direction.x += normStart.x;
                direction.y += normStart.y;
                direction = direction.normalize(); //yes 2nd .normalize is needed!

                //direction = direction.add(new Vec3(0.1f,0,0.2f));
                
                //convert Vec3 to RgbColor
                outputColor = new RgbColor(direction);

                //set the pixel with the adjusted color :)
                mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel); */
            }
        }

    }

    private void raytraceV1(){

        //our camera object
        Camera camera = new PerspCam(
            new Vec3(0, 0, 17),
            new Vec3(0, 0, 0),
            new Vec3(0,1,0),
            170f
        );
        //screensize Info
        Vec2 screensize = new Vec2(mBufferedImage.getWidth(), mBufferedImage.getHeight());


        // Color and Position variables
        Vec3 startPoint = new Vec3(screensize.x/2, screensize.y/2, 0);
        Vec3 normStart = startPoint.normalize();
        Vec3 currentPoint;
        Vec3 direction;
        Vec3 currentPoint3D;
        RgbColor outputColor;


        //this is just a helper for understanding how the numbers look in top left quadrant
        Vec2 currentPixel;
        
        System.out.println("Breite: " + screensize.x + "| Height: "+ screensize.y);
        System.out.println(camera.calculateDestinationPoint(new Vec3(30, 30, 0), screensize).normalize().toString());
        System.out.println(camera.calculateDestinationPoint(new Vec3(100, 100, 0), screensize).normalize().toString());
        System.out.println(camera.calculateDestinationPoint(new Vec3(700, 100, 0), screensize).normalize().toString());

       // /* temporär auskommentiert für testing 
        for(int x = 1; x < screensize.x; x++){

            for(int y = 1; y < screensize.y; y++){
                //save current pixel position in a Vec2 and Vec3
                currentPixel = new Vec2(x, y);
                currentPoint = new Vec3(x, y, 0);

                //get Pixel position in 3D using the camera
                currentPoint3D = camera.calculateDestinationPoint(currentPoint, screensize);
                //currentPoint3D = myRay.calculateRayAt(currentPoint3D);

                //setup direction vector from starting middle point to our current pixel
                //direction = currentPoint3D.sub(startPoint);
                //direction = direction.normalize(); 
                direction = currentPoint3D.normalize();

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

                //direction = direction.add(new Vec3(0.1f,0,0.2f));
                
                //convert Vec3 to RgbColor
                outputColor = new RgbColor(direction);

                //set the pixel with the adjusted color :)
                mRenderWindow.setPixel(mBufferedImage, outputColor, currentPixel);
            }
        }  //end outcomment for testing here :p
    }




    //Below this are the methods from the first praktikum
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


