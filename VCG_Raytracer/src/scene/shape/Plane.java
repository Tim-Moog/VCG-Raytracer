package scene.shape;

import raytracer.Ray;
import scene.material.Material;
import utils.algebra.Vec3;

public class Plane extends Shape {

    Vec3 normal;
    Float A,B,C, Q;

    public Plane(Vec3 _position, Vec3 _normal, Material _material) {
        super(_position, _material);
        //i think this is the only extra thing we need for a plane?
        this.normal = _normal;
        //ABC UND Q SETZEN
        A = this.normal.x;
        B = this.normal.y;
        C = this.normal.z;

        //Q ist Abstand zum Ursprung -> prob makes more sense to set in intersect method as it will differ for each point of the plane
        
    }




    public Intersection intersect(Ray ray){
        Intersection intersection = new Intersection();


        Vec3 ray_origin = ray.getOrigin();
        Vec3 ray_direction = ray.getDirection();

        Q = ray_direction.sub(ray_origin).length();
        

        //idk if this is whats meant with the D in der formel
        float diskriminante = (float) (Math.pow(B, 2) - 4 * C);
        float t = -(A * ray_origin.x + B * ray_origin.y + C * ray_origin.z + Q)/(A * ray_direction.x + B * ray_direction.y + C * ray_direction.z);

        intersection.setHit(t > 0);
        intersection.setShape(this);
        intersection.setNormal(normal);
        intersection.setDistance(t);
        intersection.setInRay(ray.calculateRayAt(t));
        intersection.setIntersectionPoint(ray_origin.add(ray_direction.multScalar(t)));

        return intersection;
    }

    
    
}
