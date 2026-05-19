package scene;

import java.util.ArrayList;

import scene.light.Light;
import scene.light.PointLight;
import scene.shape.Shape;
import scene.shape.Sphere;
import utils.algebra.Vec3;
import utils.io.Log;


public class Scene {

    //think arraylists prob fit better than just flat out arrays, this way we dont have to predefine length
    private ArrayList<Shape> shapeList;
    private ArrayList<Light> lightList;

    public Scene() 
    {
        shapeList = new ArrayList<Shape>();
        lightList = new ArrayList<Light>();
        createSphere(new Vec3(1.5f,1f,0), 1.25f);
        createSphere(new Vec3(-1f,0,0), 0.75f);
        Log.print(this, "Init");
    }

    public void createSphere(Vec3 _pos, float _radius){
        //first create a 1-1 standardkreis 
        //angaben werden eher als 4x4 matrix 
        Sphere newSphere = new Sphere(_pos, _radius);
        
        shapeList.add(newSphere);
    }

    public void createPlane(){
        //yea we dont have that yet
    }

    public void createPointLight(){
        Light newLight = new PointLight();
        lightList.add(newLight);
    }

    //ngl am a bit confused why this is a method here? like where would we save it?
    public void createPerspCam(){
        
    }

    public ArrayList<Shape> getShapes(){
        return this.shapeList;
    }
    
    public ArrayList<Light> getLights(){
        return this.lightList;
    }
}
