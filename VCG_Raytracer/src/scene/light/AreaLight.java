package scene.light;

import utils.RgbColor;
import utils.algebra.Vec3;

//we NEED normal in here incase the light is mounted to a wall, else it would still go horizontally even when a light is mounted to a wall -> prob best to do with a square directly implemented
//actually nvm for now will do simply way thatll only work for horizontal lights
public class AreaLight extends Light {

    Vec3[] cornerPositions;
    Vec3 position;
    RgbColor color;
    float size;

    public AreaLight(float _size, Vec3 _pos, RgbColor _color){
        this.size = _size;
        this.position = _pos;
        this.color = _color;
        this.cornerPositions = new Vec3[4];
        cornerPositions[0] = new Vec3(position.x - size/2, position.y, position.z - size/2);
        cornerPositions[1] = new Vec3(position.x - size/2, position.y, position.z + size/2);
        cornerPositions[2] = new Vec3(position.x + size/2, position.y, position.z - size/2);
        cornerPositions[3] = new Vec3(position.x + size/2, position.y, position.z + size/2);
    }
    
    public RgbColor getColor(){
        return this.color;
    }

    public Vec3 getPosition(){
        return this.position;
    }

    public Vec3[] getCorners(){
        return cornerPositions;
    }

}
