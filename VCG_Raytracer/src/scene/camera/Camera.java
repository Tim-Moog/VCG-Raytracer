package scene.camera;

import raytracer.Ray;
import scene.SceneObject;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public abstract class Camera extends SceneObject{

    protected Vec3 origin; // Startpoint of Camera ("Looking from")
    protected Vec3 view, side, up; // 3 main vectors that make up the camera coordinates

    public Camera(Vec3 cameraPos_P0, Vec3 centerOfInterest_C, Vec3 userUp_g, float alphaDeg){
        this.origin = cameraPos_P0;

        this.view = centerOfInterest_C.sub(cameraPos_P0).normalize();
        this.side = this.view.cross(userUp_g).normalize();
        this.up = this.side.cross(this.view);
    }

    public Vec3 getOrigin(){
        return this.origin;
    }

    // abstract method for perspective & orthogonal camera
    public abstract Ray getRay (float s, float u);

}
