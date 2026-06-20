package scene.material;

import raytracer.Ray;
import scene.Scene;
import scene.shape.Intersection;
import utils.RgbColor;

// Cheat Class to remove Diffuse Part of Lambert Material for a simulated AreaLight
public class LambertLamp extends Material {

    public LambertLamp () {
    }
    
    @Override
    public RgbColor getColor(Intersection _intersection, Ray _ray, Scene _scene, RgbColor _ambient){

        return RgbColor.WHITE;
    }
}
