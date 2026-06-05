package scene.material;

import scene.light.Light;
import scene.light.PointLight;
import utils.RgbColor;
import utils.algebra.Vec3;

// Calculates the Diffuse Aspect of Lighting
public class Phong extends Material {

    // diffuse factor is set on instantiation
    public Phong (RgbColor _diffuse_k) {
        super(_diffuse_k);
    }
    
    public RgbColor getColor(Vec3 _position, Vec3 _normal, Light _light){

        // We use the Light Object for the Parameter to ease changes (fewer places to add or remove values)
        Vec3 lightPosition = _light.getPosition();
        RgbColor lightColor = _light.getColor();

        // Variables later on inserted into our RgbColor output
        // Better for calculating the indiviual light components
        Float r,g,b;

        // Calculating the necessary Vectors for diffuse light component:
        // I * kd * (N x L) <- [(N x L)]
        Vec3 normal = _normal.normalize();
        Vec3 vec_light = lightPosition.sub(_position).normalize();
        Float scalar_normal_light = normal.scalar(vec_light);

        // Scalarproduct has to be above 0, else Light doesn't hit this point
        if(scalar_normal_light <= 0) {
            // Face is looking away from Light; doesn't get any values added
            return new RgbColor(0,0,0);
        } else {
            // I * kd * (N x L)
            r = lightColor.red() * diffuseK.red() * scalar_normal_light;
            g = lightColor.green() * diffuseK.green() * scalar_normal_light;
            b = lightColor.blue() * diffuseK.blue() * scalar_normal_light;

            return new RgbColor(r,g,b);
        }
    }


    private RgbColor spekularK;
    private int specularExponent;

    public Phong (RgbColor _diffuse_k, RgbColor _spekular_k, int _specular_exponent){
        super(_diffuse_k);
        this.spekularK = _spekular_k;
        this.specularExponent = _specular_exponent;
    }

    //honestly think we may need another input var for the cam position
    //man könnte light als param raus nehmen und hier drinnen ne for each schleife der lichter haben
    //but we'd need access to the lightlist for that
    public RgbColor getColorPhong(Vec3 _camPos, Vec3 _position, Vec3 _normal, Light _light){


        Vec3 lightPosition = _light.getPosition();
        RgbColor lightColor = _light.getColor();
        Float r,g,b;

        Vec3 normal = _normal.normalize();
        Vec3 vec_light = lightPosition.sub(_position).normalize();

        Float scalar_normal_light = normal.scalar(vec_light);
        if(scalar_normal_light <= 0){
            // Face is looking away from Light; doesn't get any values added
            return new RgbColor(0,0,0);
        }else{
            r = lightColor.red() * diffuseK.red() * scalar_normal_light;
            g = lightColor.green() * diffuseK.green() * scalar_normal_light;
            b = lightColor.blue() * diffuseK.blue() * scalar_normal_light;

            //betrachtungsvektor
            Vec3 vec_cam = _camPos.sub(_position);
            vec_cam = vec_cam.normalize();

            //reflektionsvektor
            Vec3 vec_reflection = new Vec3();
            //possible that we'd need to split this over 2 lines
            vec_reflection = vec_reflection.sub(normal.multScalar(2 * scalar_normal_light)).sub(vec_light);
            vec_reflection = vec_reflection.normalize();

            Float scalar_view_reflection = vec_reflection.scalar(vec_cam);

            r += (float) (lightColor.red() * (spekularK.red() * Math.pow(Math.max(0, scalar_view_reflection), specularExponent)));
            g += (float) (lightColor.green() * (spekularK.green() * Math.pow(Math.max(0, scalar_view_reflection), specularExponent)));
            b += (float) (lightColor.blue() * (spekularK.blue() * Math.pow(Math.max(0, scalar_view_reflection), specularExponent)));

            return new RgbColor(r, g, b);
        }
    }

}
