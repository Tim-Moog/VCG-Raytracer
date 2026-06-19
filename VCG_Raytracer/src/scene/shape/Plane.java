package scene.shape;

import raytracer.Ray;
import scene.material.Material;
import utils.algebra.Matrix4x4;
import utils.algebra.Vec3;
import utils.algebra.Vec4;

/* 
public class Plane extends Shape {

    Vec3 normal;
    Float A,B,C, Q;

    Matrix4x4 myTransformationMatrix;
    Matrix4x4 myInverseMatrix;

    public Plane(Vec3 _pos, Vec3 _normal, Material _material) {
        super(_pos, _material);
        //i think this is the only extra thing we need for a plane?
        this.normal = _normal.normalize();
        //ABC UND Q SETZEN
        A = this.normal.x;
        B = this.normal.y;
        C = this.normal.z;

        //Q needs to be constant based off of the normal and plane position
        Q = -normal.scalar(position);


        this.myTransformationMatrix = new Matrix4x4();
        this.addInvTranslation(_pos);
        this.myInverseMatrix = myTransformationMatrix.invert();
        intersectionData.setShape(this);
    }

    public void addInvTranslation (Vec3 _pos) { //why did we name this inv? when its not inverse??
        Vec4 homoTrans = new Vec4(_pos.x, _pos.y, _pos.z, 1);
        myTransformationMatrix = myTransformationMatrix.translateXYZW(homoTrans);
    }


    public Ray createTransformedRay(Ray _ray){
        Vec3 newOrigin = this.myInverseMatrix.multVec3(_ray.getOrigin(), true);
        Vec3 newDirection = this.myInverseMatrix.multVec3(_ray.getDirection(), false).normalize();
        return new Ray(newOrigin, newDirection);
    }

    public Intersection intersect(Ray ray){
        Intersection intersection = new Intersection();

        Ray transformedRay = createTransformedRay(ray);

        Vec3 ray_origin = transformedRay.getOrigin();
        Vec3 ray_direction = transformedRay.getDirection().add(position);
        
        float check = normal.scalar(ray_direction);
        float t = -(A * ray_origin.x + B * ray_origin.y + C * ray_origin.z + Q)/(A * ray_direction.x + B * ray_direction.y + C * ray_direction.z);
        //think should be the same as
        t = -(normal.scalar(ray_origin) + Q )/ normal.scalar(ray_direction);

        //check < 0 to see if we are hitting the plane from the front -> as long as we dont have a plane between a shape and a light this should be fine
        intersection.setHit(check < 0); 
        intersection.setShape(this);
        intersection.setNormal(normal);
        intersection.setDistance(ray.calculateRayAt(t).length());
        //System.err.println(intersectionData.getDistance());
        intersection.setIntersectionPoint(ray_origin.add(ray_direction.multScalar(t)));


        // We need: local intersection point
        Vec3 local_intersection = ray_origin.add(ray_direction.multScalar(t));
        //Transform into global coordinates
        Vec3 global_intersection = myTransformationMatrix.multVec3(local_intersection, true);

        intersection.setDistance(global_intersection.sub(ray.getOrigin()).length());
        intersection.setIntersectionPoint(global_intersection);
        intersection.setShape(this);
        intersection.setInRay(ray);
        // OutRay is set in Material Shadow calc if needed

        return intersection;
    }
    
}*/


//testing version 
public class Plane extends Shape {

    protected Matrix4x4 myTransformationMatrix;
    protected Matrix4x4 myInverseMatrix;

    private Vec3 localNormal = new Vec3(0, 1, 0);
    protected Vec3 normal;

    public Plane(Vec3 pos, Vec3 normal, Material material) {
        super(pos, material);
        this.normal = normal;

        this.myTransformationMatrix = new Matrix4x4();

        setupMatrix(pos, normal.normalize());

        this.myInverseMatrix = myTransformationMatrix.invert();
        intersectionData.setShape(this);
    }

    private void setupMatrix(Vec3 pos, Vec3 normal) {
        // lokale Plane: y = 0, Normal (0,1,0)
        // jetzt Matrix bauen, die (0,1,0) auf normal rotiert
        Matrix4x4 rotation = Matrix4x4.rotationFromTo(new Vec3(0, 1, 0), normal);

        Matrix4x4 translation = new Matrix4x4().translateXYZW(new Vec4(pos.x, pos.y, pos.z, 1));

        // Reihenfolge wie bei Sphere: erst Rotation, dann Translation
        myTransformationMatrix = translation.mult(rotation);
    }

    public Ray createTransformedRay(Ray ray) {
        Vec3 newOrigin = this.myInverseMatrix.multVec3(ray.getOrigin(), true);
        Vec3 newDirection = this.myInverseMatrix.multVec3(ray.getDirection(), false).normalize();

        return new Ray(newOrigin, newDirection);
    }

    @Override
    public Intersection intersect(Ray ray) {
        Intersection intersection = new Intersection();

        Ray transformedRay = createTransformedRay(ray);

        Vec3 localOrigin = transformedRay.getOrigin();
        Vec3 localDirection = transformedRay.getDirection();

        float check = localNormal.scalar(localDirection);

        if (Math.abs(check) < 0.0001f) {
            return null;
        }

        // lokale Plane ist y = 0
        float t = -localOrigin.y / check;

        if (t <= 0.0001f) {
            return null;
        }

        Vec3 localIntersection = localOrigin.add(localDirection.multScalar(t));
        Vec3 globalIntersection = myTransformationMatrix.multVec3(localIntersection, true);

        //isnt this completely overkill like we dont need this since we can simply save the normal from constructor and set that
        Vec3 globalNormal = myInverseMatrix
            .transpose()
            .multVec3(localNormal, false)
            .normalize();

        intersection.setHit(true);
        intersection.setDistance(globalIntersection.sub(ray.getOrigin()).length());
        intersection.setIntersectionPoint(globalIntersection);
        intersection.setNormal(normal);//changed this from gloabalNormal to normal given in constructor
        intersection.setShape(this);
        intersection.setInRay(ray);

        return intersection;
    }
}