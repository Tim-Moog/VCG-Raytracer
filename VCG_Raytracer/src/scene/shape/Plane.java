package scene.shape;

import raytracer.Ray;
import scene.material.Material;
import utils.algebra.Vec3;

public class Plane extends Shape {

    Vec3 normal;

    public Plane(Vec3 _position, Vec3 _normal, Material _material) {
        super(_position, _material);
        //i think this is the only extra thing we need for a plane?
        this.normal = _normal;
    }


    public Intersection intersect(Ray checkRay){
        //how could we check if theres a hit?
        //
        return null;
    }

    
    
}
