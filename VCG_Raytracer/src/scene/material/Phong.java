package scene.material;

import java.util.ArrayList;

import scene.light.Light;
import scene.light.PointLight;
import scene.shape.Shape;
import utils.RgbColor;
import utils.algebra.Vec3;

// Calculates the Diffuse Aspect of Lighting
public class Phong extends Material {

    private RgbColor diffuseK;
    private RgbColor specularK;
    private int specularExponent;

    // diffuse factor is set on instantiation
    public Phong (RgbColor _diffuseK, RgbColor _specularK, int _specularExponent) {
        this.diffuseK = _diffuseK;
        this.specularK = _specularK;
        this.specularExponent = _specularExponent;
    }

    //honestly think we may need another input var for the cam position
    //man könnte light als param raus nehmen und hier drinnen ne for each schleife der lichter haben
    //but we'd need access to the lightlist for that
    public RgbColor getColor(Vec3 _camPos, Vec3 _position, Vec3 _normal, ArrayList<PointLight> _list_lights, ArrayList<Shape> _list_shapes){

        Float r,g,b;

        // AMBIENT

        r = getAmbientK().red();
        g = getAmbientK().green();
        b = getAmbientK().blue();

        for (Light light : _list_lights) {

            Vec3 lightPosition = light.getPosition();
            RgbColor lightColor = light.getColor();
            

            Vec3 normal = _normal.normalize();
            Vec3 vec_light = lightPosition.sub(_position).normalize();

            Float scalar_normal_light = normal.scalar(vec_light);
            if(scalar_normal_light <= 0){
                // Face is looking away from Light; doesn't get any values added
                return new RgbColor(r, g, b);
            }else{
                r += lightColor.red() * diffuseK.red() * scalar_normal_light;
                g += lightColor.green() * diffuseK.green() * scalar_normal_light;
                b += lightColor.blue() * diffuseK.blue() * scalar_normal_light;

                //betrachtungsvektor
                Vec3 vec_cam = _position.sub(_camPos);
                vec_cam = vec_cam.normalize();

                //reflektionsvektor
                Vec3 vec_reflection = new Vec3();
                //possible that we'd need to split this over 2 lines
                vec_reflection = vec_light.sub(normal.multScalar(2 * scalar_normal_light));
                vec_reflection = vec_reflection.normalize();

                Float scalar_view_reflection = vec_reflection.scalar(vec_cam);

                System.out.println(scalar_view_reflection);

                Float shadeResult = this.shade(_position, light, _list_shapes);
                if(shadeResult != 0){
                  r += (float) (lightColor.red() * (specularK.red() * Math.pow(Math.max(0, scalar_view_reflection), specularExponent)));// * shadeResult;
                  g += (float) (lightColor.green() * (specularK.green() * Math.pow(Math.max(0, scalar_view_reflection), specularExponent)));// * shadeResult;
                  b += (float) (lightColor.blue() * (specularK.blue() * Math.pow(Math.max(0, scalar_view_reflection), specularExponent)));// * shadeResult;
                }


            }
        }

        return new RgbColor(r, g, b);

    }

}

/*

// das ambiente Licht
let ambientLight = {intensity: {r: 0.125, g: 0.125, b: 0.125}};

// alle übrigen (Punkt)-Lichter der Szene
let lights = [
  {position: new THREE.Vector3(-100, 100, 80),
   intensity: {r: 1, g: 0.2, b: 0.5}},
  {position: new THREE.Vector3(60, 70, 90),
   intensity: {r: 0.5, g: 0.0, b: 1}},
  {position: new THREE.Vector3(-10, -40, 70),
   intensity: {r: 0.8, g: 0.8, b: 0.0}},
];

// Reflektionskoeffizienten
let ambientK = {r: 0.5, g: 0.5, b: 0.5};
let diffuseK = {r: 0.8, g: 0.6, b: 0.9};
let specularK = {r: 0.7, g: 0.7, b: 0.7};

// Weitere Angaben
let specularExponent = 50;



////////////////////////////////////////////////////////////////////////////////
// initScene()
// Initialisierung.
// Wird automatisch beim Start aufgerufen. 
////////////////////////////////////////////////////////////////////////////////
function initScene()
{
  registerLights(lights);
}



////////////////////////////////////////////////////////////////////////////////
// phong(position, normal, camPosition)
// Wird während des Renderings für jeden Vertex einmal aufgerufen.
// Parameter: position     Vertexposition (Vector3)
//            normal       Vertexnormale (Vector3)
//            camPosition  Kameraposition (Vector3)
// Rueckgabe: Eine Farbe. D.h. ein Objekt mit den Attributen .r, .g und .b
//
// Hinweis: Alle Parameter befinden sich im selben Koordinatensystem.
////////////////////////////////////////////////////////////////////////////////
function phong(position, normal, camPosition)
{
  // Initialisiere den Rueckgabewert
  // Die einzelnen Anteile werden folglich schrittweise addiert
  let outColor = {r: 0.0, g: 0.0, b: 0.0};


  // AMBIENTE = ambientes Licht (rgb) * ambienter Reflektionskoeffizient (rgb)

  outColor.r += ambientLight.intensity.r * ambientK.r; // zugriff auf rgb-Wert des ambienten Lichts durch .intensity.r|g|b
  outColor.g += ambientLight.intensity.g * ambientK.g;
  outColor.b += ambientLight.intensity.b * ambientK.b;


  // FOLGENDES MUSS FÜR JEDES LICHT BERECHNET WERDENn 

  // Für jede Lichtquelle in unserem Array
  for (let lightCount = 0; lightCount < lights.length; lightCount++) {


    // DIFFUS = Lichtquelle (rgb) * (diffuser Koeff. (rgb) * (Beziehung zwischen Einfallwinkel und Normale))

    // Initialisiere Hilfsvariablen innerhalb der Schleife, für jedes Licht neu
    let normal_0 = normal.normalize();
    let vecToLight = new THREE.Vector3();
    // Vektor vom Punkt zur Lichtquelle, Lichtquelle - Vertexposition
    vecToLight.subVectors(lights[lightCount].position, position); // Abruf des Lichtes über das Lights-Array
    vecToLight.normalize();
    let scalar_nl = normal_0.dot(vecToLight);  // Skalarprodukt zwischen Normalen- und Lichtvektor
    // Weitere Berechnungen bei Skalar = 0 nicht nötig, Licht trifft nicht auf die Fläche
    if(scalar_nl <= 0){
      continue;
    }

    // Formel für alle 3 rgb's berechnen
    outColor.r += lights[lightCount].intensity.r * (diffuseK.r * scalar_nl);  // Intensity des Lichtes muss auch wieder aus dem Array geholt werden
    outColor.g += lights[lightCount].intensity.g * (diffuseK.g * scalar_nl);
    outColor.b += lights[lightCount].intensity.b * (diffuseK.b * scalar_nl);


    // SPEKULAR = Lichtquelle (rgb) * (spekularer Koeff. (rgb) * (Beziehung zwischen Betrachtungsvektor und Reflektionsvektor)^n)

    // Betrachtungsvektor ist der Vektor zwischen dem Vertex und der Kamera
    let vecToCam = new THREE.Vector3(); 
    vecToCam.subVectors(camPosition, position); // Vektor zur Kamera
    vecToCam.normalize();

    // Reflektionsvektor wird berechnet: R = 2 * (N * L) * N - L
    // Normalenvektor ist schon normalisiert, Lichtvektor und Skalar_NL sind in Diffus berechnet worden
    // Hilfsvariablen -> Normalenvektor klonen, da wir diesen verändern müssen
    let vecReflection = new THREE.Vector3();
    let clone_normal_0 = normal_0.clone();
    // Wir ziehen den Lichtvektor vom Normalenanteil der Gleichung ab für den Reflektionsvektor
    vecReflection = vecReflection.subVectors(clone_normal_0.multiplyScalar(2 * scalar_nl), vecToLight); 
    vecReflection.normalize();
    // Der Skalar zwischen dem Betrachtungs- und Reflektionsvektor
    scalar_vr = vecReflection.dot(vecToCam);


    // Formel für alle 3 rgb's berechnen
    outColor.r += lights[lightCount].intensity.r * (specularK.r * Math.pow(Math.max(0, scalar_vr), specularExponent));
    outColor.g += lights[lightCount].intensity.g * (specularK.g * Math.pow(Math.max(0, scalar_vr), specularExponent));
    outColor.b += lights[lightCount].intensity.b * (specularK.b * Math.pow(Math.max(0, scalar_vr), specularExponent));
  }

  // Rueckgabe des berechneten Farbwerts
  return outColor;
}

 */
