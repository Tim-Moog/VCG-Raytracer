package scene.camera;

import scene.SceneObject;
import utils.algebra.Vec3;

public class Camera extends SceneObject{
    
    //need vars for Center of Cam P Vec3
    //Center of Interest C Vec3
    //User Up-Vektor g Vec3
    //Angle alpha prob einfach int grad zahl

    private Vec3 cameraPos_P0;
    private Vec3 centerOfInterest_C;
    private Vec3 upVektor_g;
    private Float viewAngle;
    private Float focalLength;

    public Camera(Vec3 p, Vec3 c, Vec3 g, Float alpha){
        cameraPos_P0 = p;
        centerOfInterest_C = c;
        upVektor_g = g;
        viewAngle = alpha;
    }

    //future method from UML
    public Vec3 calculateDestinationPoint(Vec3 targetPixel){
        //Vec3 direction
        return cameraPos_P0; //remove me since this is for error avoidance
    }



}
