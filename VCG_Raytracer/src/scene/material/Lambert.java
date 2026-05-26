package scene.material;

import scene.light.PointLight;
import utils.RgbColor;
import utils.algebra.Vec3;

public class Lambert extends Material {

    public Lambert (Vec3 _diffuseFactor_kd) {
        super(_diffuseFactor_kd);
    }
    
    public RgbColor getColor(Vec3 _position, Vec3 _normal, Vec3 _surfaceColor, PointLight _light){
        // DIFFUSE: I = kd * (N * L)
        // Light = diffuseFactor * (normal . light)

        Vec3 lightPosition = _light.getPosition();
        RgbColor lightColor = _light.getColor();

        Float r,g,b;

        Vec3 normal = _normal.normalize();
        Vec3 vec_light = _position.sub(lightPosition).normalize();
        Float scalar_normal_light = normal.scalar(vec_light);

        // Scalarproduct has to be above 0, else Light doesn't hit this point
        if(scalar_normal_light <= 0) {
            return null;
        } else {
            r = lightColor.red() * (diffuseK.x * scalar_normal_light);
            g = lightColor.green() * (diffuseK.y * scalar_normal_light);
            b = lightColor.blue() * (diffuseK.z * scalar_normal_light);

            RgbColor outColor = new RgbColor(r,g,b);
            return outColor;
        }
    }

}
