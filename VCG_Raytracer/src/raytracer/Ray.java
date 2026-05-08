package raytracer;

import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class Ray {

    private Vec3 startpoint;
    private Vec3 direction;

    public Ray (Vec3 _startpoint, Vec3 _direction) {
        this.startpoint = _startpoint;
        this.direction = _direction; //maybe das nicht hier definieren sondern
    }

    public Vec3 calculateRayAt (Vec3 _direction, Vec3 viewVector, Vec3 targetPixel, Vec2 widthAndHeight) { 
        //maybe iwas cam mäßiges als param weiterleiten
        Vec3 sideVector = new Vec3(1,0,0);
        Vec3 camUp = new Vec3(0,1,0);
        float deltaX = targetPixel.x - widthAndHeight.x/2;
        float deltaY = targetPixel.y - widthAndHeight.y/2;
        Vec3 ourRay = viewVector.add(sideVector.multScalar(deltaX)).add(camUp.multScalar(deltaY));
        return ourRay;
    }

    public void setStart(Vec3 newStart){
        startpoint = newStart;
    }

    public void setDir(Vec3 newDir){
        direction = newDir;
    }

    //#i think sowas wie ablenkungsvektor und so muss hier maybe rein
    //brauchen viewvektor der auf 0,0,0 gerichtet ist
    /*
    die Ray formel ist R = Anfangspunkt_P0 + param_t * Richtung_D
    D = Viewvektor(auf 0,0,0 gerichtet) + (deltaX * sideVektor) + (deltaY * camera_up_vektor_u)
    ALSO 
    
    */
}
