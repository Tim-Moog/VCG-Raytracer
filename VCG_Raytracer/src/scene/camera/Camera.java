package scene.camera;

import java.awt.JobAttributes.SidesType;

import raytracer.Ray;
import scene.SceneObject;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class Camera extends SceneObject{
    
    //need vars for Center of Cam P Vec3
    //Center of Interest C Vec3
    //User Up-Vektor g Vec3 // we provide this one, not the proper one
    //Angle alpha prob einfach int grad zahl
    //FocalLength standard:1 distance from camera to picture
    //AspectRatio desired WindowSize of what we display

    private Vec3 cameraPos_P0;
    private Vec3 centerOfInterest_C;
    private Vec3 user_upVektor_g;
    private Float viewAngle;
    private Float focalLength = 1f;

    private Vec3 viewVector; // Center of Interest - Camera Position
    private Vec3 sideVector; // viewVektor X User-upVector
    private Vec3 upVector; // viewVektor X sideVector

    public Camera(Vec3 p, Vec3 c, Vec3 g, Float alpha){
        cameraPos_P0 = p;
        centerOfInterest_C = c;
        user_upVektor_g = g;
        viewAngle = alpha;

        viewVector = centerOfInterest_C.sub(cameraPos_P0);
        sideVector = viewVector.cross(user_upVektor_g);
        upVector = sideVector.cross(viewVector);
    }

    // Input: 2D Pixel Plane with coordinate system with origin in the top left corner
    // Translation into our Viewplane for Raytracer - Coordinate System with origin in the middle, size of 2 (normalized)
    public Vec3 calculateDestinationPoint(Vec3 targetPixel, Vec2 widthAndHeight){

        //calc xNorm and yNorm
        float xNorm = (float) (2 * (targetPixel.x + 0.5)/widthAndHeight.x - 1);
        float yNorm = (float) (2 * (targetPixel.y + 0.5)/widthAndHeight.y - 1);
        Vec2 pNorm = new Vec2(xNorm, yNorm);
        pNorm = pNorm.normalize();


        //calc aspectRatio and Vhorizontal and Vwidth
        float aspectRatio = widthAndHeight.x / widthAndHeight.y;
        float vHeight = (float) (2 * Math.tan(viewAngle/2) * widthAndHeight.y); 
        float vWidth = (float) (aspectRatio * vHeight);


        //this NEEDS to be in one line else it wont work?
        //Punkt3D = CamPos + V * focalLength + S * Xnorm *Vb/2 + U * Ynorm *Vh/2
        //Vec3 punkt3D = cameraPos_P0.add(viewVector.multScalar(focalLength)).add(sideVector.multScalar(xNorm * (vWidth/2))).add(upVector.multScalar(yNorm * (vHeight/2)));
        Vec3 punkt3D = cameraPos_P0.add(viewVector.multScalar(focalLength)).add(sideVector.multScalar(pNorm.x * (vWidth/2))).add(upVector.multScalar(pNorm.y * (vHeight/2)));

        //debug info - wenn man das nicht auskommentiert hat dann dauerts etwas länger plus wollen ja eh keine milliarde ausgaben auf console
        /* 
        System.out.println("CURRENT PIXEL IS "+ targetPixel.toString());
        System.out.println("xNorm: " +xNorm);
        System.out.println("yNorm: " +yNorm);
        System.out.println("Aspect Ratio: " +aspectRatio);
        System.out.println("vHeight: "+vHeight);
        System.out.println("vWidth: "+vWidth);
        System.out.println("Point3D: " + punkt3D); */


        //trying ray inside----------
        //trying to add ray stuff here
        Ray myRay = new Ray(cameraPos_P0, punkt3D);
        Vec3 maybeRayHit = myRay.calculateRayAt(punkt3D, viewVector, targetPixel, widthAndHeight);
        return maybeRayHit;
        //-----trying ray inside

        //Vec3 direction
        //return punkt3D;
    }

    /*
    hmmmm what do i think we need
    we prob need to calc D in here and then 
    */


}
