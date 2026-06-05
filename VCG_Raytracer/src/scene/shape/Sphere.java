package scene.shape;

import raytracer.Ray;
import scene.material.Material;
import utils.algebra.Matrix4x4;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.algebra.Vec4;

public class Sphere extends Shape{

    private float radius;

    private float A;
    private float B;
    private float C;

    private Matrix4x4 myTransformationMatrix;
    private Matrix4x4 myInverseMatrix;

    
    public Sphere(Vec3 pos, float radius, Material _material) {
        super(pos, _material);
        //might change the radius setting later
        this.radius = 1; //this is fine due to us doing scale in transformationmatrix, without that we'd need radius set correctly here
        this.myTransformationMatrix = new Matrix4x4();
        this.setupMatrix(pos, radius);
        this.myInverseMatrix = myTransformationMatrix.invert();
        intersectionData.setShape(this);
    }

    public void setupMatrix(Vec3 _pos, float _rad){
        //scale first
        addScaleUni(_rad);
        //then translate
        addInvTranslation(_pos);
    }

    public void addInvTranslation (Vec3 _pos) {
        Vec4 homoTrans = new Vec4(_pos.x, _pos.y, _pos.z, 1);
        myTransformationMatrix = myTransformationMatrix.translateXYZW(homoTrans);
    }

    public void addScaleUni (double _scale) {
        myTransformationMatrix = myTransformationMatrix.scale(_scale);
    }

    public Ray createTransformedRay(Ray _ray){
        //richtung bleibt
        Vec3 newDirection = _ray.getDirection();
        //müssen nur den startpunkt bearbeiten mit der inverse
        Vec3 newOrigin = this.myInverseMatrix.multVec3(_ray.getOrigin(), true);

        //return ray with modified data
        return new Ray(newOrigin, newDirection);
    }


    public Intersection intersect (Ray ray) {
        Intersection intersection = new Intersection();

        //very sure this is needed for transformations to work
        Ray transformedRay = createTransformedRay(ray);

        Vec3 ray_origin = transformedRay.getOrigin();
        Vec3 ray_direction = transformedRay.getDirection();

        //A = (float) Math.pow(checkRay.x, 2) + (float) Math.pow(checkRay.y, 2) + (float) Math.pow(checkRay.z, 2);
        //ist das selbe wie
        Float A = ray_direction.scalar(ray_direction);
        //B = (float) (2 * (camPos.x * checkRay.x + camPos.y * checkRay.y + camPos.z * checkRay.z));
        //ist das selbe wie
        Float B = 2.0f * ray_origin.scalar(ray_direction);
        //C = (float) (Math.pow(camPos.x,2) + Math.pow(camPos.y,2) + Math.pow(camPos.z,2) - Math.pow(radius,2));
        //ist das selbe wie
        Float C = ray_origin.scalar(ray_origin) - radius * radius;

        float diskriminante = (float) (Math.pow(B, 2) - 4 * C);
        
        // Did we hit a sphere in line of sight?
        intersection.setHit(diskriminante >= 0);
        // Stop if we did not hit, or hit is behind camera
        if(diskriminante < 0)
            return intersection;

        //calculating t's
        float t0 = (float) (-B - Math.sqrt(diskriminante))/2;
        float t1 = (float) (-B + Math.sqrt(diskriminante))/2;

        // Deciding, which t is first contact infront of us
        float t = -1;
        if (t0 > 0 && t1 > 0) {
            // Smaller one is chosen, closer to camera
            t = Math.min(t0, t1);
        } else if (t0 > 0) {
            // The one that is positive is chosen
            t = t0;
        } else if (t1 > 0) {
            t = t1;
        }

        // Giving over information to Intersection
        // Hit is already set
        intersection.setDistance(t);// set distance to t / alternative would be actually getting the length but idk if thats wanted atm
        // for the point and normal we must transform the ray back(reminder we created it with the inverse so we use normal for going back)
        intersection.setIntersectionPoint(myTransformationMatrix.multVec3(ray_origin.add(ray_direction.multScalar(t)), true));// TransformationMatrix * transformedRay start, into normalized direction * distance
        intersection.setNormal(myTransformationMatrix.multVec3(intersection.getIntersectionPoint().sub(position).normalize(), false)); // TransformationMatrix * Vector between intersection and sphere center = normal
        intersection.setShape(this); // for material
        intersection.setInRay(transformedRay.calculateRayAt(t));  // primary Ray

        return intersection;
    }

}
