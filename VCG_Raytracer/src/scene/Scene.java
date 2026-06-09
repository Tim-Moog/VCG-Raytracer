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

    private RgbColor ambient_k;
    private Camera sceneCamera;

    public Camera getSceneCamera() {
        return sceneCamera;
    }

    public void setSceneCamera(Camera sceneCamera) {
        this.sceneCamera = sceneCamera;
    }

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

    public void setAmbientFactor (RgbColor _ambient_k) {
        ambient_k = _ambient_k;
    }

    public RgbColor getAmbientFactor () {
        return this.ambient_k;
    }

    public PointLight getFirstLight() {
        return lightList.getFirst();
    }

    public ArrayList<Shape> getShapes(){
        return shapeList;
    }
    
    public ArrayList<PointLight> getLights(){
        return lightList;
    }

    
    public Intersection findClosestIntersection(Ray ray) {
        Intersection closest = new Intersection();
        for (Shape shape : shapeList) {
            Intersection temp = shape.intersect(ray);
            //System.out.println(temp.isHit());

            if(closest.getShape() == null) //check if there is a shape in already if not we take that as closest
                closest = temp;
            if (temp.isHit() && temp.getDistance() < closest.getDistance()) {
                //System.out.println("SHAPE HIT");
                closest = temp;
            }
        }
        return closest;
    }

}
