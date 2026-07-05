package scene.shape;

import raytracer.Ray;
import scene.material.Material;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class Polygon extends Shape{ //cant do extend plane here unless we wanna input the normal on constructor which seems meh for custom shapes so instead we'll calc it

    protected Vec3 v1, v2, v3;
    protected Vec3 side12, side23, side31;
    protected Plane myPlane;
    protected Vec3 normal;

    //optional, used for texture mapping
    protected Vec2 uv1, uv2, uv3;//will for now try and calc a medium between the 3 instead to have 1 color per poly to avoid having to do baryzentric coordinates for now
    //will leave this here for future tho
    //update: we dont use these for now and instead calc the avg outside and use avg for polygon diffuse color since i didnt wanna rewrite whole intersect method to barycentric coords system for now

    public Polygon(Vec3 pos1, Vec3 pos2, Vec3 pos3, Material material) {
        super(pos1, material); //this can be any corner pos shouldnt matter which
        
        //setup the corners/vertices
        v1 = pos1;
        v2 = pos2;
        v3 = pos3;

        //setup the edges/sides
        side12 = v2.sub(v1);
        side23 = v3.sub(v2);
        side31 = v1.sub(v3);

        //setup normal and plane on which the polygon lies
        Vec3 side13 = v3.sub(v1); //extra side since we need two sides that are based on same corner for the normal atleast i hope this should make it more consistent as else it just feels odd
        normal = side12.cross(side13).normalize();
        myPlane = new Plane(pos1, normal, material);
    }

    public Polygon(Vec3 pos1, Vec3 pos2, Vec3 pos3, Vec2 _uv1, Vec2 _uv2, Vec2 _uv3, Material material){
        this(pos1, pos2, pos3, material);
        this.uv1 = _uv1;
        this.uv2 = _uv2;
        this.uv3 = _uv3;
    }

    public Intersection intersect(Ray ray){
        //first check if it even hits the plane
        Intersection intersection = myPlane.intersect(ray);
        if (intersection == null || intersection.isHit() == false) {
            return null;
        }

        Vec3 i = intersection.getIntersectionPoint().sub(v1);
        Vec3 e = normal.cross(side12);

        //müssen für alle 3 kanten checken und wenn alle 3 + oder alle - sind dann ist der punkt drinnen
        //so prob easiest with a counter and then make decision based off of that
        int hitsRelativeToSide = 0;
        if(i.scalar(e) > 0)
            hitsRelativeToSide++;

        i = intersection.getIntersectionPoint().sub(v2);
        e = normal.cross(side23);
        if(i.scalar(e) > 0)
            hitsRelativeToSide++;

        i = intersection.getIntersectionPoint().sub(v3);
        e = normal.cross(side31);
        if (i.scalar(e) > 0)
            hitsRelativeToSide++;

        //now we check if all sides were either + or -
        intersection.setHit(hitsRelativeToSide == 3 || hitsRelativeToSide == 0);
        intersection.setShape(this);

        return intersection;
    }

    
}
