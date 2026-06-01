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
        Material mat_lambert = new Lambert(new RgbColor(0.6f,0.2f,0.5f));
        Material mat_lambert2 = new Lambert(new RgbColor(0.8f,0.8f,0.8f));

        //create spheres
        createSphere(new Vec3(1.5f,1,0), 1.25f, mat_lambert);
        createSphere(new Vec3(-1.5f,-0.5f,0), 0.75f, mat_lambert2);

        //create lights
        createPointLight(new Vec3 (-2.5f,2.5f,4), RgbColor.GREEN);
        createPointLight(new Vec3 (2.5f,2.5f,4), RgbColor.RED);
        createPointLight(new Vec3(0, -2.5f, 4), RgbColor.BLUE);
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

    public static RgbColor getAmbientFactor () {
        return ambient_k;
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
            //honestly no clue if theres a better way atleast couldnt find any specific way for this in folien but not having it in feels troll cuz it just makes sense for distance to play a role logically


            //maybe here sekundärstrahl von schnittpunkt zu lichtquelle und if no path found then dont add?
            //gotta prob create a new ray here and test with that? i dunno
            float distplusnorm = currentLight.getPosition()
                                .sub((_intersection.getIntersectionPoint()).add(_intersection.getNormal())) //think this was nonsense since it got handeled within getColor()
                                .length();
            

            //System.out.println("DIFFUSE COLOR: " + diffuse);
            //dumm check that is already in getColor i realised
            //if(distplusnorm < distance) //check if we are looking at the light or not //i dont think this is actually needed since already done in 
              //  pointColor.add(diffuse.red(), diffuse.green(), diffuse.blue());
            //System.out.println("SHAPE COLOR: " + pointColor);

            //sekundärstrahlen attempt- nvm not attempt.... SUCCESS
            Ray secondaryRay = new Ray(_intersection.getIntersectionPoint(), currentLight.getPosition().sub(_intersection.getIntersectionPoint()));
            Shape hitObjectShape = _intersection.getShape();
            boolean objectInTheWay = false; //boolean to keep track of if any object is blocking direct LOS from impact to light

            for (Shape currentShape : shapeList) { //go through all shapes
                if(currentShape != hitObjectShape){//except the one that our ray is starting from -> da kann man es abhängig von normale machen
                    Intersection secondaryRayData = currentShape.intersect(secondaryRay, 0);
                    if(secondaryRayData.isHit())
                        objectInTheWay = true;
                }
            }

            if(!objectInTheWay)
                pointColor.add(diffuse.red(), diffuse.green(), diffuse.blue());
        }

        //pretty sure this aint wanted since it just makes it a lot darker -> yea this just makes it very dark so dont do this
        //pointColor = new RgbColor(pointColor.red()/getLights().size(), pointColor.green()/getLights().size(), pointColor.blue()/getLights().size());

        //sekundärstrahlen attempt
        //logic written down 
        //send ray from intersectionpoint to the light itself <- due to this, this prob needs to be inside of the prev for each loop so will add there
        //schnitttest mit allen objekten
            //falls treffer
            //objektpunkt im schatten = pixelfarbe ist ambientwert
            //falls kein treffer
            //berechne lokales beleuchtungsmodell  

        return pointColor;
    }

    
    public static Intersection findClosestIntersection(Ray ray) {
        Intersection closest = new Intersection();
        for (Shape shape : shapeList) {
            Intersection temp = shape.intersect(ray, 0); //added 2nd param for testing
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
