package scene.shape;

import raytracer.Ray;
import scene.material.Material;
import utils.algebra.Matrix4x4;
import utils.algebra.Vec3;
import utils.algebra.Vec4;

public class Plane extends Shape {

    protected Matrix4x4 myTransformationMatrix;
    protected Matrix4x4 myInverseMatrix;

    private Vec3 localNormal = new Vec3(0, 1, 0);
    protected Vec3 normal;

    public Plane(Vec3 pos, Vec3 normal, Material material) {
        super(pos, material);
        this.normal = normal;

        setupMatrix(pos, normal.normalize());
        this.myInverseMatrix = myTransformationMatrix.invert();
    }

    private void setupMatrix(Vec3 pos, Vec3 normal) {
        // startingpoint for plane:
        // local Plane: y = 0, Normal (0,1,0) --> this is pretty much the same as in sphere where we take as startingpoint 0,0,0 with radius 1

        Matrix4x4 rotation = Matrix4x4.rotationFromTo(new Vec3(0, 1, 0), normal);
        Matrix4x4 translation = new Matrix4x4().translateXYZW(new Vec4(pos.x, pos.y, pos.z, 1));
        //first we rotate then we translate
        myTransformationMatrix = translation.mult(rotation);
    }

    public Ray createTransformedRay(Ray ray) {
        Vec3 newOrigin = this.myInverseMatrix.multVec3(ray.getOrigin(), true);
        Vec3 newDirection = this.myInverseMatrix.multVec3(ray.getDirection(), false).normalize();

        return new Ray(newOrigin, newDirection);
    }


    public Intersection intersect(Ray ray) {
        Intersection intersection = new Intersection();

        Ray transformedRay = createTransformedRay(ray);

        Vec3 rayOrigin = transformedRay.getOrigin();
        Vec3 rayDirection = transformedRay.getDirection();

        float check = localNormal.scalar(rayDirection);

        //treffen wir die plane? -> safety check to make sure the ray isnt going parallel to the plane
        //basically step size on Y -> if 0 we never hit plane
        if (Math.abs(check) < 0.0001f) {
            return null;
        }

        // local Plane we've defined as y = 0
        float t = -rayOrigin.y / check;

        //distance check to make sure the hit is infront of us and not behind us
        if (t <= 0.0001f) {
            return null;
        }

        //transform everything to global coordinate system
        Vec3 localIntersection = rayOrigin.add(rayDirection.multScalar(t));
        Vec3 globalIntersection = myTransformationMatrix.multVec3(localIntersection, true);

        //we do this here now since else i think it would break later if we end up doing non-uniform scaling
        Vec3 globalNormal = myInverseMatrix.transpose().multVec3(localNormal, false).normalize();

        intersection.setHit(true);
        intersection.setDistance(globalIntersection.sub(ray.getOrigin()).length());
        intersection.setIntersectionPoint(globalIntersection);
        intersection.setNormal(globalNormal); //why not just use normal? -> read comment above globalNormal
        intersection.setShape(this);
        intersection.setInRay(ray);

        return intersection;
    }
}