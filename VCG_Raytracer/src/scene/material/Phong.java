package scene.material;

import scene.light.Light;
import scene.light.PointLight;
import utils.RgbColor;
import utils.algebra.Vec3;

// Calculates the Diffuse Aspect of Lighting
public class Phong extends Material {

    private RgbColor diffuseK;
    private RgbColor specularK;
    private int specularExponent;

    public Phong (RgbColor _diffuseK, RgbColor _specularK, int _specularExponent) {
        this.diffuseK = _diffuseK;
        this.specularK = _specularK;
        this.specularExponent = _specularExponent;
    }


    //honestly think we may need another input var for the cam position
    //man könnte light als param raus nehmen und hier drinnen ne for each schleife der lichter haben
    //but we'd need access to the lightlist for that
    public RgbColor getColor(Vec3 _camPos, Vec3 _position, Vec3 _normal, Light _light){


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
            vec_reflection = normal.multScalar(2 * scalar_normal_light).sub(vec_light);
            vec_reflection = vec_reflection.normalize();

            Float scalar_view_reflection = vec_reflection.scalar(vec_cam);

            r += (float) (lightColor.red() * (specularK.red() * Math.pow(Math.max(0, scalar_view_reflection), specularExponent)));
            g += (float) (lightColor.green() * (specularK.green() * Math.pow(Math.max(0, scalar_view_reflection), specularExponent)));
            b += (float) (lightColor.blue() * (specularK.blue() * Math.pow(Math.max(0, scalar_view_reflection), specularExponent)));

            return new RgbColor(r, g, b);
        }
    }

}
