package scene.camera;

import raytracer.Ray;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class PerspCam extends Camera {

    public PerspCam(Vec3 p, Vec3 c, Vec3 g, Float alpha, Vec2 windowSize){
        super(p,c,g,alpha, windowSize);
    }

    // Input: 2D Pixel Plane with coordinate system with origin in the top left corner
    // Translation into our Viewplane for Raytracer - Coordinate System with origin in the middle, size of 2 (normalized)

    /**
     * Input: 2D Pixel Plane with coordinate system with origin in the top left corner
     * Translation into our Viewplane for Raytracer - Coordinate System with origin in the middle, size of 2 (normalized)
     * 
     * @param targetPixel the pixel from the window we want to calculate for 
     * @return our point in 3D
     */
    public Vec3 calculateDestinationPoint(Vec2 targetPixel){

        Vec2 p_norm = super.calculateDestinationPoint_2D(targetPixel);
        p_norm = p_norm.normalize();


        Vec3 viewVector_norm = viewVector.normalize();
        //calc aspectRatio and Vhorizontal and Vwidth
        //float viewplane_height = (float) (2 * Math.tan(viewAngle/2) * windowSize.y);  // WE DONT NEED THESE
        //float viewplane_width = (float) (aspectRatio * viewplane_height);
        //Vec3 viewplane_midpoint = cameraPos_P0.add(viewVector_norm);

        //calc directional vector for direction to viewplanePoint
        float deltaX = targetPixel.x - windowSize.x/2;
        float deltaY = targetPixel.y - windowSize.y/2;

        Vec3 distance = viewVector_norm.multScalar(focalLength);
        Vec3 directional = distance.add((sideVector.multScalar(deltaX)));
        directional = directional.add((upVector.multScalar(deltaY)));
        directional = directional.normalize();

        // Ray = P0 + distance * directionalVector
        Ray ray = new Ray(cameraPos_P0, directional);
        Vec3 pixelHitByRay = ray.calculateRayAt();
       
        return pixelHitByRay;
    }


}

