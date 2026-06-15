package scene;

import java.util.ArrayList;

import raytracer.Ray;
import scene.camera.Camera;
import scene.camera.PerspCam;
import scene.light.Light;
import scene.light.PointLight;
import scene.material.Lambert;
import scene.material.Material;
import scene.shape.Intersection;
import scene.shape.Plane;
import scene.shape.Shape;
import scene.shape.Sphere;
import utils.RgbColor;
import utils.algebra.Vec3;
import utils.io.Log;

public class Scene {

    private ArrayList<Shape> shapeList;
    private ArrayList<PointLight> lightList;
    private Camera sceneCamera;

    public Scene() 
    {
        shapeList = new ArrayList<Shape>();
        lightList = new ArrayList<PointLight>();
        Log.print(this, "Init");
    }

    public void createPlane(Vec3 _pos, Vec3 _norm, Material _mat){
        Plane newPlane = new Plane(_pos, _norm, _mat);
        shapeList.add(newPlane);
    }

    public void createSphere(Vec3 _pos, float _radius, Material _material){
        // unit circle, transformation can be added later
        Sphere newSphere = new Sphere(_pos, _radius, _material);
        shapeList.add(newSphere);
    }

    public void createPointLight(Vec3 _position, RgbColor _color){
        PointLight newLight = new PointLight(_position, _color);
        lightList.add(newLight);
    }

    public ArrayList<Shape> getShapes(){
        return shapeList;
    }
    
    public ArrayList<PointLight> getLights(){
        return lightList;
    }

    public Camera getSceneCamera() {
        return sceneCamera;
    }

    public void setSceneCamera(Camera sceneCamera) {
        this.sceneCamera = sceneCamera;
    }
    
    public Intersection findClosestIntersection(Ray ray) {
        // Start with null, incase we never hit anything
        Intersection closest = null;
        for (Shape shape : shapeList) {
            // Creates a temporary hit check
            Intersection hit = shape.intersect(ray);

            // Checks, if a Shape was even hit
            if(hit != null && hit.isHit()) {
                // Only sets the Clostest hit if Closest is yet empty, or a hit that is nearer is found
                if (closest == null || hit.getDistance() < closest.getDistance()) {
                closest = hit;
                }
            }
        }
        return closest;
    }

    public boolean isInShadow(Intersection _intersection, Light _light) {

        // intersection towards light
        Vec3 vToLight = _light.getPosition().sub(_intersection.getIntersectionPoint()).normalize();
        // offset to prevent ray starting inside the object
        Vec3 origin = _intersection.getIntersectionPoint().add(vToLight.multScalar(0.01f));
        Ray shadowRay = new Ray(origin, vToLight);
        Intersection hit = this.findClosestIntersection(shadowRay);

        // Nothing hit or own shape hit -> no shadow
        if (hit == null) {
            return false;
        }

        // only if object is infront of light -> shadow
        float distanceToLight = _light.getPosition().sub(origin).length();
        float distanceToHit = hit.getIntersectionPoint().sub(origin).length();

        return distanceToHit < distanceToLight;
    }

}
