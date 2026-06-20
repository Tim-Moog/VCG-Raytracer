package scene.material;

import raytracer.Ray;
import scene.Scene;
import scene.light.Light;
import scene.shape.Intersection;
import utils.RgbColor;
import utils.algebra.Vec3;

// Calculates the Diffuse Aspect of Lighting
public class Lambert extends Material {

    // diffuse factor is set on instantiation
    public Lambert (RgbColor _diffuse_k) {
        this.diffuseK = _diffuse_k;
    }
    
    @Override
    public RgbColor getColor(Intersection _intersection, Ray _ray, Scene _scene, RgbColor _ambient, int _depth){

        Float r,g,b;

        r = _ambient.red() * diffuseK.red();
        g = _ambient.green() * diffuseK.green();
        b = _ambient.blue() * diffuseK.blue();

        for (Light light : _scene.getLights()) {

            if (!_scene.isInShadow(_intersection, light)) {

                Vec3 lightPosition = light.getPosition();
                Vec3 intersectionPosition = _intersection.getIntersectionPoint();

                Vec3 vNormal = _intersection.getNormal().normalize();
                Vec3 vLight = lightPosition.sub(intersectionPosition).normalize();
                Float scalar_normal_light = vNormal.scalar(vLight);

                RgbColor lightColor = light.getColor();

                // DIFFUSE
                r += lightColor.red() * diffuseK.red() * scalar_normal_light;
                g += lightColor.green() * diffuseK.green() * scalar_normal_light;
                b += lightColor.blue() * diffuseK.blue() * scalar_normal_light;
            }
        }
        return new RgbColor(r,g,b);
    }
}
