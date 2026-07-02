package scene.shape;

import raytracer.Ray;
import utils.algebra.Vec3;

//i think this makes sense to not be extending shape as its not a shape itself?
//also added it as an IV to shape and will edit methods to check if a shape has a BB? actually think that might make problems with others that dont have a BB hmmmm

//actually maybe it would make more sense other way around that the BB saves the mesh/shape
//cuz then i think we could save the boxes in our shapelist and work off of this intersect method
//yea i think this would be easier but this will also then require it to be a shape again since else we cant save it in our shapelist
public class BoundingBox extends Shape{

    //hard set to a mesh to avoid possibility of infinite loop
    private PolygonMesh myMesh;

    private Vec3 min;
    private Vec3 max;

    public BoundingBox(PolygonMesh _mesh, Vec3 _min, Vec3 _max){
        this.myMesh = _mesh;
        this.min = _min;
        this.max = _max;
    }

    public Intersection intersect(Ray checkRay) {
        //set default to no hit
        Intersection intersection = new Intersection();
        intersection.setHit(false);

        //check if we hit the BoundingBox around our mesh
        if(hitsBoundingBox(checkRay)){
            //if we do then we check for an intersection on the actual mesh
            intersection = myMesh.intersect(checkRay);
        }

        return intersection;
    }


    //checks if there is any valid t one can put into the ray formular for it to hit the box on all 3 axis
    //this is done by checking each axis one by one and adjusting the interval where the ray hits it based on each new axis we account for
    public boolean hitsBoundingBox(Ray ray){
        //check x interval of where the ray would hit the box
        double tx1 = (min.x - ray.getOrigin().x) / ray.getDirection().x;
        double tx2 = (max.x - ray.getOrigin().x) / ray.getDirection().x;

        //save min and max for x hit
        double tmin = Math.min(tx1, tx2);
        double tmax = Math.max(tx1, tx2);

        //check y interval for where the ray would hit the box
        double ty1 = (min.y - ray.getOrigin().y) / ray.getDirection().y;
        double ty2 = (max.y - ray.getOrigin().y) / ray.getDirection().y;

        //update min and max 
        //if we hit on x between 2 and 8 but on y only from 3-7 
        //then the new hit min and max where the ray hits the box is 3 and 7, as 2 and 8 would hit on x but not on y
        tmin = Math.max(tmin, Math.min(ty1, ty2));
        tmax = Math.min(tmax, Math.max(ty1, ty2));

        //check z interval of where the ray would hit the box
        double tz1 = (min.z - ray.getOrigin().z) / ray.getDirection().z;
        double tz2 = (max.z - ray.getOrigin().z) / ray.getDirection().z;

        //update based on z interval
        //same thing again update the min to be the higher min where all 3 axis get hit
        //and update the max to be the lower max as the higher wouldnt hit all 3
        tmin = Math.max(tmin, Math.min(tz1, tz2));
        tmax = Math.min(tmax, Math.max(tz1, tz2));

        //if we then still have an interval between min and max(with min ofc being smaller)
        //then the ray does hit the box else it does not hit it on all 3 axis at the same time
        return tmax >= Math.max(tmin, 0);
    }
    
}
