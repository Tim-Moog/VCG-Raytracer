package scene.shape;

import raytracer.Ray;
import utils.algebra.Vec3;

public class Intersection { //for now ive just added basic setters and getters for everything for us to use from inside of sphere itself when doing the intersection check
    
    //basic IVs from UML diagramm
    private Vec3 intersectionPoint;
    private Vec3 normal;
    private Ray inRay;
    private Ray outRay;
    private Ray reflectionRay;
    private Ray transmissionRay;
    private float fresnel;
    private Shape shape;
    private float distance;
    private boolean hit;

    public Ray getReflectionRay() {
        return reflectionRay;
    }
    public void setReflectionRay(Ray reflectionRay) {
        this.reflectionRay = reflectionRay;
    }
    public Ray getTransmissionRay() {
        return transmissionRay;
    }
    public void setTransmissionRay(Ray transmissionRay) {
        this.transmissionRay = transmissionRay;
    }
    public Vec3 getIntersectionPoint() {
        return intersectionPoint;
    }
    public Vec3 getNormal() {
        return normal;
    }
    public Ray getInRay() {
        return inRay;
    }
    public Ray getOutRay() {
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
    public void setInRay(Ray inRay) {
        this.inRay = inRay;
    }
    public void setOutRay(Ray outRay) {
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
    public float getFresnel() {
        return fresnel;
    }
    public void setFresnel(float fresnel) {
        this.fresnel = fresnel;
    }

    

}
