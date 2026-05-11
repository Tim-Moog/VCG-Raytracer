package raytracer;

import utils.algebra.Vec3;

public class Ray {

    private Vec3 startpoint_p0;
    private Vec3 direction_D;

    public Ray (Vec3 _startpoint, Vec3 _direction) {
        this.startpoint_p0 = _startpoint;
        this.direction_D = _direction.normalize();
    }
    
    // Ray = startpoint + parameter * direction
    // R = P0 + t * D
    public Vec3 calculateRayAt (Float parameter_t) {
        return startpoint_p0.add(direction_D.multScalar(parameter_t));
    }

    // applies a color to the direction of the ray in a normalized scale [0,1]
    public Vec3 ray_colorRGB () {
        float red   = (direction_D.x + 1f) * 0.5f;
        float green = (direction_D.y + 1f) * 0.5f;
        float blue  = (direction_D.z + 1f) * 0.5f;

        return new Vec3(red, green, blue);
    }

}
