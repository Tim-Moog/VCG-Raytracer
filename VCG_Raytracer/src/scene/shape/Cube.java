package scene.shape;

import raytracer.Ray;
import scene.material.Material;
import utils.algebra.Vec3;

public class Cube extends Square{

    private Square test;
    public Cube(float _size, Vec3 pos, Material material) {
        super(_size, pos, new Vec3(1,0,0), material);
    }

    public Intersection intersect(Ray ray){
        //listen i know what you'll say, I KNOW this is scuff 
        //okay yea cant get this perfectly working so for now ill leave it like this
        //-> basically lighting is scuff

        //we need to save the distance of each side of our cube that gets hit and only return the closest hit AND NOT THE FIRST!!!
        Intersection closest = null;
        float closestDistance = Float.MAX_VALUE;

        this.normal = new Vec3(1, 0, 0);
        test = new Square(this.size, new Vec3(position.x + size/2, position.y, position.z), normal, material);
        Intersection side1 = test.intersect(ray);
        if(side1 != null && side1.isHit() && side1.getDistance() < closestDistance) {
            closest = side1;
            closestDistance = side1.getDistance();
        }

        this.normal = new Vec3(-1, 0, 0);
        test = new Square(this.size, new Vec3(position.x - size/2, position.y, position.z), normal, material);
        Intersection side2 = test.intersect(ray);
        if(side2 != null && side2.isHit() && side2.getDistance() < closestDistance) {
            closest = side2;
            closestDistance = side2.getDistance();
        }

        this.normal = new Vec3(0, 1, 0);
        test = new Square(this.size, new Vec3(position.x, position.y + size/2, position.z), normal, material);
        Intersection side3 = test.intersect(ray);
        if(side3 != null && side3.isHit() && side3.getDistance() < closestDistance) {
            closest = side3;
            closestDistance = side3.getDistance();
        }

        this.normal = new Vec3(0, -1, 0);
        test = new Square(this.size, new Vec3(position.x, position.y - size/2, position.z), normal, material);
        Intersection side4 = test.intersect(ray);
        if(side4 != null && side4.isHit() && side4.getDistance() < closestDistance) {
            closest = side4;
            closestDistance = side4.getDistance();
        }

        this.normal = new Vec3(0, 0, 1);
        test = new Square(this.size, new Vec3(position.x, position.y, position.z + size/2), normal, material);
        Intersection side5 = test.intersect(ray);
        if(side5 != null && side5.isHit() && side5.getDistance() < closestDistance) {
            closest = side5;
            closestDistance = side5.getDistance();
        }

        this.normal = new Vec3(0, 0, -1);
        test = new Square(this.size, new Vec3(position.x, position.y, position.z - size/2), normal, material);
        Intersection side6 = test.intersect(ray);
        if(side6 != null && side6.isHit()&& side6.getDistance() < closestDistance){
            closest = side6;
            closestDistance = side6.getDistance();
        }

        if(closest == null)
            return null;
        return closest;
    }

    
}
