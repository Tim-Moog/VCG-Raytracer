package scene.light;

import java.util.Random;

import raytracer.Ray;
import scene.Scene;
import utils.RgbColor;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

//we NEED normal in here incase the light is mounted to a wall, else it would still go horizontally even when a light is mounted to a wall -> prob best to do with a square directly implemented
//actually nvm for now will do simply way thatll only work for horizontal lights
public class AreaLight extends Light {

    private PointLight[] lights;
    private Vec3 position;
    private RgbColor color;
    private float size;


    public AreaLight(Scene _scene, short _light_samples,  float _size, Vec3 _pos, RgbColor _color){
        this.size = _size;
        this.position = _pos;
        this.color = _color;
        this.lights = new PointLight[_light_samples];
        _color = _color.multScalar(3f/_light_samples);

        int samplesPerRowCol = (int)Math.sqrt(_light_samples);
        float subSectionSize = size/samplesPerRowCol;
        Random numberGenerator = new Random(67);
        int index = 0;

        for(float row = 0; row < samplesPerRowCol; row++){
            for(float col = 0; col < samplesPerRowCol; col++){
                //make new random values for ray position inside of subpixel
                float randomX = numberGenerator.nextFloat();
                float randomZ = numberGenerator.nextFloat();

                //get the actual offset of our subpixel using the rdm generated float and the current subpixel position based on AA sample amount
                float offsetX = (col + 0.5f) * subSectionSize;
                float offsetZ = (row + 0.5f) * subSectionSize;

                Vec3 adjustedPosition = new Vec3(position.x - size/2 + offsetX, position.y,  position.z - size/2 + offsetZ);
                lights[index++] = new PointLight(adjustedPosition, _color);
            }
        }
        _scene.addCornerLights(lights);
    }
    
    public RgbColor getColor(){
        return this.color;
    }

    public Vec3 getPosition(){
        return this.position;
    }

    public PointLight[] getLights(){
        return this.lights;
    }

}
