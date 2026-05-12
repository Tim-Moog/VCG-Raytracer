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

    protected Vec3 cameraPos_P0;
    protected Vec3 centerOfInterest_C;
    protected Vec3 user_upVektor_g;
    protected Float viewAngle;
    protected Float focalLength = 1f;

    protected Vec3 viewVector; // Center of Interest - Camera Position
    protected Vec3 sideVector; // viewVektor X User-upVector
    protected Vec3 upVector; // viewVektor X sideVector

    protected Ray myRay; // single ray which we'll use and update for all pixels

    public Camera(Vec3 p, Vec3 c, Vec3 g, Float alpha){
        cameraPos_P0 = p;
        centerOfInterest_C = c;
        user_upVektor_g = g;
        viewAngle = alpha;

        viewVector = centerOfInterest_C.sub(cameraPos_P0);
        sideVector = viewVector.cross(user_upVektor_g);
        upVector = sideVector.cross(viewVector);
    }


    /**
     * This method will calculate the destination point based off of what camera we use
     * we do this for different subclasses by using polymorphism
     * @param targetPixel pixel we want to calculate for
     * @param widthAndHeight screensizes
     * @return calculated pixel in subclasses. In Camera null + error 
     */
    public Vec3 calculateDestinationPoint(Vec3 targetPixel, Vec2 widthAndHeight){
        System.out.println("ERROR: DONT USE CAMERA CLASS AND INSTEAD USE ONE OF ITS SUBCLASSES!");
        System.out.println("USE PERSP OR ORTHO CAM");

        return null;
    }

}
