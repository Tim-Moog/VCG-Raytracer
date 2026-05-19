package scene.shape;

import raytracer.Ray;
import scene.SceneObject;
import utils.algebra.Vec3;

public abstract class Shape extends SceneObject {

    protected Intersection intersectionData;
    
    //prob another place for polymorphism
    public abstract void intersect(Ray checkRay);

    public abstract Intersection getIntersectionData();

}
