package raytracer;

import utils.algebra.Vec3;

public class Ray {

    private Vec3 startpoint;
    private Vec3 direction;

    Ray (Vec3 _startpoint, Vec3 _direction) {
        this.startpoint = _startpoint;
        this.direction = _direction;
    }

    public Vec3 calculateRayAt () {
        Vec3 target;
        target = startpoint.add(direction);
        return target;
    }

}
