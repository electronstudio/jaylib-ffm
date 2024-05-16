package uk.co.electronstudio;


import com.raylib.*;


import static com.raylib.Raylib.*;


public class Test {
    public static void main(String args[]) {
        initWindow(800, 450, "Hello world");
        setTargetFPS(60);

//        System.out.println("RLGL TEST: "+rlGetVersion());
//
        Camera3D camera = new Camera3D(new Vector3(18,16,18).memorySegment,
                new Vector3().memorySegment,
                new Vector3(0,1,0).memorySegment, 45, 0);

        Image image = new Image(loadImage("heightmap.png"));
        Texture texture = new Texture(loadTextureFromImage(image.memorySegment));
        Mesh mesh = new Mesh(genMeshHeightmap(image.memorySegment, new Vector3(16, 8, 16).memorySegment));
        Model model = new Model(loadModelFromMesh(mesh.memorySegment));

        var mats = com.raylib.jextract.Model.materials(model.memorySegment);
        System.out.println(mats);
        var maps = com.raylib.jextract.Material.maps(mats);
        var matmap = com.raylib.jextract.MaterialMap.asSlice(maps, 0);
        com.raylib.jextract.MaterialMap.texture(matmap, texture.memorySegment);

        //model.materials.maps().position(0).texture(texture);
        unloadImage(image.memorySegment);

        while(!windowShouldClose()){
            updateCamera(camera.memorySegment, CAMERA_ORBITAL);
            beginDrawing();
            clearBackground(RAYWHITE.memorySegment);
            beginMode3D(camera.memorySegment);
            drawModel(model.memorySegment, new Vector3(-8,0,-8).memorySegment, 1, RED.memorySegment);
            drawGrid(20, 1.0f);
            endMode3D();
            drawText("Hello world", 190, 200, 20, VIOLET.memorySegment);
            drawFPS(20, 20);
            endDrawing();
        }
        closeWindow();
    }


}
