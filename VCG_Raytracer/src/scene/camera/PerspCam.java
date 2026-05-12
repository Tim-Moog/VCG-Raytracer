package scene.camera;

import raytracer.Ray;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class PerspCam extends Camera {

    public PerspCam(Vec3 p, Vec3 c, Vec3 g, Float alpha){
        super(p,c,g,alpha);
    }

    // Input: 2D Pixel Plane with coordinate system with origin in the top left corner
    // Translation into our Viewplane for Raytracer - Coordinate System with origin in the middle, size of 2 (normalized)

    /**
     * Input: 2D Pixel Plane with coordinate system with origin in the top left corner
     * Translation into our Viewplane for Raytracer - Coordinate System with origin in the middle, size of 2 (normalized)
     * 
     * @param targetPixel the pixel we want to calculate for 
     * @param widthAndHeight screensizes
     * @return our point in 3D
     */
    public Vec3 calculateDestinationPoint(Vec3 targetPixel, Vec2 widthAndHeight){

        //calc xNorm and yNorm
        float xNorm = (float) (2 * (targetPixel.x + 0.5)/widthAndHeight.x - 1);
        float yNorm = (float) (2 * (targetPixel.y + 0.5)/widthAndHeight.y - 1);
        Vec2 pNorm = new Vec2(xNorm, yNorm);
        pNorm = pNorm.normalize();


        //calc aspectRatio and Vhorizontal and Vwidth
        float aspectRatio = widthAndHeight.x / widthAndHeight.y;
        float vHeight = (float) (2 * Math.tan(viewAngle/2) * widthAndHeight.y); 
        float vWidth = (float) (aspectRatio * vHeight);


        //Punkt3D = CamPos + V * focalLength + S * Xnorm *Vb/2 + U * Ynorm *Vh/2
        //info: this needs to be in one line, tried it in 3 seperated by the .add's and then you always get 0 0 0 as result??? so keep in one line
        Vec3 punkt3D = cameraPos_P0.add(viewVector.multScalar(focalLength)).add(sideVector.multScalar(pNorm.x * (vWidth/2))).add(upVector.multScalar(pNorm.y * (vHeight/2)));


        //calculate the ray from in here
        Ray myRay = new Ray(cameraPos_P0, punkt3D); //could move this out into a IV and use setDir on each call instead of creating new object each time -> will change later
        Vec3 pixelHitByRay = myRay.calculateRayAt(punkt3D, viewVector, targetPixel, widthAndHeight);
        return pixelHitByRay;
    }


}

