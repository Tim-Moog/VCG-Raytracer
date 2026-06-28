package scene.material;

import raytracer.Ray;
import scene.Scene;
import scene.light.Light;
import scene.shape.Intersection;
import utils.RgbColor;
import utils.algebra.Vec3;

/**
 * Phong Material
 */
public class Phong extends Material {

    public Phong (RgbColor _diffuseK, RgbColor _specularK, int _specularExponent) {
        this.diffuseK = _diffuseK;
        this.specularK = _specularK;
        this.specularExponent = _specularExponent;
    }

    public Phong (RgbColor _diffuseK, RgbColor _specularK, int _specularExponent, float _reflection, float _transmission) {
        super(_reflection, _transmission);
        this.diffuseK = _diffuseK;
        this.specularK = _specularK;
        this.specularExponent = _specularExponent;
    }

    public Phong (RgbColor _diffuseK, RgbColor _specularK, int _specularExponent, float _reflection, float _transmission, float _transmission_index) {
        super(_reflection, _transmission, _transmission_index);
        this.diffuseK = _diffuseK;
        this.specularK = _specularK;
        this.specularExponent = _specularExponent;
    }

    @Override
    public RgbColor getColor(Intersection _intersection, Ray _ray, Scene _scene, RgbColor _ambient, int _depth){

        Float r,g,b;

        // AMBIENT
        r = _ambient.red() * diffuseK.red();
        g = _ambient.green() * diffuseK.green();
        b = _ambient.blue() * diffuseK.blue();

        //calculates color for every light in the scene
        for (Light light : _scene.getLights()) {

            Vec3 intersectionPosition = _intersection.getIntersectionPoint();
            Vec3 vNormal = _intersection.getNormal().normalize();
            //Viewvector
            Vec3 vCam = _ray.getDirection().multScalar(-1).normalize();

            //calculates surface color aslong as the object is not obscured by another in the scene
            if (!(_scene.isInShadow(_intersection, light))) {

                Vec3 lightPosition = light.getPosition();
                float distance = lightPosition.sub(intersectionPosition).length();
                if(distance < 1) //setting this as a min distance to avoid big white light circles on ceiling
                    distance = 1;

                Vec3 vLight = lightPosition.sub(intersectionPosition).normalize();
                Float scalar_normal_light = vNormal.scalar(vLight);

                RgbColor lightColor = light.getColor();

                // DIFFUSE
                r += lightColor.red() * diffuseK.red() * scalar_normal_light * (1/distance);
                g += lightColor.green() * diffuseK.green() * scalar_normal_light * (1/distance);
                b += lightColor.blue() * diffuseK.blue() * scalar_normal_light * (1/distance);

                // reflectionvector for highlight
                Vec3 vReflection = vNormal.multScalar(2 * scalar_normal_light).sub(vLight);
                vReflection = vReflection.normalize();

                Float scalar_view_reflection = vReflection.scalar(vCam);
                
                // SPECULAR
                r += (float) (lightColor.red() * (specularK.red() * Math.pow(Math.max(0, scalar_view_reflection), specularExponent))) * (1/distance);
                g += (float) (lightColor.green() * (specularK.green() * Math.pow(Math.max(0, scalar_view_reflection), specularExponent))) * (1/distance);
                b += (float) (lightColor.blue() * (specularK.blue() * Math.pow(Math.max(0, scalar_view_reflection), specularExponent))) * (1/distance);
            
            }

            //always calculated, are not impacted by shadows!

            if (this.reflection != 0) {
                this.calculateReflectionRay(_ray, vNormal, _intersection);
            }
            if (this.transmission != 0) {
               this.calculateTransmissionRay(_ray, vNormal, _intersection);
            }

        }
        return new RgbColor(r,g,b);
    }

}
