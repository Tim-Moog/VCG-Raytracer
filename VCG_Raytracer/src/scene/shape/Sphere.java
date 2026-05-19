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
    }


    
    public void intersect(Intersection intersectionData, Ray ray){
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
    }
    
}
