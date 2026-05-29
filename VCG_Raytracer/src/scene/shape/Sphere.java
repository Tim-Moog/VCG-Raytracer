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
        super(new Vec3(0,0,0), _material);
        this.radius = 1;
        this.intersectionData = new Intersection();
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
    //erst inverse dann schnittpunkt dann schnittpunkt berechnen
    //darauf dann die normale matrix drauf anwenden 

    
    public void intersect(Ray ray){
        Ray transformedRay = createTransformedRay(ray);
        Vec3 start = transformedRay.getOrigin();
        Vec3 direction = transformedRay.getDirection();

        //A = (float) Math.pow(checkRay.x, 2) + (float) Math.pow(checkRay.y, 2) + (float) Math.pow(checkRay.z, 2);
        //ist das selbe wie
        A = direction.scalar(direction);
    
        //B = (float) (2 * (camPos.x * checkRay.x + camPos.y * checkRay.y + camPos.z * checkRay.z));
        //ist das selbe wie
        B = 2.0f * start.scalar(direction);

        //C = (float) (Math.pow(camPos.x,2) + Math.pow(camPos.y,2) + Math.pow(camPos.z,2) - Math.pow(radius,2));
        //ist das selbe wie
        C = start.scalar(start) - radius * radius;

        float diskriminante = (float) (Math.pow(B, 2) - 4 * C);
        
        intersectionData.setHit(diskriminante >= 0);
        if(diskriminante < 0)
            return;


        //calculating t's
        float t0 = (float) (- B - Math.sqrt(diskriminante))/2;
        float t1 = (float) (- B + Math.sqrt(diskriminante))/2;

        //decide which one is first point of contact in front of us 
        float t;
        if (diskriminante > 0){
            if(t1 > 0 && t0 > 0){// case + +
                //strahl trifft die kugel vor uns
                if(t1 < t0){
                    t=t1;
                    //case t1 is closer to cam
                    intersectionData.setInRay(ray.calculateRayAt(t1));
                    intersectionData.setOutRay(ray.calculateRayAt(t0));
                }else{
                    //case t0 is closer to cam
                    t=t0;
                    intersectionData.setInRay(ray.calculateRayAt(t0));
                    intersectionData.setOutRay(ray.calculateRayAt(t1));
                }
                /* 
                intersectionData.setIntersectionPoint(intersectionData.getInRay());
                Vec3 normal = (intersectionData.getInRay().sub(centerPosition)).multScalar(1/radius);
                intersectionData.setNormal(normal);*/

                //testing Nicoles - me thinks this more correct
                intersectionData.setIntersectionPoint(myTransformationMatrix.multVec3(start.add(direction.multScalar(t)), true));
                intersectionData.setNormal(myTransformationMatrix.multVec3(intersectionData.getIntersectionPoint().normalize(), false));
                System.err.println("hit");
                
            } else{
                System.err.println("entered else that has never before been entered");
                //right now this else block never gets entered?
                //if(t1 < 0 && t0 < 0) // case - -
                    //strahl hinter der kugel
                    //System.err.println("- -");
                //if(t1 > 0 || t1 > 0)// case + - or - +
                    //strahl in der kugel
                    //System.err.println("+ -");
            }
        }
        if(diskriminante == 0){
            //strahl tangiert die kugel demnach sollte es ja nur einen punkt geben
            //System.err.println("tangiert");
            t = t0;
        }


        //calculate distance
        //honestly dont think this is needed rn 
        //intersectionData.setDistance(t); //prob not t but length of a the inRay vector

        /* moved this up to into the if 
        intersectionData.setIntersectionPoint(intersectionData.getInRay());
        Vec3 normal = (intersectionData.getInRay().sub(centerPosition)).multScalar(1/radius);
        intersectionData.setNormal(normal);
            */
        


    }
    
    public Intersection getIntersectionData(){
        return this.intersectionData;
    }

    public Material getMaterial () {
        return this.material;
    }


    //adding other version for testing
    public Intersection intersect (Ray ray, int filler) {
        Intersection intersection = new Intersection();

        Vec3 ray_origin = ray.getOrigin();
        Vec3 ray_direction = ray.getDirection();

        //A = (float) Math.pow(checkRay.x, 2) + (float) Math.pow(checkRay.y, 2) + (float) Math.pow(checkRay.z, 2);
        //ist das selbe wie
        Float a = ray_direction.scalar(ray_direction);
        //B = (float) (2 * (camPos.x * checkRay.x + camPos.y * checkRay.y + camPos.z * checkRay.z));
        //ist das selbe wie
        Float b = 2.0f * ray_origin.scalar(ray_direction);
        //C = (float) (Math.pow(camPos.x,2) + Math.pow(camPos.y,2) + Math.pow(camPos.z,2) - Math.pow(radius,2));
        //ist das selbe wie
        Float c = ray_origin.scalar(ray_origin) - radius * radius;

        float diskriminante = (float) (Math.pow(b, 2) - 4 * c);
        
        // Did we hit a sphere in line of sight?
        intersection.setHit(diskriminante >= 0);
        // Stop if we did not hit, or hit is behind camera
        if(diskriminante < 0)
            return intersection;

        //calculating t's
        float t0 = (float) (- b - Math.sqrt(diskriminante))/2;
        float t1 = (float) (- b + Math.sqrt(diskriminante))/2;

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
        intersection.setDistance(t);
        intersection.setIntersectionPoint(ray_origin.add(ray_direction.multScalar(t))); // Ray start, into normalized direction * distance
        intersection.setNormal(intersection.getIntersectionPoint().sub(position).normalize()); // Vector between intersection and sphere center = normal
        intersection.setShape(this); // for material
        intersection.setInRay(ray.calculateRayAt(t)); // primary Ray

        return intersection;
    }

}
