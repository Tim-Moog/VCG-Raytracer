package utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import scene.material.Material;
import scene.shape.BoundingBox;
import scene.shape.Polygon;
import scene.shape.PolygonMesh;
import utils.algebra.Matrix4x4;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.algebra.Vec4;

public class ObjImporter {

    public static BoundingBox load(InputStream input, Material material, Vec3 position, Vec3 rotation, float scale) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        //need one array for the vertices/corners and one for the faces
        List<Vec3> vertices = new ArrayList<>();
        List<int[]> faces = new ArrayList<>();
        //type int[] to pair index slots so that 3 vertices can belong to each face

        String line;

        //go through line by line
        while ((line = reader.readLine()) != null) {

            line = line.trim();

            //ignore empty lines and comments
            if (line.isEmpty() || line.startsWith("#"))
                continue;

            //------------ vertices(corners) ---------------------------
            //look for vertices aka lines starting with v
            if (line.startsWith("v ")) {

                String[] s = line.split("\\s+");

                float x = (float) Double.parseDouble(s[1]);
                float y = (float) Double.parseDouble(s[2]);
                float z = (float) Double.parseDouble(s[3]);

                vertices.add(new Vec3(x, y, z));
            }

            //------- faces bzw Indexes that tell us which vertices make a face together-------------
            //look for faces aka lines starting with an f
            else if (line.startsWith("f ")) {

                String[] s = line.split("\\s+");

                //safety check since we only want polygons
                if (s.length != 4)
                    continue;

                //grab the index info
                int i1 = parseIndex(s[1]);
                int i2 = parseIndex(s[2]);
                int i3 = parseIndex(s[3]);

                //based on index info we find out which vertices together build a face
                faces.add(new int[]{i1, i2, i3});
            }
        }
        //close stream to avoid issues like in oop
        reader.close();

        //create a transformation matrix to apply on all polygons before saving em
        Vec4 homoPosition = new Vec4(position.x, position.y, position.z, 1);
        Matrix4x4 transform = new Matrix4x4().multScalar(scale);
        transform = transform.mult(Matrix4x4.rotation(rotation));
        transform = transform.translateXYZW(homoPosition);

        //create a mesh to save all polygons inside of, imo cleaner than saving all of em in shapelist
        PolygonMesh polymesh = new PolygonMesh();

        //additionally to the Mesh we'll need a bounding box around it to increase performance
        //the box will be based on the min and max values of all coordinates AFTER transformations(since before wouldnt make sense else box wouldnt be where the model is)
        Vec3 min = new Vec3();
        Vec3 max = new Vec3();

        //for each face we create a polygon based off of the faces index telling us which vertices belong to it
        //additionally we transform them here since doing it after would be a pain
        for (int[] face : faces) {
            Vec3 v1 = transform.multVec3(vertices.get(face[0]), true);
            Vec3 v2 = transform.multVec3(vertices.get(face[1]), true);
            Vec3 v3 = transform.multVec3(vertices.get(face[2]), true);

            Vec3[] transformedVertices = {v1, v2, v3};
            updateBoundingBox(min, max, transformedVertices);

            polymesh.addPolygon(new Polygon(v1, v2, v3, material));
        }

        //save the bounding box inside of the mesh shape
        BoundingBox box = new BoundingBox(polymesh, min, max);

        return box;
    }

    //reads the index out for us to find out which vertices belong together to form a face
    // used to read stuff like f 302/353/302 (at bottom of .obj)
    private static int parseIndex(String token) {
        return Integer.parseInt(token.split("/")[0]) - 1;
    }

    private static void updateBoundingBox(Vec3 _min, Vec3 _max, Vec3[] vertices){
        for (Vec3 vert : vertices) {
            if(vert.x < _min.x)
                _min.x = vert.x;
            if(vert.x > _max.x)
                _max.x = vert.x;

            if(vert.y < _min.y)
                _min.y = vert.y;
            if(vert.y > _max.y)
                _max.y = vert.y;

            if(vert.z < _min.z)
                _min.z = vert.z;
            if(vert.z > _max.z)
                _max.z = vert.z;
        }
    }


    //trying to make a 2nd version where we dont input material and instead it takes it from a .mtl file
    public static BoundingBox loadWithTextures(InputStream input, InputStream matInput, Vec3 position, Vec3 rotation, float scale) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        //need one array for the vertices/corners and one for the faces
        List<Vec3> vertices = new ArrayList<>();
        List<int[]> faces = new ArrayList<>();
        //type int[] to pair index slots so that 3 vertices can belong to each face
        List<String> faceMatNames = new ArrayList<>();
        List<Vec2> textureCoords = new ArrayList<>();
        

        Map<String, Material> allMats = MtlImporter.loadMats(matInput);

        String line;
        String currentMatName = "";

        //go through line by line
        while ((line = reader.readLine()) != null) {

            line = line.trim();

            //ignore empty lines and comments
            if (line.isEmpty() || line.startsWith("#"))
                continue;

            //------------- material names ---------------------------
            //if a line marks the beginning of a new material we gotta save its name in order to be able to call it from our map
            if(line.startsWith("usemtl")){
                String[] s = line.split("\\s+");
                currentMatName = s[1];
                //here we just save it in a var and then once we save the face we save this in the same slot of another arraylist
            }

            //------ vertice textures ---------------
            //save all vertice textures in another array so that we can go through those with the same index later 
            if(line.startsWith("vt")){
                String[] s = line.split("\\s+");

                float u = (float) Double.parseDouble(s[1]);
                float v = (float) Double.parseDouble(s[2]);

                textureCoords.add(new Vec2(u, v));
            }

            //------------ vertices(corners) ---------------------------
            //look for vertices aka lines starting with v
            if (line.startsWith("v ")) {

                String[] s = line.split("\\s+");

                float x = (float) Double.parseDouble(s[1]);
                float y = (float) Double.parseDouble(s[2]);
                float z = (float) Double.parseDouble(s[3]);

                vertices.add(new Vec3(x, y, z));
            }

            //------- faces bzw Indexes that tell us which vertices make a face together-------------
            //look for faces aka lines starting with an f
            else if (line.startsWith("f ")) {

                String[] s = line.split("\\s+");

                //safety check since we only want polygons
                if (s.length != 4)
                    continue;

                //grab the index info
                int i1 = parseIndex(s[1]);
                int i2 = parseIndex(s[2]);
                int i3 = parseIndex(s[3]);

                //based on index info we find out which vertices together build a face
                faces.add(new int[]{i1, i2, i3});
                faceMatNames.add(currentMatName);
            }
        }
        //close stream to avoid issues like in oop
        reader.close();

        //create a transformation matrix to apply on all polygons before saving em
        Vec4 homoPosition = new Vec4(position.x, position.y, position.z, 1);
        Matrix4x4 transform = new Matrix4x4().multScalar(scale);
        transform = transform.mult(Matrix4x4.rotation(rotation));
        transform = transform.translateXYZW(homoPosition);

        //create a mesh to save all polygons inside of, imo cleaner than saving all of em in shapelist
        PolygonMesh polymesh = new PolygonMesh();

        //additionally to the Mesh we'll need a bounding box around it to increase performance
        //the box will be based on the min and max values of all coordinates AFTER transformations(since before wouldnt make sense else box wouldnt be where the model is)
        Vec3 min = new Vec3();
        Vec3 max = new Vec3();

        //for each face we create a polygon based off of the faces index telling us which vertices belong to it
        //additionally we transform them here since doing it after would be a pain
        /*for (int[] face : faces) {
            Vec3 v1 = transform.multVec3(vertices.get(face[0]), true);
            Vec3 v2 = transform.multVec3(vertices.get(face[1]), true);
            Vec3 v3 = transform.multVec3(vertices.get(face[2]), true);

            Vec3[] transformedVertices = {v1, v2, v3};
            updateBoundingBox(min, max, transformedVertices);

            polymesh.addPolygon(new Polygon(v1, v2, v3, newMat));
        }*/

        //need a different loop here prob a for loop to be able to use the index in both array
        for(int i=0; i < faces.size(); i++){
            //get current face
            int[] face = faces.get(i);

            //get current mat
            String matName = faceMatNames.get(i);
            Material mat = allMats.get(matName);

            //maybe here try and calc diffuse off of map if map exists
            if(mat.getDiffuseTextureMap() != null){
                Vec2 uv1 = textureCoords.get(face[0]);
                Vec2 uv2 = textureCoords.get(face[1]);
                Vec2 uv3 = textureCoords.get(face[2]);
                Vec2 uvAvg = new Vec2((uv1.x + uv2.x + uv3.x) /3f, (uv1.y + uv2.y + uv3.y) /3f);
                //das sind dann unsere texture coords fürs gesamte polygon

                RgbColor colorAtAvg = sampleUV(mat.getDiffuseTextureMap(), uvAvg);
                mat.setDiffuseK(colorAtAvg);
            }
            


            Vec3 v1 = transform.multVec3(vertices.get(face[0]), true);
            Vec3 v2 = transform.multVec3(vertices.get(face[1]), true);
            Vec3 v3 = transform.multVec3(vertices.get(face[2]), true);

            Vec3[] transformedVertices = {v1, v2, v3};
            updateBoundingBox(min, max, transformedVertices);

            polymesh.addPolygon(new Polygon(v1, v2, v3, mat));
        }

        //save the bounding box inside of the mesh shape
        BoundingBox box = new BoundingBox(polymesh, min, max);

        return box;
    }

    private static RgbColor sampleUV(BufferedImage image, Vec2 uvCoords){
        int x = (int)(uvCoords.x * (image.getWidth() - 1));
        int y = (int)((1 - uvCoords.y) * (image.getHeight() - 1)); // weil Bilder meist oben beginnen

        int rgb = image.getRGB(x, y);

        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        return new RgbColor(
            r / 255f,
            g / 255f,
            b / 255f
        );
    }
}