package scene.camera;

import raytracer.Ray;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class PerspCam extends Camera {

    // side of out viewport, what camera looks at
    private float viewport_height, viewport_width;

    public PerspCam (Vec3 cameraPos_P0, Vec3 centerOfInterest_C, Vec3 userUp_g, float alphaDeg, float aspectRatio) {
        super(cameraPos_P0, centerOfInterest_C, userUp_g, alphaDeg);

        float alpha = (float) Math.toRadians(alphaDeg); // from degree to radians
        // needed to accurately get the position of the midpoint of our pixel inside our viewport
        // normalized after direction calculation to include camera distance in proportion
        viewport_height =  2f * (float) Math.tan(alpha/2f); 
        viewport_width = aspectRatio * viewport_height; 
    }

    // builds Ray in already normalized coordinate system
    public Ray getRay(float s, float u) {

        //combines distance to camera with offset into x and y
        Vec3 direction =
                side.multScalar((s - 0.5f) * viewport_width)   
                .add(up.multScalar((u - 0.5f) * viewport_height))
                .add(view);                                   // viewplane distance to camera

        return new Ray(origin, direction.normalize());
    }

    // Ray coordinates turned into [0,1] coordinates
    public Ray getRayForPixel(Vec2 targetPixel, Vec2 windowSize) {

        // Pixel into viewplane coordinates normed
        float pNorm_x = (2f * (targetPixel.x + 0.5f) / (windowSize.x)) - 1f;
        float pNorm_y = (2f * (targetPixel.y + 0.5f) / (windowSize.y)) - 1f;

        // [-1,1] → [0,1]
        float s = (pNorm_x + 1f) * 0.5f;
        float u = (pNorm_y + 1f) * 0.5f;

        return getRay(s, u);
    }

    
}