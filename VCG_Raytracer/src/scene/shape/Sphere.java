package scene.shape;

import raytracer.Ray;
import scene.material.Material;
import utils.algebra.Matrix4x4;
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

    public void addInvTranslation (Vec3 _pos) { //why did we name this inv? when its not inverse??
        Vec4 homoTrans = new Vec4(_pos.x, _pos.y, _pos.z, 1);
        myTransformationMatrix = myTransformationMatrix.translateXYZW(homoTrans);
    }

    public void addScaleUni (double _scale) {
        myTransformationMatrix = myTransformationMatrix.scale(_scale);
    }

    public Ray createTransformedRay(Ray _ray){

        Vec3 newOrigin = this.myInverseMatrix.multVec3(_ray.getOrigin(), true);
        Vec3 newDirection = this.myInverseMatrix.multVec3(_ray.getDirection(), false).normalize();
        return new Ray(newOrigin, newDirection);
    }

    @Override
    public Intersection intersect (Ray ray) {
        Intersection intersection = new Intersection();

        //transforms Ray into local coordinates
        Ray transformedRay = createTransformedRay(ray);

        Vec3 local_ray_origin = transformedRay.getOrigin();
        Vec3 local_ray_direction = transformedRay.getDirection();

        //A = (float) Math.pow(checkRay.x, 2) + (float) Math.pow(checkRay.y, 2) + (float) Math.pow(checkRay.z, 2);
        //ist das selbe wie
        Float A = local_ray_direction.scalar(local_ray_direction);
        //B = (float) (2 * (camPos.x * checkRay.x + camPos.y * checkRay.y + camPos.z * checkRay.z));
        //ist das selbe wie
        Float B = 2.0f * local_ray_origin.scalar(local_ray_direction);
        //C = (float) (Math.pow(camPos.x,2) + Math.pow(camPos.y,2) + Math.pow(camPos.z,2) - Math.pow(radius,2));
        //ist das selbe wie
        Float C = local_ray_origin.scalar(local_ray_origin) - radius * radius;

        float diskriminante = (float) (Math.pow(B, 2) - 4 * C);
        
        // Did we hit a sphere in line of sight?
        intersection.setHit(diskriminante >= 0);
        // Stop if we did not hit, or hit is behind camera
        if(diskriminante < 0)
            return null;

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

        // no t is infront of us (behind camera or inside shape)
        if (t <= 0){
             return null;
        }

        // We need: local intersection point
        Vec3 local_intersection = local_ray_origin.add(local_ray_direction.multScalar(t));
        // local normal (position could be left out, as it's (0,0,0) in local coordinates)
        Vec3 local_normal = local_intersection.normalize();
        //Transform into global coordinates
        Vec3 global_intersection = myTransformationMatrix.multVec3(local_intersection, true);
        Vec3 global_normal = myTransformationMatrix.invert().transpose().multVec3(local_normal, false).normalize();
 
 
        System.out.println("Inc Ray: " + global_intersection.sub(ray.getOrigin()).length() + " | Trans Ray: " + local_intersection.sub(ray.getOrigin()).length());

        
        intersection.setDistance(global_intersection.sub(ray.getOrigin()).length());
        intersection.setIntersectionPoint(global_intersection);
        intersection.setNormal(global_normal);
        intersection.setShape(this);
        intersection.setInRay(ray);
        // OutRay is set in Material Shadow calc if needed

        return intersection;
    }

}
