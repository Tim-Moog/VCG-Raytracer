package scene.material;

import raytracer.Ray;
import scene.Scene;
import scene.shape.Intersection;
import utils.RgbColor;

// Abstract class for all Materials, only knows the light Factors (k's)
public abstract class Material {

    //Not pretty, these could be in Main instead, but making them known to our Materials would be a pain in the ass
    //So our Material class is just that smart instead :)
    //I think thats fair, as ONLY Material has to know these...
    protected static final float DENS_AIR = 1.2f;
    protected static final float DENS_WATER = 1.0f;

    //Traits potentially shared across different Materials, reduces having to write them for every class...
    protected RgbColor diffuseK;
    protected RgbColor specularK;
    protected int specularExponent;
    protected float shinyness = 0; //between 0 and 1
    protected float transparency = 0; //between 0 and 1

    /** 
     * Standard Material class without any added effects
    */
    protected Material () {
        // UwU empty
    }

    /**
     * Material with specific effects like Reflaction and Refraction
     * @param _shinyness between 0-1, how much this object reflects the environment
     * @param _transparency between 0-1, how much this object lets through
     */
    protected Material (float _shinyness, float _transparency) {
        this.shinyness = this.correctProcentualValue(_shinyness);
        this.transparency = this.correctProcentualValue(_transparency);
    }
    
    /**
     * Method called by Raytracer for shading, shared across all Materials. Calculates the color of a Point. 
     * Is called seperately for every Ray (f.e. secondary)!
     * @param _intersection Intersection Object containing all Information regarding the Point we are working with
     * @param _ray Ray pointing towards the Point, includes length etc.
     * @param _scene Scene we are currently in and all other Shapes
     * @param _ambient Ambient Coloring, set in main and given with through Raytracer
     * @param _depth Current Recursion this Method has been called on 
     * @return Color of the Point in question
     */
    public abstract RgbColor getColor(Intersection _intersection, Ray _ray, Scene _scene, RgbColor _ambient, int _depth);
    //public abstract RgbColor getColorPhong(Vec3 _camPos, Vec3 _position, Vec3 _normal, Light _light);

    public float getShinyness () {
        return this.shinyness;
    }

    public float getTransperency () {
        return this.transparency;
    }

    /**
     * Checks if Float is actually between 0 and 1 for accurate procentual usecase
     * If not, sets out of bound values to the direct next bound
     * @param _value Float we check the validity of
     * @return Value between 0 and 1
     */
    private float correctProcentualValue (float _value) {
        // Our procentual values are all between 0 and 1, so set cases to these extremes in case they happen
        // F.e. Shinyness = 5 -> 1
        if(_value > 1) {
            return 1;
        }else if(_value < 0) {
            return 0;
        }
        return _value;
    }

}
