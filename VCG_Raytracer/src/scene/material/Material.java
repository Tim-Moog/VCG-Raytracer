package scene.material;

import raytracer.Ray;
import scene.Scene;
import scene.shape.Intersection;
import utils.RgbColor;

// Abstract class for all Materials, only knows the light Factors (k's)
public abstract class Material {

    protected RgbColor diffuseK;
    protected RgbColor specularK;
    protected int specularExponent;
    protected float shinyness = 0; //between 0 and 1

    protected Material () {
        // UwU empty
    }

    protected Material (float _shinyness) {
        this.shinyness = _shinyness;
    }
    
    public abstract RgbColor getColor(Intersection _intersection, Ray _ray, Scene _scene, RgbColor _ambient, int _depth);
    //public abstract RgbColor getColorPhong(Vec3 _camPos, Vec3 _position, Vec3 _normal, Light _light);

    public float getShinyness () {
        return this.shinyness;
    }

}
