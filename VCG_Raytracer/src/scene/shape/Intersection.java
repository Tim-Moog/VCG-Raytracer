package scene.shape;

import utils.algebra.Vec3;

public class Intersection { //for now ive just added basic setters and getters for everything for us to use from inside of sphere itself when doing the intersection check
    
    //basic IVs from UML diagramm
    private Vec3 intersectionPoint;
    private Vec3 normal;
    private Vec3 inRay;
    private Vec3 outRay;
    private Shape shape;
    private float distance;
    private boolean hit;

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
        return distance;
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
        this.distance = distance;
    }
    public void setHit(boolean hit) {
        this.hit = hit;
    }

    

}
