package scene.light;

import scene.SceneObject;
import utils.RgbColor;
import utils.algebra.Vec3;

public abstract class Light extends SceneObject{

    public abstract RgbColor getColor();
    public abstract Vec3 getPosition();
    public abstract Vec3[] getCorners();

}
