package scene.camera;

import raytracer.Ray;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class OrthoCam extends Camera {
    public OrthoCam(Vec3 p, Vec3 c, Vec3 g, Float alphaDeg){
        super(p,c,g,alphaDeg);
    }

    public Ray getRay(float s, float u) {
        return null;
    }

    public Ray getRayForPixel(Vec2 _currentPixel, Vec2 _screensize){
        System.err.println("Ortho Cam is not yet implemented!");
        return null;
    }
}
