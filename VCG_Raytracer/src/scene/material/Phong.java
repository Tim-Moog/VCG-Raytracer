package scene.material;

import raytracer.Ray;
import scene.Scene;
import scene.light.Light;
import scene.light.PointLight;
import scene.shape.Intersection;
import utils.RgbColor;
import utils.algebra.Vec3;

// Calculates the Diffuse Aspect of Lighting
public class Phong extends Material {

    public Phong (RgbColor _diffuseK, RgbColor _specularK, int _specularExponent) {
        this.diffuseK = _diffuseK;
        this.specularK = _specularK;
        this.specularExponent = _specularExponent;
    }

    @Override
    public RgbColor getColor(Intersection _intersection, Ray _ray, Scene _scene, RgbColor _ambient){

        Float r,g,b;

        r = _ambient.red() * diffuseK.red();
        g = _ambient.green() * diffuseK.green();
        b = _ambient.blue() * diffuseK.blue();

        for (Light light : _scene.getLights()) {

            Vec3 intersectionPosition = _intersection.getIntersectionPoint();

            if (!_scene.isInShadow(_intersection, light)) {

                Vec3 lightPosition = light.getPosition();

                Vec3 vNormal = _intersection.getNormal().normalize();
                Vec3 vLight = lightPosition.sub(intersectionPosition).normalize();
                Float scalar_normal_light = vNormal.scalar(vLight);

                if(scalar_normal_light <= 0){
                    // Face is looking away from Light; doesn't get any values added
                    continue;
                }else{

                    RgbColor lightColor = light.getColor();

                    // DIFFUSE
                    r += lightColor.red() * diffuseK.red() * scalar_normal_light;
                    g += lightColor.green() * diffuseK.green() * scalar_normal_light;
                    b += lightColor.blue() * diffuseK.blue() * scalar_normal_light;


                    //betrachtungsvektor
                    Vec3 vCam = _ray.getDirection().multScalar(-1).normalize();
                    //reflektionsvektor
                    Vec3 vReflection = vNormal.multScalar(2 * scalar_normal_light).sub(vLight);
                    vReflection = vReflection.normalize();

                    Float scalar_view_reflection = vReflection.scalar(vCam);
                    
                    //SPECULAR
                    r += (float) (lightColor.red() * (specularK.red() * Math.pow(Math.max(0, scalar_view_reflection), specularExponent)));
                    g += (float) (lightColor.green() * (specularK.green() * Math.pow(Math.max(0, scalar_view_reflection), specularExponent)));
                    b += (float) (lightColor.blue() * (specularK.blue() * Math.pow(Math.max(0, scalar_view_reflection), specularExponent)));
                
                }
            }
        }
        return new RgbColor(r,g,b);
    }

}
