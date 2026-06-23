package scene.material;

import raytracer.Ray;
import scene.Scene;
import scene.light.Light;
import scene.shape.Intersection;
import scene.shape.Plane;
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

    public Lambert (RgbColor _diffuse_k, float _shinyness, float _transparency) {
        super(_shinyness, _transparency);
        this.diffuseK = _diffuse_k;
    }
    
    @Override
    public RgbColor getColor(Intersection _intersection, Ray _ray, Scene _scene, RgbColor _ambient, int _depth){

        Float r,g,b;

        r = _ambient.red() * diffuseK.red();
        g = _ambient.green() * diffuseK.green();
        b = _ambient.blue() * diffuseK.blue();

        for (Light light : _scene.getLights()) {

            Vec3 intersectionPosition = _intersection.getIntersectionPoint();
            Vec3 vNormal = _intersection.getNormal().normalize();
            //Viewvector
            Vec3 vCam = _ray.getDirection().multScalar(-1).normalize();


            //basically what we need for areaLight is to make the isInShadow method check each if its corners/how many of the corners are blocked by sth in the way 
            float shadowPercent = _scene.isInShadow(_intersection, light);
            if (shadowPercent != 0) {

                Vec3 lightPosition = light.getPosition();

                Vec3 vLight = lightPosition.sub(intersectionPosition).normalize();
                Float scalar_normal_light = vNormal.scalar(vLight);

                RgbColor lightColor = light.getColor();

                // DIFFUSE
                r += lightColor.red() * diffuseK.red() * scalar_normal_light * shadowPercent;
                g += lightColor.green() * diffuseK.green() * scalar_normal_light * shadowPercent;
                b += lightColor.blue() * diffuseK.blue() * scalar_normal_light * shadowPercent;
                
                

            }

            if (this.shinyness != 0) {
                Vec3 vOutReflection = vNormal.multScalar(2 * (vCam.scalar(vNormal))).sub(vCam).normalize();

                Ray reflectRay = new Ray(intersectionPosition.add(_intersection.getNormal().multScalar(0.0001f)), vOutReflection);
                _intersection.setOutRay(reflectRay);
            }
        }
        return new RgbColor(r,g,b);
    }
}
