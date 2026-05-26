package scene;

import java.util.ArrayList;

import raytracer.Ray;
import scene.camera.PerspCam;
import scene.light.Light;
import scene.light.PointLight;
import scene.material.Lambert;
import scene.material.Material;
import scene.shape.Intersection;
import scene.shape.Shape;
import scene.shape.Sphere;
import utils.RgbColor;
import utils.algebra.Vec3;
import utils.io.Log;

public class Scene {

    private ArrayList<Shape> shapeList;
    private ArrayList<PointLight> lightList;

    private PerspCam perspCam;
    private RgbColor ambient_k;

    public Scene() 
    {
        shapeList = new ArrayList<Shape>();
        lightList = new ArrayList<PointLight>();
        Log.print(this, "Init");
    }

    public Scene(PerspCam _perspCam) {
        // For our Task 3 specified
        this();
        addPerspCam(_perspCam);
        Material mat_lambert = new Lambert(new RgbColor(0.8f,0.8f,0.8f));
        createSphere(new Vec3(0,0,0), 1, mat_lambert);
        //createSphere(new Vec3(1.5f,1,0), 1.25f, mat_lambert);
        //createSphere(new Vec3(-1f,0,0), 0.75f);
        createPointLight(new Vec3 (0.8f,0.8f,0), RgbColor.PURPLE);
        ambient_k = new RgbColor(0.1f,0.1f,0.1f);
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
    
    public void createPerspCam(Vec3 cameraPos_P0, Vec3 centerOfInterest_C, Vec3 userUp_g, float alphaDeg, float aspectRatio){
        this.perspCam = new PerspCam(cameraPos_P0, centerOfInterest_C, userUp_g, alphaDeg, aspectRatio);
    }

    public void addPerspCam(PerspCam _perspCam){
        this.perspCam = _perspCam;
    }

    public PerspCam getPerspectiveCamera () {
        return this.perspCam;
    }

    public void setAmbientFactor (RgbColor _ambient_k) {
        this.ambient_k = _ambient_k;
    }

    public RgbColor getAmbientFactor () {
        return this.ambient_k;
    }

    public PointLight getFirstLight() {
        return lightList.getFirst();
    }

    public ArrayList<Shape> getShapes(){
        return this.shapeList;
    }
    
    public ArrayList<PointLight> getLights(){
        return this.lightList;
    }

    public RgbColor shade (Intersection _intersection) {
        RgbColor pointColor = ambient_k;

        Material material = _intersection.getShape().getMaterial();
        RgbColor diffuse = material.getColor(_intersection.getIntersectionPoint(), 
                                            _intersection.getNormal(), 
                                            this.getFirstLight());
        
                                            
        System.out.println("DIFFUSE COLOR: " + diffuse);
        pointColor.add(diffuse.red(), diffuse.green(), diffuse.blue());
        System.out.println("SHAPE COLOR: " + pointColor);

        return pointColor;
    }

    public Intersection findClosestIntersection(Ray ray) {
        Intersection closest = new Intersection();
        for (Shape shape : shapeList) {
            Intersection temp = shape.intersect(ray);
            System.out.println(temp.isHit());
            if (temp.isHit() && temp.getDistance() < closest.getDistance()) {
                System.out.println("SHAPE HIT");
                closest = temp;
            }
        }
        return closest;
    }

}
