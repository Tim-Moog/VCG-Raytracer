package scene.material;

import raytracer.Ray;
import scene.Scene;
import scene.shape.Intersection;
import utils.RgbColor;
import utils.algebra.Vec3;

// Abstract class for all Materials, only knows the light Factors (k's)
public abstract class Material {

    //Only has to be set once, or is using it's standard Rgb
    protected static RgbColor ambientK = new RgbColor(0.1f, 0.1f, 0.1f);

    //Traits potentially shared across different Materials, reduces having to write them for every class...
    protected RgbColor diffuseK;
    protected RgbColor specularK;
    protected int specularExponent;
    protected float reflection = 0; //between 0 and 1
    protected float transmission = 0; //between 0 and 1
    protected float transmission_index = 1; // final variables set in Main, provided at instantiation

    /** 
     * Standard Material class without any added effects
    */
    protected Material () {
        // UwU empty
    }

    /**
     * Material with specific effects like Reflection and Refraction
     * @param _reflection between 0-1, how much this object reflects the environment
     * @param _transmission between 0-1, how much this object lets through
     */
    protected Material (float _reflection, float _transmission) {
        this.setReflection(_reflection);
        this.setTransmission(_transmission);
    }

    /**
     * Material with specific effects like Reflection and Refraction
     * @param _reflection between 0-1, how much this object reflects the environment
     * @param _transmission between 0-1, how much this object lets through
     * @param _refrective_index > 0, Index of what kind of Material is inside (Fixed values provided in main.java)
     */
    protected Material (float _reflection, float _transmission, float _transmission_index) {
        this.setReflection(_reflection);
        this.setTransmission(_transmission);
        this.setRefrectiveIndex(_transmission_index);
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
    public abstract RgbColor getColor(Intersection _intersection, Scene _scene, int _depth); //why dont we set scene and ambient as statics and have a setter to set it? that way less param clutter

    public static void setAmbientK(RgbColor ambientK) {
        Material.ambientK = ambientK;
    }

    public float getReflection () {
        return this.reflection;
    }

    public float getTransmission () {
        return this.transmission;
    }

    public float getTransmissionIndex () {
        return this.transmission_index;
    }

    /**
     * Checks if Float is actually between 0 and 1 for accurate procentual usecase for shinyness
     * If not, sets out of bound values to the direct next bound
     * @param _value Float we check the validity of
     */
    private void setReflection (float _value) {
        // Our procentual values are all between 0 and 1, so set cases to these extremes in case they happen
        // F.e. Shinyness = 5 -> 1
        if(_value > 1) {
            System.err.println("Provided value of Reflection: " + _value 
                                + "\nCaution! Value has to be between 0 and 1"
                                + "Value has been corrected to 1");
            this.reflection = 1;
        }else if(_value < 0) {
            System.err.println("Provided value of Reflection: " + _value 
                                + "\nCaution! Value has to be between 0 and 1"
                                + "Value has been corrected to 0");
            this.reflection = 0;
        }else{
            this.reflection = _value;
        }
    }

    /**
     * Checks if Float is actually between 0 and 1 for accurate procentual usecase for transparency
     * If not, sets out of bound values to the direct next bound 
     * @param _value Float we check the validity of
     */
    private void setTransmission (float _value) {
        // Our procentual values are all between 0 and 1, so set cases to these extremes in case they happen
        // F.e. Shinyness = 5 -> 1
        if(_value > 1) {
            System.err.println("Provided value of Transmission: " + _value 
                                + "\nCaution! Value has to be between 0 and 1"
                                + "Value has been corrected to 1");
            this.transmission = 1;
        }else if(_value < 0) {
            System.err.println("Provided value of Transmission: " + _value 
                                + "\nCaution! Value has to be between 0 and 1"
                                + "Value has been corrected to 0");
            this.transmission = 0;
        }else{
            this.transmission = _value;
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
                                + "\nCaution! Value has to be greater than or equal 0"
                                + "Value has been corrected to 1 (AIR)");
            this.transmission_index = 1; //AIR
        } else {
            this.transmission_index = _value;
        }
    }

    /**
     * Sets the correct Ray deflecting off the surface of the IntersectionPoint as the specific outRay for Intersection
     * @param _incoming Ray pointing towards the IntersectionPoint
     * @param _normal Normal of our Surface
     * @param _intersection The Intersection on the Shape we are looking at, for which we set the Ray
     */
    protected void calculateReflectionRay (Ray _incoming, Vec3 _normal, Intersection _intersection) {
        Vec3 vInRay = _incoming.getDirection().multScalar(-1).normalize();
        Vec3 vNormal = _normal.normalize();
        Vec3 vOutReflection = vNormal.multScalar(2 * (vInRay.scalar(vNormal))).sub(vInRay).normalize();
        Vec3 origin = _intersection.getIntersectionPoint().add(_normal.multScalar(0.0001f));

        Ray vOutRay = new Ray(origin, vOutReflection);
        _intersection.setReflectionRay(vOutRay);
    }

    /**
     * Sets the correct Ray splitting into (or out of) the shape of the IntersectionPoint as the specific outRay for Intersection
     * Sources used outside of our learning Material: "Reflections and Refractions in Ray Tracing" from Standford (for pebs in my group)
     * @param _incoming Ray pointing towards the IntersectionPoint
     * @param _normal Normal of our Surface
     * @param _intersection The Intersection on the Shape we are looking at, for which we set the Ray
     */
    protected void calculateTransmissionRay (Ray _incoming, Vec3 _normal, Intersection _intersection) {
        float n1 = 1; //Material outside shape (AIR)
        float n2 = this.transmission_index; //Material of shape

        //Prepping the Incoming Ray (sent from cam) and Normal
        Vec3 vInRay = _incoming.getDirection().normalize(); // I
        Vec3 vNormal = _normal.normalize(); // N
        //Turning incoming Ray direction around so we get the angle between 
        //the Normal and the Ray BOTH pointing away from our surface!
        float cos_I = -vNormal.scalar(vInRay);

        //Checking if Ray is originating from inside the Shape or Outside;
        //If inside -> flip Normal around
        if(cos_I < 0) {
            vNormal = vNormal.negate();
            cos_I = -cos_I;
            //Materials flip aswell
            float temp = n1;
            n1 = n2;
            n2 = temp;
        }

        float snellius = n1/n2;

        //Calculating Snellius Term sin_T = n1/n2 * sin_I
        //    and sin_I^2 = (1-cos_I^2)
        //As we need sin_T^2 we also need to apply that to the other side of the equation
        float sin_T2 = (float) Math.pow(snellius, 2) * (1 - (float) Math.pow(cos_I, 2));

        //Total internal reflection -> sin_T2 has to be smaller or equal to 1, else Snellius doesn't apply
        //This means that no Reflection is possible! (nothing happens, or reflection)
        if(sin_T2 > 1) {
            _intersection.setFresnel(1);
            _intersection.setTransmissionRay(null);
            return;
        }

        //final angle (between normal and transmission Ray pointing in the same direction)
        //cos_T = sqrt(1 - sin_T^2) from Vorlesung Raytracer
        float cos_T = (float) Math.sqrt(1 - sin_T2);

        //Building up the Transmission Ray by Snell's Law (STANFORD)
        // T = n1/n2 * I + (n1/n2 * cos_I - sqrt(1 - sin_T2)) * N
        // T = n1/n2 * I + (n1/n2 * cos_I - cos_T) * N
        Vec3 vOutTransmission = vInRay.multScalar(n1/n2)
                                .add(vNormal.multScalar(n1/n2 * cos_I - cos_T))
                                .normalize();
        Vec3 origin = _intersection.getIntersectionPoint().sub(vNormal.multScalar(0.0001f));
        Ray vOutRay = new Ray(origin, vOutTransmission);
        _intersection.setTransmissionRay(vOutRay);

        // FRESNEL
        // Calculation following Schlick's approximation
        float r0 = (float) Math.pow((n1 - n2) / (n1 + n2), 2);
        // R(schlick) = R0 + (1 - R0) * (1 - cos_I) ^5
        _intersection.setFresnel(r0 + (1 - r0) * (float) Math.pow(1 - cos_I, 5));
    }

}
