package scene.material;

import scene.light.Light;
import scene.light.PointLight;
import utils.RgbColor;
import utils.algebra.Vec3;

// Abstract class for all Materials, only knows the light Factors (k's)
public abstract class Material {

    protected static RgbColor ambientK = new RgbColor(0.1f,0.1f,0.1f);

    protected Material (RgbColor _ambientK) {
        Material.ambientK = _ambientK;
    }

    protected Material () {
        //uwu 
    }

    public RgbColor getAmbientK() {
        return ambientK;
    }

    
    public abstract RgbColor getColor(Vec3 _campos, Vec3 _position, Vec3 _normal, Light _light);
    //public abstract RgbColor getColorPhong(Vec3 _camPos, Vec3 _position, Vec3 _normal, Light _light);

}
