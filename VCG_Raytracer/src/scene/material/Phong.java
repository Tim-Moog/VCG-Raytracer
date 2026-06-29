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
    public RgbColor getColor(Intersection _intersection, Scene _scene, int _depth){

        // AMBIENT
        RgbColor output = Material.ambientK.multRGB(this.diffuseK);

        //calculates color for every light in the scene
        for (Light light : _scene.getLights()) {

            Vec3 intersectionPosition = _intersection.getIntersectionPoint();
            Ray inRay = _intersection.getInRay();
            Vec3 vNormal = _intersection.getNormal().normalize();

            //calculates surface color aslong as the object is not obscured by another in the scene
            if (!(_scene.isInShadow(_intersection, light))) {

                Vec3 lightPosition = light.getPosition();
                float distance = lightPosition.sub(intersectionPosition).length();
                if(distance < 1) //setting this as a min distance to avoid big white light circles on ceiling
                    distance = 1;

                Vec3 vLight = lightPosition.sub(intersectionPosition).normalize();
                float scalar_normal_light = vNormal.scalar(vLight);

                RgbColor lightColor = light.getColor();

                // DIFFUSE
                output = output.add(lightColor.multRGB(diffuseK).multScalar(scalar_normal_light * light.getIntensity()/distance));

                // reflectionvector for highlight
                Vec3 vReflection = vNormal.multScalar(2 * scalar_normal_light).sub(vLight);
                vReflection = vReflection.normalize();
                //Viewvector
                Vec3 vCam = inRay.getDirection().multScalar(-1).normalize();
                float scalar_view_reflection = vReflection.scalar(vCam);
                
                // SPECULAR
                float specularFactor = (float) Math.pow(Math.max(0, scalar_view_reflection), specularExponent);
                RgbColor specular = lightColor.multRGB(specularK).multScalar(specularFactor * (light.getIntensity() / distance)); 
                output = output.add(specular);
            }

            //always calculated, are not impacted by shadows!

            // REFLECTION
            if (this.reflection != 0) {
                this.calculateReflectionRay(inRay, vNormal, _intersection);
            }

            // TRANSMISSION
            if (this.transmission != 0) {
               this.calculateTransmissionRay(inRay, vNormal, _intersection);
            }

        }
        return output;
    }

}
