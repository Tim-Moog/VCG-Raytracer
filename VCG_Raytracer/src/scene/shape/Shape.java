package scene.shape;

import raytracer.Ray;
import scene.SceneObject;
import scene.material.Material;
import utils.algebra.Vec3;

public abstract class Shape extends SceneObject {

    protected Vec3 position;
    protected Material material;

    public Shape (Vec3 _position, Material _material) {
        this.position = _position;
        this.material = _material;
    }
    
    //prob another place for polymorphism
    public abstract Intersection intersect(Ray ray);

    public abstract Material getMaterial();

}
