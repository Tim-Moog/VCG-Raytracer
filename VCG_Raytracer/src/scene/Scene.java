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

    private static ArrayList<Shape> shapeList;
    private static ArrayList<PointLight> lightList;

    private static PerspCam perspCam;
    private static RgbColor ambient_k;

    public Scene() 
    {
        shapeList = new ArrayList<Shape>();
        lightList = new ArrayList<PointLight>();
        Log.print(this, "Init");

        //create materials
        Material mat_lambert = new Lambert(new RgbColor(0.9f,0.2f,0.5f));
        Material mat_lambert2 = new Lambert(new RgbColor(0.8f,0.8f,0.8f));

        //create spheres
        createSphere(new Vec3(1.5f,1,0), 1.25f, mat_lambert);
        createSphere(new Vec3(-1.5f,-0.5f,0), 0.75f, mat_lambert2);

        //create lights
        createPointLight(new Vec3 (3f,3f,0), RgbColor.PURPLE);
        createPointLight(new Vec3 (0f,0f,1), RgbColor.RED);
        createPointLight(new Vec3(-2, -3, -2), RgbColor.YELLOW);
        ambient_k = new RgbColor(0.1f,0.1f,0.1f);
    }

    public Scene(PerspCam _perspCam) {
        // For our Task 3 specified
        this();
        addPerspCam(_perspCam);
        Material mat_lambert = new Lambert(new RgbColor(0.8f,0.8f,0.8f));
        //createSphere(new Vec3(0,0,0), 1, mat_lambert);
        createSphere(new Vec3(1.5f,1,0), 1.25f, mat_lambert);
        createSphere(new Vec3(-0.5f,-0.5f,0), 0.75f, mat_lambert);
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

    public static PointLight getFirstLight() {
        return lightList.getFirst();
    }

    public static ArrayList<Shape> getShapes(){
        return shapeList;
    }
    
    public static ArrayList<PointLight> getLights(){
        return lightList;
    }

    public static RgbColor shade (Intersection _intersection) {
        RgbColor pointColor = new RgbColor(
                                    ambient_k.red(),
                                    ambient_k.green(),
                                    ambient_k.blue()
                                );

        for(Light currentLight : getLights()){
            Material material = _intersection.getShape().getMaterial();
            RgbColor diffuse = material.getColor(_intersection.getIntersectionPoint(), 
                                                _intersection.getNormal(), 
                                                currentLight);
            
            //we prob need to grab the light position and compare to our intersection point
            float distance = currentLight.getPosition()
                            .sub(_intersection.getIntersectionPoint())
                            .length();
            diffuse = diffuse.multScalar(distance * 0.25f); // *0.25 due to else impact either too big or too smol 
            //value just depends on light distance which makes sense as you want further lights to have less impact....
            //but for this surely there has to be a better way to make it seem balanced no?

            //System.out.println("DIFFUSE COLOR: " + diffuse);
            pointColor.add(diffuse.red(), diffuse.green(), diffuse.blue());
            //System.out.println("SHAPE COLOR: " + pointColor);
        }

        return pointColor;
    }

    
    public Intersection findClosestIntersection(Ray ray) {
        Intersection closest = new Intersection();
        for (Shape shape : shapeList) {
            Intersection temp = shape.intersect(ray, 0); //added 2nd param for testing
            System.out.println(temp.isHit());
            if (temp.isHit() && temp.getDistance() < closest.getDistance()) {
                System.out.println("SHAPE HIT");
                closest = temp;
            }
        }
        return closest;
    }

}
