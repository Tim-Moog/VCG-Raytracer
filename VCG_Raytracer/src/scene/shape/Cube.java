package scene.shape;

import raytracer.Ray;
import scene.material.Material;
import utils.algebra.Vec3;

public class Cube extends Square{

    
    private Square[] cubeSides;

    public Cube(float _size, Vec3 pos, Material material) {
        super(_size, pos, new Vec3(1,0,0), material);
        //save all sides of our cube in an array
        cubeSides = new Square[6];
        //top, bot
        cubeSides[0] = new Square(_size, new Vec3(pos.x, pos.y + size/2, pos.z), new Vec3(0,1,0), material);
        cubeSides[1] = new Square(_size, new Vec3(pos.x, pos.y - size/2, pos.z), new Vec3(0,-1,0), material);
        //right, left
        cubeSides[2] = new Square(_size, new Vec3(pos.x + size/2, pos.y, pos.z), new Vec3(1,0,0), material);
        cubeSides[3] = new Square(_size, new Vec3(pos.x - size/2, pos.y, pos.z), new Vec3(-1,0,0), material);
        //front, back
        cubeSides[4] = new Square(_size, new Vec3(pos.x, pos.y, pos.z + size/2), new Vec3(0,0,1), material);
        cubeSides[5] = new Square(_size, new Vec3(pos.x, pos.y, pos.z - size/2), new Vec3(0,0,-1), material);
    }

    public Intersection intersect(Ray ray){
        //we need to save the distance of each side of our cube that gets hit and only return the closest hit AND NOT THE FIRST!!!
        Intersection closest = null;
        float closestDistance = Float.MAX_VALUE;

       //moved creation of sides from here to constructor since i was dumb and theres no reason for it to be here(simply didnt want all sides saved back then)
       //this is pretty much same as prev 6 if's just instead as a for-each since it is just wayyyyy cleaner
       for (Square side : cubeSides) {
            Intersection intersection = side.intersect(ray);

            //if theres been a hit and its closer we overwrite closest
            if(intersection != null && intersection.isHit() && intersection.getDistance() < closestDistance){
                closest = intersection;
                closestDistance = intersection.getDistance();
            }
       }


        if(closest == null)
            return null;
        return closest;
    }
    
}
