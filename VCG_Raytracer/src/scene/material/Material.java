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

    protected Material () {
        // UwU empty
    }

    
    public abstract RgbColor getColor(Intersection _intersection, Ray _ray, Scene _scene, RgbColor _ambient);
    //public abstract RgbColor getColorPhong(Vec3 _camPos, Vec3 _position, Vec3 _normal, Light _light);

}
