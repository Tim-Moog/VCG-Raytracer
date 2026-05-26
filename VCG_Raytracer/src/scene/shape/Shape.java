package scene.shape;

import raytracer.Ray;
import scene.SceneObject;
import utils.algebra.Vec3;

public abstract class Shape extends SceneObject {

    protected Vec3 position;

    public Shape (Vec3 _position) {
        this.position = _position;
    }
    
    //prob another place for polymorphism
    public abstract Intersection intersect(Ray ray);

}
