package scene.shape;

import utils.algebra.Vec3;

// Most Basic Task: Does the Ray Hit something?
// -> yes = where? what normal? at what distance? what material?
public class Intersection { //for now ive just added basic setters and getters for everything for us to use from inside of sphere itself when doing the intersection check
    
    //basic IVs from UML diagramm
    private Vec3 intersectionPoint; // hit position
    private Vec3 normal; // surface normal at hit position
    private Shape shape; // knows it's material
    private float t; // distance of ray till intersection
    private boolean hit; // Was Object hit, or just background
    private Vec3 inRay; // Primary Ray
    private Vec3 outRay; // Secondary Ray

    public Intersection () {
        this.hit = false;
        this.t = Float.POSITIVE_INFINITY; // Every Sphere could only be closer to the camera than the default
    }

    // ---- GETTER SETTER ----

    public Vec3 getIntersectionPoint() {
        return intersectionPoint;
    }
    public Vec3 getNormal() {
        return normal;
    }
    public Vec3 getInRay() {
        return inRay;
    }
    public Vec3 getOutRay() {
        return outRay;
    }
    public Shape getShape() {
        return shape;
    }
    public float getDistance() {
        return t;
    }
    public boolean isHit() {
        return hit;
    }
    public void setIntersectionPoint(Vec3 intersectionPoint) {
        this.intersectionPoint = intersectionPoint;
    }
    public void setNormal(Vec3 normal) {
        this.normal = normal;
    }
    public void setInRay(Vec3 inRay) {
        this.inRay = inRay;
    }
    public void setOutRay(Vec3 outRay) {
        this.outRay = outRay;
    }
    public void setShape(Shape shape) {
        this.shape = shape;
    }
    public void setDistance(float distance) {
        this.t = distance;
    }
    public void setHit(boolean hit) {
        this.hit = hit;
    }

    

}
