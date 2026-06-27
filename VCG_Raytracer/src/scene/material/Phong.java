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
                float n1 = 1f;
                float n2 = this.refrective_index;
                // vector vCam and vNormal already normalized and calculated
                // vCam points away from Surface, we need it the other way around
                vCam = vCam.multScalar(-1).normalize();

                // cos HAS to be between -1 and 1 -> else sqrt error (float errors)
                float scalar_vCam_vNormal = vCam.scalar(vNormal);
                if(scalar_vCam_vNormal < -1)
                    scalar_vCam_vNormal = -1;
                if(scalar_vCam_vNormal > 1) 
                    scalar_vCam_vNormal = 1;

                // Checking from which direction the ray originates (inside / outside)
                if(scalar_vCam_vNormal > 0) {
                    // Ray hits from the Inside, Normal looks into the same direction -> problems
                    // Normal has to be switched, Material Indices switch order
                    vNormal = vNormal.multScalar(-1);
                    float n_temp = n1;
                    n1 = n2;
                    n2 = n_temp;
                }
                scalar_vCam_vNormal = Math.abs(scalar_vCam_vNormal);

                float snellius = n1 / n2;
                // Calc underneath the sqrt to further test for value (above / below 0)
                float sqrt_test = (float) (1 - Math.pow(snellius, 2) * (1 - Math.pow(scalar_vCam_vNormal, 2)));

                // if result underneath the sqrt is less than 0, we cannot apply sqrt and there is no refrection
                // -> angle between Surface and Viewvector is basically 0, total reflection = no refraction!!  
                if(sqrt_test < 0) {
                    Vec3 vOutReflection = vNormal.multScalar(2 * (vCam.scalar(vNormal))).sub(vCam).normalize();
                    Ray reflectRay = new Ray(intersectionPosition.add(_intersection.getNormal().multScalar(0.0001f)), 
                                             vOutReflection);
                    _intersection.setOutRay(reflectRay);
                }else{
                    // Formel aus Vorlesung funktioniert nicht? Nehmen die von Stanford:
                    // T = n * I - (n * cos a + cos b) * N
                    Vec3 vOutRefrection = vCam.multScalar(snellius).sub(vNormal
                                            .multScalar(snellius * scalar_vCam_vNormal + (float) Math.sqrt(sqrt_test)))
                                            .normalize();
                    Ray refrectRay = new Ray(intersectionPosition.add(_intersection.getNormal().multScalar(0.0001f)), 
                                            vOutRefrection);
                    _intersection.setOutRay(refrectRay);
                }
            }

        }
        return new RgbColor(r,g,b);
    }

}
