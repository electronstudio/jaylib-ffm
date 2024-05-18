package uk.co.electronstudio;


import com.raylib.*;


import static com.raylib.Raylib.*;
import static com.raylib.Raylib.CameraMode.CAMERA_ORBITAL;
import static com.raylib.jextract.raylib_h.rlGetVersion;


public class Test {
    public static void main(String args[]) {
        initWindow(800, 450, "Hello world");
        setTargetFPS(60);

        System.out.println("RLGL TEST: "+rlGetVersion());
//
        Camera3D camera = new Camera3D(new Vector3(18,16,18),
                new Vector3(),
                new Vector3(0,1,0), 45, 0);

        Image image = loadImage("heightmap.png");
        Texture texture = loadTextureFromImage(image);
        Mesh mesh = genMeshHeightmap(image,new Vector3(16, 8, 16));
        Model model = loadModelFromMesh(mesh);

        var mats = com.raylib.jextract.Model.materials(model.memorySegment);
        System.out.println(mats);
        var maps = com.raylib.jextract.Material.maps(mats);
        var matmap = com.raylib.jextract.MaterialMap.asSlice(maps, 0);
        com.raylib.jextract.MaterialMap.texture(matmap, texture.memorySegment);

        //model.materials.maps().position(0).texture(texture);
        unloadImage(image);

        while(!windowShouldClose()){
            updateCamera(camera, CAMERA_ORBITAL); //fixme
            beginDrawing();
            clearBackground(RAYWHITE);
            beginMode3D(camera);
            drawModel(model, new Vector3(-8,0,-8), 1, RED);
            drawGrid(20, 1.0f);
            endMode3D();
            drawText("Hello world", 190, 200, 20, VIOLET);
            drawFPS(20, 20);
            endDrawing();
        }
        closeWindow();
    }


}
