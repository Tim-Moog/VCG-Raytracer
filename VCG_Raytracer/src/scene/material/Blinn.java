package scene.material;

import scene.light.Light;
import scene.light.PointLight;
import utils.RgbColor;
import utils.algebra.Vec3;


//////////////////////////-----------------------NOT IMPLEMENTED YET-----------------//////////////////////////
///------CURRENTLY JUST A COPY PASTE TO AVOID ISSUES


// Calculates the Diffuse Aspect of Lighting
public class Blinn extends Material {

    // diffuse factor is set on instantiation
    public Blinn (RgbColor _diffuse_k) {
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

    //temp for testing
    public RgbColor getColorPhong(Vec3 _camPos, Vec3 _position, Vec3 _normal, Light _light){
        return diffuseK;
    }
}
