package scene.material;

import raytracer.Ray;
import scene.Scene;
import scene.light.Light;
import scene.shape.Intersection;
import utils.RgbColor;
import utils.algebra.Vec3;

/** 
 * Lambert Material
 */
public class Lambert extends Material {

    // diffuse factor is set on instantiation
    public Lambert (RgbColor _diffuse_k) {
        this.diffuseK = _diffuse_k;
    }

    public Lambert (RgbColor _diffuse_k, float _reflection, float _transmission) {
        super(_reflection, _transmission);
        this.diffuseK = _diffuse_k;
    }

    public Lambert (RgbColor _diffuse_k, float _reflection, float _transmission, float _transmission_index) {
        super(_reflection, _transmission, _transmission_index);
        this.diffuseK = _diffuse_k;
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

    //basic copy method needed for texture mapping
    public Lambert copy(){
        Lambert copy;

        copy = new Lambert(diffuseK, reflection, transmission, transmission_index);
        if(this.getDiffuseTextureMap() != null)
            copy.setDiffuseTextureMap(diffuseTextureMap);

        return copy;
    }
}
