package scene.shape;

import raytracer.Ray;
import scene.material.Material;
import utils.algebra.Matrix4x4;
import utils.algebra.Vec3;
import utils.algebra.Vec4;

public class Plane extends Shape {

    Vec3 normal;
    Float A,B,C, Q;

    public Plane(Vec3 _pos, Vec3 _normal, Material _material) {
        super(_pos, _material);
        //i think this is the only extra thing we need for a plane?
        this.normal = _normal.normalize();
        //ABC UND Q SETZEN
        A = this.normal.x;
        B = this.normal.y;
        C = this.normal.z;

        //Q needs to be constant based off of the normal and plane position
        Q = -normal.scalar(position);
    }

    public Intersection intersect(Ray ray){
        Intersection intersection = new Intersection();

        Vec3 ray_origin = ray.getOrigin();
        Vec3 ray_direction = ray.getDirection();
        
        float check = normal.scalar(ray_direction);
        float t = -(A * ray_origin.x + B * ray_origin.y + C * ray_origin.z + Q)/(A * ray_direction.x + B * ray_direction.y + C * ray_direction.z);
        //think should be the same as
        t = -(normal.scalar(ray_origin) + Q )/ normal.scalar(ray_direction);

        //check < 0 to see if we are hitting the plane from the front -> as long as we dont have a plane between a shape and a light this should be fine
        intersection.setHit(check < 0); 
        intersection.setShape(this);
        intersection.setNormal(normal);
        intersection.setDistance(ray.calculateRayAt(t).length());
        intersection.setIntersectionPoint(ray_origin.add(ray_direction.multScalar(t)));

        return intersection;
    }
    
}
