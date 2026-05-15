package scene.light;

import scene.SceneObject;
import utils.RgbColor;

public abstract class Light extends SceneObject{
    
    protected RgbColor color;

    public abstract RgbColor getColor();
}
