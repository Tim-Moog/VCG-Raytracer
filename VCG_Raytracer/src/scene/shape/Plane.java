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

/* 

//testing other groups version
public class Plane extends Shape {

    protected Vec3 normal;

    public Plane(Vec3 _position, Vec3 _normal, Material _material){
        super(_position, _material);
        normal = _normal.normalize();
    }    

    public Intersection intersect(Ray inRay) {
        
        Intersection intersection = new Intersection();
        //RÃ¼ckgabewerte
        boolean hit = false;
        Vec3 intersectionPoint = null;
        Vec3 _normal = null;
        float distance = -1;
        Ray outRay = null;

        Vec3 rayDirection = inRay.getDirection().normalize();
        float pScalarD= normal.scalar(rayDirection);
        
        if(pScalarD < 0){
            Vec3 rayStartpoint = inRay.getOrigin();

            float planeDistance = position.sub(rayStartpoint).length();
            float t = -(normal.scalar(rayStartpoint)+planeDistance)/pScalarD;

            if(t>0){
                hit = true;
                distance = t;

                intersectionPoint = rayStartpoint.add(rayDirection.multScalar(distance));
                _normal = normal;
            }
        }

        intersection.setIntersectionPoint(intersectionPoint);
        intersection.setNormal(_normal);
        intersection.setInRay(inRay);
        intersection.setOutRay(outRay);
        intersection.setHit(hit);
        intersection.setShape(this);
        intersection.setDistance(distance);

        
        return intersection; 
    }

    
}*/


//testing version chatti
public class Plane extends Shape {

    private Matrix4x4 myTransformationMatrix;
    private Matrix4x4 myInverseMatrix;

    private Vec3 localNormal = new Vec3(0, 1, 0);

    public Plane(Vec3 pos, Vec3 normal, Material material) {
        super(pos, material);

        this.myTransformationMatrix = new Matrix4x4();

        setupMatrix(pos, normal.normalize());

        this.myInverseMatrix = myTransformationMatrix.invert();
        intersectionData.setShape(this);
    }

    private void setupMatrix(Vec3 pos, Vec3 normal) {
        // lokale Plane: y = 0, Normal (0,1,0)
        // jetzt Matrix bauen, die (0,1,0) auf normal rotiert
        Matrix4x4 rotation = Matrix4x4.rotationFromTo(new Vec3(0, 1, 0), normal);

        Matrix4x4 translation = new Matrix4x4()
            .translateXYZW(new Vec4(pos.x, pos.y, pos.z, 1));

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

        float denom = localNormal.scalar(localDirection);

        if (Math.abs(denom) < 0.0001f) {
            return null;
        }

        // lokale Plane ist y = 0
        float t = -localOrigin.y / denom;

        if (t <= 0.0001f) {
            return null;
        }

        Vec3 localIntersection = localOrigin.add(localDirection.multScalar(t));
        Vec3 globalIntersection = myTransformationMatrix.multVec3(localIntersection, true);

        Vec3 globalNormal = myInverseMatrix
            .transpose()
            .multVec3(localNormal, false)
            .normalize();

        intersection.setHit(true);
        intersection.setDistance(globalIntersection.sub(ray.getOrigin()).length());
        intersection.setIntersectionPoint(globalIntersection);
        intersection.setNormal(globalNormal);
        intersection.setShape(this);
        intersection.setInRay(ray);

        return intersection;
    }
}