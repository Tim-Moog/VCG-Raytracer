package raytracer;

import utils.algebra.Vec3;

public class Ray {

    private Vec3 startpoint;
    private Vec3 direction;
    private Vec3 ray_vector;

    public Ray (Vec3 _startpoint, Vec3 _direction) {
        this.startpoint = _startpoint;
        this.set_direction(_direction);
    }

    public Ray (Vec3 _startpoint) {
        this.startpoint = _startpoint;
        set_direction(new Vec3(0,0,0));
    }




    public void set_direction (Vec3 _direction) {
        this.direction = _direction;
    }

    public void set_startpoint (Vec3 _startpoint) {
        this.startpoint = _startpoint;
    }

    public Vec3 get_normalized_ray () {
        this.normalize();
        return ray_vector;
    }

    
        
    public Vec3 calculateRayAt () {
        this.ray_vector = startpoint.add(direction);
        this.ray_vector = ray_vector.normalize();
        return this.ray_vector;
    }

    public void normalize () {
        this.calculateRayAt();
        this.ray_vector.normalize();
    }

}
