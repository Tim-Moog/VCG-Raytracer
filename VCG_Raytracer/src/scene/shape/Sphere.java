package scene.shape;

import raytracer.Ray;
import utils.algebra.Matrix4x4;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.algebra.Vec4;

public class Sphere extends Shape{

    private Vec3 centerPosition;
    private float radius;

    private float A;
    private float B;
    private float C;

    private Matrix4x4 myTransformationMatrix;

    
    public Sphere(Vec3 pos, float radius) {
        this.centerPosition = new Vec3(0,0,0);
        this.radius = 1;
        this.intersectionData = new Intersection();
        this.myTransformationMatrix = new Matrix4x4();
        this.setupMatrix(pos, radius);
    }

    public void setupMatrix(Vec3 _pos, float _rad){
        addInvTranslation(_pos);
        addScaleUni(_rad);
    }

    public void addInvTranslation (Vec3 _pos) {
        Vec4 homoTrans = new Vec4(_pos.x, _pos.y, _pos.z, 1);
        myTransformationMatrix = myTransformationMatrix.translateXYZW(homoTrans).invert();
    }

    public void addScaleUni (double _scale) {
        myTransformationMatrix = myTransformationMatrix.scale(_scale);
    }

    public void applyTransformation(Ray _ray){
        Vec3 newDirection = _ray.getDirection();
        Vec3 newOrigin = _ray.getOrigin();
        _ray.setDirection_D(myTransformationMatrix.multVec3(newDirection, false));
        _ray.setStartpoint_p0(myTransformationMatrix.multVec3(newOrigin, true));
    }
    //erst inverse dann schnittpunkt dann schnittpunkt berechnen
    //darauf dann die normale matrix drauf anwenden 

    
    public void intersect(Ray ray){
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
        if(diskriminante < 0)
            return;


        //calculating t's
        float t0 = (float) (- B - Math.sqrt(diskriminante))/2;
        float t1 = (float) (- B + Math.sqrt(diskriminante))/2;

        //decide which one is first point of contact in front of us 
        float t;
        if (diskriminante > 0){
            if(t1 > 0 && t0 > 0){// case + +
                //strahl trifft die kugel vor uns
                if(t1 < t0){
                    //case t1 is closer to cam
                    intersectionData.setInRay(ray.calculateRayAt(t1));
                    intersectionData.setOutRay(ray.calculateRayAt(t0));
                }else{
                    //case t0 is closer to cam
                    intersectionData.setInRay(ray.calculateRayAt(t0));
                    intersectionData.setOutRay(ray.calculateRayAt(t1));
                }

                intersectionData.setIntersectionPoint(intersectionData.getInRay());
                Vec3 normal = (intersectionData.getInRay().sub(centerPosition)).multScalar(1/radius);
                intersectionData.setNormal(normal);
                
            } else{
                //right now this else block never gets entered?
                //if(t1 < 0 && t0 < 0) // case - -
                    //strahl hinter der kugel
                    //System.err.println("- -");
                //if(t1 > 0 || t1 > 0)// case + - or - +
                    //strahl in der kugel
                    //System.err.println("+ -");
            }
        }
        if(diskriminante == 0){
            //strahl tangiert die kugel demnach sollte es ja nur einen punkt geben
            //System.err.println("tangiert");
            t = t0;
        }


        //calculate distance
        //honestly dont think this is needed rn 
        //intersectionData.setDistance(t); //prob not t but length of a the inRay vector

        /* moved this up to into the if 
        intersectionData.setIntersectionPoint(intersectionData.getInRay());
        Vec3 normal = (intersectionData.getInRay().sub(centerPosition)).multScalar(1/radius);
        intersectionData.setNormal(normal);
            */
        


    }
    
    public Intersection getIntersectionData(){
        return this.intersectionData;
    }
}
