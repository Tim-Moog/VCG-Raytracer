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

    public Phong (RgbColor _diffuseK, RgbColor _specularK, int _specularExponent, float _shinyness, float _transparency) {
        super(_shinyness, _transparency);
        this.diffuseK = _diffuseK;
        this.specularK = _specularK;
        this.specularExponent = _specularExponent;
    }

    public Phong (RgbColor _diffuseK, RgbColor _specularK, int _specularExponent, float _shinyness, float _transparency, float _refrective_index) {
        super(_shinyness, _transparency, _refrective_index);
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

            //calculates the ray for the reflection
            //BROKEN FOR MULTIPLE LIGHTS!
            if (this.shinyness != 0) {
                Vec3 vOutReflection = vNormal.multScalar(2 * (vCam.scalar(vNormal))).sub(vCam).normalize();

                Ray reflectRay = new Ray(intersectionPosition.add(_intersection.getNormal().multScalar(0.0001f)), 
                                            vOutReflection);
                _intersection.setOutRay(reflectRay);
            }

            //Needed: IncRay, Surface Normal, Material Index from IncRay and this Material
            if (this.transparency != 0) {
                // Both Material Indices
                // snellius: sin a / sin b = n1 / n2
                float n1 = 1f; // Surrounding (Air)
                float n2 = this.refrective_index; //from Shape
                // cos a = N * I
                float cos_a = vNormal.scalar(vCam);
                // cos b = sqrt(1-(n1/n2)^2 * (1-(N*I))^2)
                float cos_b = (float) Math.sqrt(1 - Math.pow((n1/n2), 2) * (1 - Math.pow(cos_a, 2)));
                // T = sin b/sin a * (N * cos a - I) - N * cos b
                Vec3 vOutRefrect = vNormal.multScalar(cos_a).sub(vCam).multScalar(n2/n1).sub(vNormal.multScalar(cos_b));

                Ray reflectRay = new Ray(intersectionPosition.add(_intersection.getNormal().multScalar(-0.0001f)), 
                                            vOutRefrect);
                _intersection.setOutRay(reflectRay);
            }

        }
        return new RgbColor(r,g,b);
    }

}
