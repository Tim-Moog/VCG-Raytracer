package scene.camera;

import raytracer.Ray;
import scene.SceneObject;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

// Abstract class, sets up the camera coordinates + one method for implementation
public abstract class Camera extends SceneObject{

    protected Vec3 origin; // Startpoint of Camera ("Looking from")
    protected Vec3 view, side, up; // 3 main vectors that make up the camera coordinates

    // alpha isn't needed here necessarily, but thats what the task said to implement and we obey
    public Camera(Vec3 cameraPos_P0, Vec3 centerOfInterest_C, Vec3 userUp_g, float alphaDeg){
        this.origin = cameraPos_P0;

        // Calculating camera coordinate system with our 3 vectors
        this.view = centerOfInterest_C.sub(cameraPos_P0).normalize(); // v = C - P0
        this.side = this.view.cross(userUp_g).normalize(); // s = v x g
        this.up = this.side.cross(this.view); // u = s x v
    }

    // abstract method for perspective & orthogonal camera
    public abstract Ray getRay (float s, float u);

}
