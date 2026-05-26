package scene.shape;

import raytracer.Ray;
import scene.material.Material;
import utils.algebra.Vec3;

public class Sphere extends Shape{

    private float radius;

    public Sphere(Vec3 _position, float _radius, Material _material) {
        super(_position, _material);
        this.radius = _radius;
    }

    public Intersection intersect (Ray ray) {
        Intersection intersection = new Intersection();

        Vec3 ray_origin = ray.getOrigin();
        Vec3 ray_direction = ray.getDirection();

        //A = (float) Math.pow(checkRay.x, 2) + (float) Math.pow(checkRay.y, 2) + (float) Math.pow(checkRay.z, 2);
        //ist das selbe wie
        Float a = ray_direction.scalar(ray_direction);
        //B = (float) (2 * (camPos.x * checkRay.x + camPos.y * checkRay.y + camPos.z * checkRay.z));
        //ist das selbe wie
        Float b = 2.0f * ray_origin.scalar(ray_direction);
        //C = (float) (Math.pow(camPos.x,2) + Math.pow(camPos.y,2) + Math.pow(camPos.z,2) - Math.pow(radius,2));
        //ist das selbe wie
        Float c = ray_origin.scalar(ray_origin) - radius * radius;

        float diskriminante = (float) (Math.pow(b, 2) - 4 * c);
        
        // Did we hit a sphere in line of sight?
        intersection.setHit(diskriminante >= 0);
        // Stop if we did not hit, or hit is behind camera
        if(diskriminante < 0)
            return intersection;

        //calculating t's
        float t0 = (float) (- b - Math.sqrt(diskriminante))/2;
        float t1 = (float) (- b + Math.sqrt(diskriminante))/2;

        // Deciding, which t is first contact infront of us
        float t = -1;
        if (t0 > 0 && t1 > 0) {
            // Smaller one is chosen, closer to camera
            t = Math.min(t0, t1);
        } else if (t0 > 0) {
            // The one that is positive is chosen
            t = t0;
        } else if (t1 > 0) {
            t = t1;
        }

        // Giving over information to Intersection
        // Hit is already set
        intersection.setDistance(t);
        intersection.setIntersectionPoint(ray_origin.add(ray_direction.multScalar(t))); // Ray start, into normalized direction * distance
        intersection.setNormal(intersection.getIntersectionPoint().sub(position).normalize()); // Vector between intersection and sphere center = normal
        intersection.setShape(this); // for material
        intersection.setInRay(ray.calculateRayAt(t)); // primary Ray

        return intersection;
    }

    public Material getMaterial () {
        return this.material;
    }
    
}
