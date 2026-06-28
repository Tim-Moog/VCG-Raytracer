package scene.material;

import raytracer.Ray;
import scene.Scene;
import scene.shape.Intersection;
import utils.RgbColor;

// Cheat Class for Lamps to give a shape a solid color without any calculations
public class Solid extends Material {

    private RgbColor colorOverride = RgbColor.WHITE;

    public Solid() {}

    public Solid (RgbColor _colorOverride) {
        this.colorOverride = _colorOverride;
    }
    
    @Override
    public RgbColor getColor(Intersection _intersection, Ray _ray, Scene _scene, int _depth){
        return this.colorOverride;
    }
}
