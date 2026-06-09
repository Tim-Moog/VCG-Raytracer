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

    public RgbColor shade (Intersection _intersection) {
        RgbColor pointColor = new RgbColor(
                                    ambient_k.red(),
                                    ambient_k.green(),
                                    ambient_k.blue()
                                );
        //pointColor = new RgbColor(0, 0, 0); //idk which once of these is the correct start

        for(Light currentLight : getLights()){
            Material material = _intersection.getShape().getMaterial();
            RgbColor diffuse = material.getColor(this.getSceneCamera().getOrigin(),
                                                _intersection.getIntersectionPoint(), 
                                                _intersection.getNormal(), 
                                                currentLight);
            
            //we prob need to grab the light position and compare to our intersection point
            float distance = currentLight.getPosition()
                            .sub(_intersection.getIntersectionPoint())
                            .length();
            diffuse = diffuse.multScalar(distance * 0.25f); // *0.25 due to else impact either too big or too smol 
            //value just depends on light distance which makes sense as you want further lights to have less impact....
            //but for this surely there has to be a better way to make it seem balanced no?
            //honestly no clue if theres a better way atleast couldnt find any specific way for this in folien but not having it in feels troll cuz it just makes sense for distance to play a role logically
            //weirdest thing is wenn wir das auskommentieren sieht es genauso dunkel aus bei *0.25 ?? like wha but bei nur *distance it bright af


            //----------------------------- secondary rays -----------------------
            //sekundärstrahlen idee
            //logic written down 
            //send ray from intersectionpoint to the light itself <- due to this, this prob needs to be inside of the prev for each loop so will add there
            //schnitttest mit allen objekten
                //falls treffer
                //objektpunkt im schatten = pixelfarbe ist ambientwert
                //falls kein treffer
                //berechne lokales beleuchtungsmodell 

            //sekundärstrahlen attempt- nvm not attempt.... SUCCESS
            Ray secondaryRay = new Ray(_intersection.getIntersectionPoint(), currentLight.getPosition().sub(_intersection.getIntersectionPoint()));
            Shape hitObjectShape = _intersection.getShape();
            boolean objectInTheWay = false; //boolean to keep track of if any object is blocking direct LOS from impact to light

            for (Shape currentShape : shapeList) { //go through all shapes
                if(currentShape != hitObjectShape){//except the one that our ray is starting from -> da kann man es abhängig von normale machen
                    Intersection secondaryRayData = currentShape.intersect(secondaryRay);
                    if(secondaryRayData.isHit())
                        objectInTheWay = true;
                }
            }

            if(!objectInTheWay)
                pointColor.add(diffuse.red(), diffuse.green(), diffuse.blue());
        }

        //pretty sure this aint wanted since it just makes it a lot darker -> yea this just makes it very dark so dont do this
        //pointColor = new RgbColor(pointColor.red()/getLights().size(), pointColor.green()/getLights().size(), pointColor.blue()/getLights().size());

        return pointColor;
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
