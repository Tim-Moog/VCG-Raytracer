package scene.light;

import utils.RgbColor;
import utils.algebra.Vec3;

// Light that eminates from a single point
public class PointLight extends Light {

    protected Vec3 position;
    protected RgbColor color;
    
    public PointLight (Vec3 _position, RgbColor _color, float _intensity) {
        this.position = _position;
        this.color = _color;
        this.intensity = _intensity;
    }
    
    public RgbColor getColor() {
        return this.color;
    }

    public Vec3 getPosition() {
        return this.position;
    }

    public float getIntensity(){
        return this.intensity;
    }
}
