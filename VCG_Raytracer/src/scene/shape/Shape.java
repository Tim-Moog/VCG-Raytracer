package scene.shape;

import raytracer.Ray;
import scene.SceneObject;
import scene.material.Material;
import utils.algebra.Vec3;

public abstract class Shape extends SceneObject {

    protected Intersection intersectionData;
    protected Vec3 position;
    protected Material material;


    public Shape (Vec3 _position, Material _material) {
        this.position = _position;
        this.material = _material;
        this.material.setMyShape(this);
        this.intersectionData = new Intersection();
    }

    
    //prob another place for polymorphism
    public abstract Intersection intersect(Ray checkRay);

    public Material getMaterial(){
        return this.material;
    };

    public Intersection getIntersectionData() {
        return this.intersectionData;
    }

}
