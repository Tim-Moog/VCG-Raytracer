package scene.camera;

import java.awt.JobAttributes.SidesType;

import scene.SceneObject;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class Camera extends SceneObject{
    
    //need vars for Center of Cam P Vec3
    //Center of Interest C Vec3
    //User Up-Vektor g Vec3 // we provide this one, not the proper one
    //Angle alpha prob einfach int grad zahl
    //FocalLength standard:1 distance from camera to picture
    //AspectRatio desired WindowSize of what we display

    private Vec3 cameraPos_P0;
    private Vec3 centerOfInterest_C;
    private Vec3 user_upVektor_g;
    private Float viewAngle;
    private Float focalLength = 1f;

    private Vec3 viewVector; // Center of Interest - Camera Position
    private Vec3 sideVector; // viewVektor X User-upVector
    private Vec3 upVector; // viewVektor X sideVector

    public Camera(Vec3 p, Vec3 c, Vec3 g, Float alpha){
        cameraPos_P0 = p;
        centerOfInterest_C = c;
        user_upVektor_g = g;
        viewAngle = alpha;

        viewVector = centerOfInterest_C.sub(cameraPos_P0);
        sideVector = viewVector.cross(g);
        upVector = sideVector.cross(viewVector);
    }

    // Input: 2D Pixel Plane with coordinate system with origin in the top left corner
    // Translation into our Viewplane for Raytracer - Coordinate System with origin in the middle, size of 2 (normalized)
    public Vec3 calculateDestinationPoint(Vec3 targetPixel, Vec2 widthAndHeight){

        //calc xNorm and yNorm
        float xNorm = (float) (2 * (targetPixel.x + 0.5)/widthAndHeight.x - 1);
        float yNorm = (float) (2 * (targetPixel.y + 0.5)/widthAndHeight.y - 1);

        //calc aspectRatio and Vhorizontal and Vwidth
        float aspectRatio = widthAndHeight.x / widthAndHeight.y;
        float vHeight = (float) (2 * Math.tan(viewAngle/2) * widthAndHeight.y); 
        float vWidth = (float) (aspectRatio * vHeight);


        //Punkt3D = CamPos + V * focalLength + S * Xnorm *Vb/2 + U * Ynorm *Vh/2
        Vec3 punkt3D = cameraPos_P0.add(viewVector.multScalar(focalLength));
        punkt3D.add(sideVector.multScalar(xNorm * (vWidth/2)));
        punkt3D.add(upVector.multScalar(yNorm * (vHeight/2)));

        //Vec3 direction
        return punkt3D;
    }



}
