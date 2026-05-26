package scene.material;

import scene.light.PointLight;
import utils.RgbColor;
import utils.algebra.Vec3;

// Abstract class for all Materials, only knows the light Factors (k's)
public abstract class Material {

    protected Vec3 diffuseK = new Vec3 (0,0,0);

    public Material (Vec3 _diffuseK) {
        this.diffuseK = _diffuseK;
    }
    
    public abstract RgbColor getColor(Vec3 _position, Vec3 _normal, Vec3 _surfaceColor, PointLight _light);

}
