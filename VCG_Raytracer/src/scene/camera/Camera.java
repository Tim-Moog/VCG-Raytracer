package scene.camera;

import scene.SceneObject;
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
    private Float focalLength;

    private Vec3 viewVector; // Center of Interest - Camera Position
    private Vec3 sideVector; // viewVektor X User-upVector
    private Vec3 upVector; // viewVektor X sideVector

    public Camera(Vec3 p, Vec3 c, Vec3 g, Float alpha){
        cameraPos_P0 = p;
        centerOfInterest_C = c;
        upVektor_g = g;
        viewAngle = alpha;

        viewVector = centerOfInterest_C.sub(cameraPos_P0);
        //sideVector = 
    }

    // Input: 2D Pixel Plane with coordinate system with origin in the top left corner
    // Translation into our Viewplane for Raytracer - Coordinate System with origin in the middle, size of 2 (normalized)
    public Vec3 calculateDestinationPoint(Vec3 targetPixel){
        //Vec3 direction
        return cameraPos_P0; //remove me since this is for error avoidance
    }



}
