package scene.camera;

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
    protected Float viewAngle;
    protected Float focalLength = 1f;
    protected Float aspectRatio;

    protected Vec3 viewVector; // Center of Interest - Camera Position
    protected Vec3 sideVector; // viewVektor X User-upVector
    protected Vec3 upVector; // viewVektor X sideVector

    protected Ray ray;

    protected Vec2 windowSize;

    public Camera(Vec3 cameraPos_P0, Vec3 centerOfInterest_C, Vec3 user_upVektor_g, Float alpha){
        this.cameraPos_P0 = cameraPos_P0;
        this.viewAngle = alpha;

        this.viewVector = centerOfInterest_C.sub(this.cameraPos_P0);
        this.sideVector = this.viewVector.cross(user_upVektor_g);
        this.upVector = this.sideVector.cross(this.viewVector);
    }

    public Camera(Vec3 cameraPos_P0, Vec3 centerOfInterest_C, Vec3 user_upVektor_g, Float alpha, Vec2 windowSize) {
        this(cameraPos_P0, centerOfInterest_C, user_upVektor_g, alpha);
        this.aspectRatio =  windowSize.x / windowSize.y;
        this.windowSize = windowSize;
    }


    // Searching for: ray destination point from window to viewplane
    public Vec2 calculateDestinationPoint_2D (Vec2 targetPixel) {
        // Norm sides from 0...1
        // Double as we need all quadrants -> 0...2
        // Offset by 1 to get correct coordinates -> -1...1
        Float p_norm_x = 2 * ((targetPixel.x + 0.5f) / this.windowSize.x) -1 ;
        Float p_norm_y = 2 * ((targetPixel.y + 0.5f) / this.windowSize.y) -1;

        Vec2 p_norm = new Vec2 (p_norm_x, p_norm_y); // x,y plane
        return p_norm;
    }

}
