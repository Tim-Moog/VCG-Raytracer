package scene.shape;

import raytracer.Ray;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class Sphere extends Shape{

    private Vec3 centerPosition;
    private float radius;

    private float A;
    private float B;
    private float C;
    
    public Sphere(Vec3 pos, float radius) {
        this.centerPosition = pos;
        this.radius = radius;
        this.intersectionData = new Intersection();

    }

    
    public void intersect(Ray ray){
        Vec3 start = ray.getOrigin();
        Vec3 direction = ray.getDirection();

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
                    //case t1 is closer to cam
                    intersectionData.setInRay(ray.calculateRayAt(t1));
                    intersectionData.setOutRay(ray.calculateRayAt(t0));
                }else{
                    //case t0 is closer to cam
                    intersectionData.setInRay(ray.calculateRayAt(t0));
                    intersectionData.setOutRay(ray.calculateRayAt(t1));
                }

                intersectionData.setIntersectionPoint(intersectionData.getInRay());
                Vec3 normal = (intersectionData.getInRay().sub(centerPosition)).multScalar(1/radius);
                intersectionData.setNormal(normal);
                
            } else{
                //right now this else block never gets entered?
                if(t1 < 0 && t0 < 0) // case - -
                    //strahl hinter der kugel
                    System.err.println("- -");
                if(t1 > 0 || t1 > 0)// case + - or - +
                    //strahl in der kugel
                    System.err.println("+ -");
            }
        }
        if(diskriminante == 0){
            //strahl tangiert die kugel demnach sollte es ja nur einen punkt geben
            System.err.println("tangiert");
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
}
