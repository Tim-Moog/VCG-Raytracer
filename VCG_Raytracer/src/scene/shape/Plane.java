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
        //trying without this and manually adding the position in intersect
        //yea dont think we need that and instead can do - when creating the modified ray and + when readjusting at the end like we do with the transformation matrix's in sphere

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

        //Ray transformedRay = createTransformedRay(ray);

        Vec3 ray_origin = ray.getOrigin();
        Vec3 ray_direction = ray.getDirection();

        Q = ray_origin.sub(position).length(); //testing with sub position instead of ray_direction
        
        float check = normal.scalar(ray_direction);
        float t = -(A * ray_origin.x + B * ray_origin.y + C * ray_origin.z + Q)/(A * ray_direction.x + B * ray_direction.y + C * ray_direction.z);
        //think should be the same as
        t = -(normal.scalar(ray_origin) + Q )/ normal.scalar(ray_direction);

        intersection.setHit(check < 0); //glaub der check muss instead != 0 sein -> dann maybe ne abbruch bedingung if not != 0 then just stop
        //oder maybe könnten wir es aber auch vereinfachen?(laut folien wärs != 0 aber cornell box wär eher <0 id say)
        //also nur den check < 0 bereich zu betrachten da wir in der cornellbox ja strahlen innerhalb der box behalten wollen und keine von draußen rein kommen sollen?
        //i think that would make sense and make things less complicated
        intersection.setShape(this);
        intersection.setNormal(normal);
        intersection.setDistance(ray.calculateRayAt(t).length());
        intersection.setInRay(ray.calculateRayAt(t));
        intersection.setIntersectionPoint(ray_origin.add(ray_direction.multScalar(t)));

        //intersection.setIntersectionPoint(myTransformationMatrix.multVec3(ray_origin.add(ray_direction.multScalar(t)), true));
        //intersection.setInRay(transformedRay.calculateRayAt(t));

        //intersection.setIntersectionPoint(myTransformationMatrix.multVec3(ray_origin.add(position), true)); //this combined with q = ray_position.sub(position).length() results in closer
        return intersection;
    }

    
    
}
