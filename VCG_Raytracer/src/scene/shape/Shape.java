package scene.shape;

import raytracer.Ray;
import scene.SceneObject;
import utils.algebra.Vec3;

public abstract class Shape extends SceneObject {

    protected Intersection intersectionData;
    
    //prob another place for polymorphism
    public void intersect(Ray checkRay){
        System.err.println("PLEASE USE SUBCLASS NOT SUPERCLASS SHAPE");
    };

    public abstract Intersection getIntersectionData();

}
