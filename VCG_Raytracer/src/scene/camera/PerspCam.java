package scene.camera;

import utils.algebra.Vec3;

public class PerspCam extends Camera {
    public PerspCam(Vec3 p, Vec3 c, Vec3 g, Float alpha){
        super(p,c,g,alpha);
    }
}
