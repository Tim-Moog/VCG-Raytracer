package scene.shape;

import utils.algebra.Vec3;

public class Sphere extends Shape{

    private Vec3 centerPosition;
    private float radius;

    public Sphere(Vec3 pos, float radius) {
        this.centerPosition = pos;
        this.radius = radius;
    }

    public void intersect(){ //glaub das als boolean methode macht mehr sinn außer wir wollen von außen zugriff auf das Intersection object und die IV hit abfragen?
        Intersection intersectionCheck = new Intersection();

    }
    
    
}
