package scene.shape;

import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class Sphere extends Shape{

    private Vec3 centerPosition;
    private float radius;

    private float A;
    private float B;
    private float C;
    

    //MAIN CURRENT ISSUE
    // NEED TO CONVERT THE SPHERE TO NORMALIZED COORDINATE SYSTEM SINCE IT TECHNICALLY WORKS
    // BUT THE SPHERE IS LIKE 3 PIXELS ATM DUE TO RADIUS=1 AND NO CANT JUST SCALE THAT ELSE IT BREAK 

    public Sphere(Vec3 pos, float radius) {
        this.centerPosition = pos;
        this.radius = radius;
    }

    public boolean intersect(Vec3 checkRay, Vec3 camPos, Vec2 currentPixel){ //glaub das als boolean methode macht mehr sinn außer wir wollen von außen zugriff auf das Intersection object und die IV hit abfragen?
        Intersection intersectionCheck = new Intersection();

        A = (float) Math.pow(checkRay.x, 2) + (float) Math.pow(checkRay.y, 2) + (float) Math.pow(checkRay.z, 2);
    
        B = (float) (2 * (camPos.x * checkRay.x + camPos.y * checkRay.y + camPos.z * checkRay.z));

        C = (float) (Math.pow(camPos.x,2) + Math.pow(camPos.y,2) + Math.pow(camPos.z,2) - Math.pow(radius,2));

        float determinante = (float) (Math.pow(B, 2) - 4 * C);

        float t0 = (float) ((-B - Math.sqrt(determinante))/2);
        float t1 = (float) ((-B + Math.sqrt(determinante))/2);

        //trying something outcommented below for this -----
        //kleineres t ist first point of contact
        float t;
        if(t0 < t1 && t0 > 0){
            t = t0;
        }else{
            t = t1;
        }
        float calc = A * (float) Math.pow(t, 2) + B * t + C;
        

        //end of trying block ----- hmmmm same result

        
        if(determinante < 0)
            return false;




        //better to move these prints to below a conditional return false 
        System.out.println("current pixel pos: " + currentPixel.toString());
        System.out.println("A has the value: " + A);
        System.out.println("B has the value: " + B);
        System.out.println("C has the value: " + C);
        System.out.println("Determinante: " + determinante); 
        System.out.println("t0 has the value: " + t0);
        System.err.println("t1 has the value: " + t1); 


        return true; 
    }
    
    /*
        müssen dann die beiden gleichungen gleich setzen und nach t umformen
        A = xD^2 + yD^2 + zD^2 = 1 -> Richtung normalisieren!
        B = 2(x0 *xd + y0 *yd + z0 *zd)
        C = x0^2 + y0^2 + z0^2 - r^2
        t0 = (-B -root(B^2 - 4C))/2
        t1 = (-B +root(B^2 - 4C))/2

        innerhalb von t0 und t1 ist die diskriminante
        d = B^2 - 4C
        check d < 0 -> keine reelle Lösung
            kein schnitt -> no clip dont need to do more
        check d = 0 -> eine reelle Doppellösung
            strahl tangiert die kugel -> genau an ecke der kugel
        check d > 0 -> 2 reelle Lösungen -> 3 optionen
            1. ++ strahl trifft kugel
            2. -- strahl hinter kugel
            3. -+ strahl in Kugel

        
            ----

        schnittpunkt ist der ort wo der strahl die kugel trifft
        Normale dort ist Schnittpunkt - Kugelzentrum -> N = xyz1 - xyzC
        -> normalisiert durch geteilt durch radius
    */
    
}
