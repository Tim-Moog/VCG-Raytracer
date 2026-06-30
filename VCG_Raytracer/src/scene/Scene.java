package scene;

import java.util.ArrayList;

import raytracer.Ray;
import scene.camera.Camera;
import scene.light.AreaLight;
import scene.light.Light;
import scene.light.PointLight;
import scene.material.Material;
import scene.shape.Cube;
import scene.shape.Intersection;
import scene.shape.Plane;
import scene.shape.Shape;
import scene.shape.Sphere;
import scene.shape.Square;
import utils.RgbColor;
import utils.algebra.Vec3;
import utils.io.Log;

public class Scene {

    private ArrayList<Shape> shapeList;
    private ArrayList<Light> lightList;
    private Camera sceneCamera;

    public Scene() 
    {
        shapeList = new ArrayList<Shape>();
        lightList = new ArrayList<Light>();
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

    public void createSquare(float _size, Vec3 _pos, Vec3 _normal, Material _Material){
        Square newSquare = new Square(_size, _pos, _normal, _Material);
        shapeList.add(newSquare);
    }

    public void createCube(float _size, Vec3 _pos, Material _Material){
        Square newCube = new Cube(_size, _pos, _Material);
        shapeList.add(newCube);
    }

    //careful dont set size too high else we'll stack a lot
    public void createCubeTower(float _size, Vec3 _pos, Material _mat){
        float current_size = _size;
        float last_size;

        while(current_size > 0.1f){ //dont set this condition too low else you'll stack a loooong time
            createCube(current_size, _pos, _mat);
            last_size = current_size;
            current_size = current_size/1.5f;//how different we want the stacking cubes to be in size
            _pos = new Vec3(_pos.x, _pos.y + last_size/2 + current_size/2, _pos.z);
        }
    }

    public void createPointLight(Vec3 _position, RgbColor _color, float _intensity){
        PointLight newLight = new PointLight(_position, _color, _intensity);
        lightList.add(newLight);
    }

    public void createAreaLight(Scene _scene, short _light_samples, float _size, Vec3 _position, RgbColor _color, float _intensity){
        AreaLight newLight = new AreaLight(_scene, _light_samples, _size, _position, _color, _intensity);
        //no need to add it to the list here since we do that with the inner pointlights
    }

    //this is needed since else we cant add the PointLight to our scene list AND save it in AreaLight
    public void addCornerLights(PointLight[] _lights){
        for (PointLight pointLight : _lights) {
            lightList.add(pointLight);
        }
    }

    public void addShape(Shape addMe){
        shapeList.add(addMe);
    }

    public ArrayList<Shape> getShapes(){
        return shapeList;
    }
    
    public ArrayList<Light> getLights(){
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
                closest.setInRay(ray);
                }
            }
        }
        return closest;
    }

    //this can receive arealights too, so we gotta check if its an arealight
    //i think we should change return type from bool to float and adjust how we handle it
    //pointLight -> in shadow: return 1 // not in shadow: return 0
    //areaLight: return value depends on how many lights are blocked to tell us if its core shadow or not
    //0 corners blocked -> return 0
    //1 corner blocked -> return 0.25f
    //scale up by 0.25f till all corners blcoked = return 1 due to being coreshadow

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
        //distanceToHit = hit.getDistance();

        return distanceToHit < distanceToLight;
    }


}
