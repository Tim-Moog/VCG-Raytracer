package scene.shape;

import raytracer.Ray;
import scene.material.Material;
import utils.algebra.Matrix4x4;
import utils.algebra.Vec3;
import utils.algebra.Vec4;

public class Plane extends Shape {

    Vec3 normal;
    Float A,B,C, Q;

    private Matrix4x4 myTransformationMatrix;
    private Matrix4x4 myInverseMatrix;

    public Plane(Vec3 _pos, Vec3 _normal, Material _material) {
        super(_pos, _material);
        //i think this is the only extra thing we need for a plane?
        this.normal = _normal.normalize();
        //ABC UND Q SETZEN
        A = this.normal.x;
        B = this.normal.y;
        C = this.normal.z;
        //Q ist Abstand zum Ursprung -> prob makes more sense to set in intersect method as it will differ for each point of the plane
        

        //think we need the transformationsmatrix for translation
        this.myTransformationMatrix = new Matrix4x4();
        Vec4 homoTrans = new Vec4(_pos.x, _pos.y, _pos.z, 1);
        myTransformationMatrix = myTransformationMatrix.translateXYZW(homoTrans);
        this.myInverseMatrix = myTransformationMatrix.invert();

    }

    public Ray createTransformedRay(Ray _ray){
        //richtung bleibt
        Vec3 newDirection = _ray.getDirection();
        //müssen nur den startpunkt bearbeiten mit der inverse
        Vec3 newOrigin = this.myInverseMatrix.multVec3(_ray.getOrigin(), true);

        //return ray with modified data
        return new Ray(newOrigin, newDirection);
    }



    public Intersection intersect(Ray ray){
        Intersection intersection = new Intersection();

        Ray transformedRay = createTransformedRay(ray);

        Vec3 ray_origin = transformedRay.getOrigin();
        Vec3 ray_direction = transformedRay.getDirection();

        Q = ray_direction.sub(ray_origin).length();
        

        //idk if this is whats meant with the D in der formel
        float diskriminante = (float) (Math.pow(B, 2) - 4 * C);
        float t = -(A * ray_origin.x + B * ray_origin.y + C * ray_origin.z + Q)/(A * ray_direction.x + B * ray_direction.y + C * ray_direction.z);

        intersection.setHit(diskriminante > 0);
        intersection.setShape(this);
        intersection.setNormal(normal);
        intersection.setDistance(t);
        intersection.setInRay(ray.calculateRayAt(t));
        intersection.setIntersectionPoint(ray_origin.add(ray_direction.multScalar(t)));

        intersection.setIntersectionPoint(myTransformationMatrix.multVec3(ray_origin.add(ray_direction.multScalar(t)), true));
        //intersection.setNormal(myTransformationMatrix.multVec3(intersection.getIntersectionPoint().sub(position).normalize(), false));
        intersection.setInRay(ray.calculateRayAt(t));

        return intersection;
    }

    
    
}
