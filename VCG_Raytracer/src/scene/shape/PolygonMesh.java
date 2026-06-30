package scene.shape;

import java.util.ArrayList;
import java.util.List;
import raytracer.Ray;
import utils.algebra.Vec3;

public class PolygonMesh extends Shape {

    //list to save all polygons that are a part of this mesh
    private List<Polygon> polygons = new ArrayList<>();

    //no we cant just use default due to how shape works
    public PolygonMesh() {
        super(null, null);
    }

    public void addPolygon(Polygon polygon) {
        polygons.add(polygon);
    }

    //this is basically another closest intersection finder for inside of the mesh
    public Intersection intersect(Ray ray) {

        Intersection closest = null;

        for (Polygon polygon : polygons) {
            Intersection hit = polygon.intersect(ray);

            //ignore if no hit
            if (hit == null || !hit.isHit())
                continue;

            //update closest if nearer than current closest(or if no current closest set this hit as closest)
            if (closest == null || hit.getDistance() < closest.getDistance()) 
                closest = hit;
        }

        return closest;
    }

}