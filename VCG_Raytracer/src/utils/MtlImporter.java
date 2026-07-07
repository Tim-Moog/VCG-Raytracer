package  utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import scene.material.Lambert;
import scene.material.Material;
import scene.material.Phong;
import utils.algebra.Vec3;

//basically we need some kinda way to save the materials from the .mtl file with their name
public class MtlImporter {


    public static Map<String, Material> loadMats(InputStream input, String modelName) throws IOException{

        Map<String, Material> allMats = new HashMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        //material values
        //base set to null and maxvalue for easy checks if the field has been used for our current mat
        Material currentMat = null;
        String currentName = null;
        Vec3 ambientK = null; //Ka
        Vec3 diffuseK = null; //Kd
        Vec3 specularK = null;//Ks
        float specularExponent = Float.MAX_VALUE;//Ns --> yes i know we do this as int in our classes but ill save as float and cast to int since idk if else i can save it as easily in a var
        float transmission= Float.MAX_VALUE;//d or Tr
        float transmission_index= Float.MAX_VALUE;//Ni
        //no reflection since from what im reading that doesnt exist in .mtl?
        //so im just gonna set it to 0 everywhere

        String line;


        BufferedImage textureMap = null;

        //go through line by line
        while ((line = reader.readLine()) != null) {

            line = line.trim();

            //ignore empty lines and comments
            if (line.isEmpty() || line.startsWith("#"))
                continue;
        
            String[] s = line.split("\\s+");

            //changed this from normal switch to a "rule switch" since vs code recommended and good to try out to be aware of
            switch(s[0]){

                case "newmtl" -> {
                    if(currentName != null){
                        //hier drinnen brauchen wir dann checks für welche values benutzt wurden und machen basierend dadrauf ein passendes material
                        //was müssen wir machen wenn wir bei nem neuen material ankommen?
                        //prüfen welche werte wir haben und auf basis dessen dann ein passendes material erstellen

                        //testwise adding this
                        //currentMat = new Lambert(new RgbColor(diffuseK));

                        //if(ambientK != null)
                            //Material.setAmbientK(new RgbColor(ambientK)); //not setting ambient for now for testing
                        if(transmission == Float.MAX_VALUE){
                            transmission = 0f;
                        }else{
                            transmission = 1f - transmission;
                        }
                            

                        
                        if(specularK == null){//in this case we prob got a lambert mat
                            if(transmission_index != Float.MAX_VALUE){
                                currentMat = new Lambert(new RgbColor(diffuseK), 0f, transmission, transmission_index);
                            }else{
                                currentMat = new Lambert(new RgbColor(diffuseK), 0f, transmission);
                            }
                        }else{
                            //otherwise we make a phong due to having a specular
                            if(specularExponent ==Float.MAX_VALUE)
                                specularExponent = 1f;

                            if(transmission_index != Float.MAX_VALUE){
                                currentMat = new Phong(new RgbColor(diffuseK), new RgbColor(specularK), (int)specularExponent, 0f, transmission, transmission_index);
                            }else{
                                currentMat = new Phong(new RgbColor(diffuseK), new RgbColor(specularK), (int)specularExponent, 0f, transmission);
                            }
                        }

                        if(textureMap != null)
                            currentMat.setDiffuseTextureMap(textureMap);
                        //save the just created mat under its name
                        allMats.put(currentName, currentMat);

                        //after saving the created mat we gotta reset all saveslots
                        ambientK = null; //Ka
                        diffuseK = null; //Kd
                        specularK = null;//Ks
                        specularExponent = Float.MAX_VALUE;//Ns
                        transmission= Float.MAX_VALUE;//d or Tr
                        transmission_index= Float.MAX_VALUE;//Ni
                        textureMap = null;
                    }

                    //we gotta save the new name
                    currentName = s[1];
                }
                
                case "Ka" -> ambientK = new Vec3(Float.parseFloat(s[1]),
                                        Float.parseFloat(s[2]),
                                        Float.parseFloat(s[3]));

                case "Kd" -> diffuseK = new Vec3(Float.parseFloat(s[1]),
                                        Float.parseFloat(s[2]),
                                        Float.parseFloat(s[3]));

                case "Ks" -> specularK = new Vec3(Float.parseFloat(s[1]),
                                        Float.parseFloat(s[2]),
                                        Float.parseFloat(s[3]));

                case "Ns" -> specularExponent = Float.parseFloat(s[1]);

                case "d","Tr" -> transmission = Float.parseFloat(s[1]);

                case "Ni" -> transmission_index = Float.parseFloat(s[1]);

                case "map_Kd" -> textureMap = ImageIO.read(new FileInputStream("VCG_Raytracer/models/mats/" + modelName + "/" + s[1]));
            }
        }

        //after going through all lines we still gotta save the current mat
        if(currentName != null){
            //hier drinnen brauchen wir dann checks für welche values benutzt wurden und machen basierend dadrauf ein passendes material
            //was müssen wir machen wenn wir bei nem neuen material ankommen?
            //prüfen welche werte wir haben und auf basis dessen dann ein passendes material erstellen
            //if(ambientK != null)
                //Material.setAmbientK(new RgbColor(ambientK));
            if(transmission == Float.MAX_VALUE)
                transmission = 0f;
            
            if(specularK == null){//in this case we prob got a lambert mat
                if(transmission_index != Float.MAX_VALUE){
                    currentMat = new Lambert(new RgbColor(diffuseK), 0f, transmission, transmission_index);
                }else{
                    currentMat = new Lambert(new RgbColor(diffuseK), 0f, transmission);
                }
            }else{
                //otherwise we make a phong due to having a specular
                if(specularExponent ==Float.MAX_VALUE)
                    specularExponent = 1f;

                if(transmission_index != Float.MAX_VALUE){
                    currentMat = new Phong(new RgbColor(diffuseK), new RgbColor(specularK), (int)specularExponent, 0f, transmission, transmission_index);
                }else{
                    currentMat = new Phong(new RgbColor(diffuseK), new RgbColor(specularK), (int)specularExponent, 0f, transmission);
                }
            }

            if(textureMap != null)
                currentMat.setDiffuseTextureMap(textureMap);
            //save the just created mat under its name
            allMats.put(currentName, currentMat);
        }



        return allMats;
    }

}
