package scene.shape;

import raytracer.Ray;
import scene.material.Material;
import utils.algebra.Vec3;

//a square is just a cutout section of a plane
public class Square extends Plane{

    //default size
    protected float size = 1;

    public Square(float _size, Vec3 pos, Vec3 normal, Material material) {
        super(pos, normal, material);
        this.size = _size;
    }

    public Intersection intersect(Ray ray){
        //get intersection info with the plane this square is based on
        Intersection intersection = super.intersect(ray);

        //if the ray doesnt hit the plane then we can immediately stop
        if(intersection == null)
            return null;

       //compare hit position with where the square lies and its size
        boolean inSquare = true;
        Vec3 intersectionPoint = intersection.getIntersectionPoint();

        float minX = this.position.x - size/2;
        float maxX = this.position.x + size/2;
        float minY = this.position.y - size/2;
        float maxY = this.position.y + size/2;
        float minZ = this.position.z - size/2;
        float maxZ = this.position.z + size/2;

        //if one of the hit coordinates are outside our intended position then we adjust it to not show a hit
        if(intersectionPoint.x > maxX || intersectionPoint.x < minX)
            inSquare = false;
        if(intersectionPoint.y > maxY || intersectionPoint.y < minY)
            inSquare = false;
        if(intersectionPoint.z > maxZ || intersectionPoint.z < minZ)
            inSquare = false;

        //update hit status if not in square
        intersection.setHit(inSquare);

        return intersection;
    }
    

}
