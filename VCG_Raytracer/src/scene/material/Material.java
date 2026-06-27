package scene.material;

import raytracer.Ray;
import scene.Scene;
import scene.shape.Intersection;
import utils.RgbColor;

// Abstract class for all Materials, only knows the light Factors (k's)
public abstract class Material {

    //Traits potentially shared across different Materials, reduces having to write them for every class...
    protected RgbColor diffuseK;
    protected RgbColor specularK;
    protected int specularExponent;
    protected float shinyness = 0; //between 0 and 1
    protected float transparency = 0; //between 0 and 1
    protected float refrective_index = 1; // final variables set in Main, provided at instantiation

    /** 
     * Standard Material class without any added effects
    */
    protected Material () {
        // UwU empty
    }

    /**
     * Material with specific effects like Reflection and Refraction
     * @param _shinyness between 0-1, how much this object reflects the environment
     * @param _transparency between 0-1, how much this object lets through
     */
    protected Material (float _shinyness, float _transparency, float _refrective_index) {
        this.setShinyness(_shinyness);
        this.setTransparency(_transparency);
        this.setRefrectiveIndex(_refrective_index);
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
    public abstract RgbColor getColor(Intersection _intersection, Ray _ray, Scene _scene, RgbColor _ambient, int _depth); //why dont we set scene and ambient as statics and have a setter to set it? that way less param clutter

    public float getShinyness () {
        return this.shinyness;
    }

    public float getTransperency () {
        return this.transparency;
    }

    public float getRefrectiveIndex () {
        return this.refrective_index;
    }

    /**
     * Checks if Float is actually between 0 and 1 for accurate procentual usecase for shinyness
     * If not, sets out of bound values to the direct next bound
     * @param _value Float we check the validity of
     */
    private void setShinyness (float _value) {
        // Our procentual values are all between 0 and 1, so set cases to these extremes in case they happen
        // F.e. Shinyness = 5 -> 1
        if(_value > 1) {
            System.err.println("Provided value of Shinyness: " + _value 
                                + "\nCaution! Value has to be between 0 and 1"
                                + "Value has been corrected to 1");
            this.shinyness = 1;
        }else if(_value < 0) {
            System.err.println("Provided value of Shinyness: " + _value 
                                + "\nCaution! Value has to be between 0 and 1"
                                + "Value has been corrected to 0");
            this.shinyness = 0;
        }else{
            this.shinyness = _value;
        }
    }

    /**
     * Checks if Float is actually between 0 and 1 for accurate procentual usecase for transparency
     * If not, sets out of bound values to the direct next bound 
     * @param _value Float we check the validity of
     */
    private void setTransparency (float _value) {
        // Our procentual values are all between 0 and 1, so set cases to these extremes in case they happen
        // F.e. Shinyness = 5 -> 1
        if(_value > 1) {
            System.err.println("Provided value of Transparency: " + _value 
                                + "\nCaution! Value has to be between 0 and 1"
                                + "Value has been corrected to 1");
            this.transparency = 1;
        }else if(_value < 0) {
            System.err.println("Provided value of Transparency or Shinyness: " + _value 
                                + "\nCaution! Value has to be between 0 and 1"
                                + "Value has been corrected to 0");
            this.transparency = 0;
        }else{
            this.transparency = _value;
        }
    }

    /**
     * Checks if Float is above 0, Index cannot be below
     * If not, sets out of bound values to the assumed standard 1 (AIR)
     * @param _value Float we check the validity of
     */
    private void setRefrectiveIndex (float _value) {
        if(_value < 0) {
            System.err.println("Provided value of Refrective Index: " + _value 
                                + "\nCaution! Value has to be above 0"
                                + "Value has been corrected to 1 (AIR)");
            this.refrective_index = 1; //AIR
        } else {
            this.refrective_index = _value;
        }
    }

}
