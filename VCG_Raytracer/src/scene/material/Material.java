package scene.material;

import java.util.ArrayList;

import raytracer.Ray;
import scene.light.Light;
import scene.light.PointLight;
import scene.shape.Intersection;
import scene.shape.Shape;
import utils.RgbColor;
import utils.algebra.Vec3;

// Abstract class for all Materials, only knows the light Factors (k's)
public abstract class Material {

    private static RgbColor ambientK = new RgbColor(0.1f,0.1f,0.1f);
    protected Shape myShape;

    public Shape getMyShape() {
        return myShape;
    }

    public void setMyShape(Shape myShape) {
        this.myShape = myShape;
    }

    protected Material (RgbColor _ambientK) {
        Material.ambientK = _ambientK;
    }

    protected Material () {
        //uwu 
    }

    public RgbColor getAmbientK() {
        return ambientK;
    }

    public void setAmbientK(RgbColor ambientK) {
        Material.ambientK = ambientK;
    }

    public abstract RgbColor getColor(Vec3 _camPos, Vec3 _position, Vec3 _normal, ArrayList<PointLight> _list_lights, ArrayList<Shape> _list_shapes);

    //farbe trennen von dem einen licht
    public Float shade (Vec3 _intersection_pos, Light _current_light, ArrayList<Shape> _list_shapes) {

        //we prob need to grab the light position and compare to our intersection point
        float distance = _current_light.getPosition()
                        .sub(_intersection_pos)
                        .length();
        //current = current.multScalar(distance * distance); // dist * dist recommended by marcel
        // *0.25 due to else impact either too big or too smol 
        //value just depends on light distance which makes sense as you want further lights to have less impact....
        //but for this surely there has to be a better way to make it seem balanced no?
        //honestly no clue if theres a better way atleast couldnt find any specific way for this in folien but not having it in feels troll cuz it just makes sense for distance to play a role logically
        //weirdest thing is wenn wir das auskommentieren sieht es genauso dunkel aus bei *0.25 ?? like wha but bei nur *distance it bright af


        //----------------------------- secondary rays -----------------------
        //sekundärstrahlen idee
        //logic written down 
        //send ray from intersectionpoint to the light itself <- due to this, this prob needs to be inside of the prev for each loop so will add there
        //schnitttest mit allen objekten
            //falls treffer
            //objektpunkt im schatten = pixelfarbe ist ambientwert
            //falls kein treffer
            //berechne lokales beleuchtungsmodell 

        //sekundärstrahlen attempt- nvm not attempt.... SUCCESS
        Ray secondaryRay = new Ray(_intersection_pos, _current_light.getPosition().sub(_intersection_pos));
        Shape hitObjectShape = this.getMyShape();
        boolean objectInTheWay = false; //boolean to keep track of if any object is blocking direct LOS from impact to light

        for (Shape currentShape : _list_shapes) { //go through all shapes
            if(currentShape != hitObjectShape){//except the one that our ray is starting from -> da kann man es abhängig von normale machen
                Intersection secondaryRayData = currentShape.intersect(secondaryRay);
                if(secondaryRayData.isHit())
                    objectInTheWay = true;
            }
        }



        if(objectInTheWay)
            return 0f;
    

        //pretty sure this aint wanted since it just makes it a lot darker -> yea this just makes it very dark so dont do this
        //pointColor = new RgbColor(pointColor.red()/getLights().size(), pointColor.green()/getLights().size(), pointColor.blue()/getLights().size());

        return distance * distance;
    }

}
