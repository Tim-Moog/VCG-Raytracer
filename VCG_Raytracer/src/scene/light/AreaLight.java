package scene.light;

import scene.Scene;
import utils.RgbColor;
import utils.algebra.Vec3;

//we NEED normal in here incase the light is mounted to a wall, else it would still go horizontally even when a light is mounted to a wall -> prob best to do with a square directly implemented
//actually nvm for now will do simply way thatll only work for horizontal lights
public class AreaLight extends Light {

    private PointLight[] cornerLights;
    private Vec3 position;
    private RgbColor color;
    private float size;


    public AreaLight(Scene _scene, float _size, Vec3 _pos, RgbColor _color){
        this.size = _size;
        this.position = _pos;
        this.color = _color;
        this.cornerLights = new PointLight[4];
        cornerLights[0] = new PointLight(new Vec3(position.x - size/2, position.y, position.z - size/2), _color);
        cornerLights[1] = new PointLight(new Vec3(position.x - size/2, position.y, position.z + size/2), _color);
        cornerLights[2] = new PointLight(new Vec3(position.x + size/2, position.y, position.z - size/2), _color);
        cornerLights[3] = new PointLight(new Vec3(position.x + size/2, position.y, position.z + size/2), _color);

        _scene.addCornerLights(cornerLights);
    }
    
    public RgbColor getColor(){
        return this.color;
    }

    public Vec3 getPosition(){
        return this.position;
    }

    public PointLight[] getCornerLights(){
        return this.cornerLights;
    }

}
