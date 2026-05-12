package scene.shape;

import scene.SceneObject;
import utils.algebra.Vec3;

public class Shape extends SceneObject {
    
    //prob another place for polymorphism
    public boolean intersect(Vec3 checkRay){
        System.err.println("PLEASE USE SUBCLASS NOT SUPERCLASS SHAPE");
        return false;
    };

}
