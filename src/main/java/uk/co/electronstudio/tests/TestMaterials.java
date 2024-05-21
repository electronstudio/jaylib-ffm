package uk.co.electronstudio.tests;


import com.raylib.*;


import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.raylib.Raylib.*;
import static com.raylib.Raylib.CameraMode.CAMERA_ORBITAL;
import static com.raylib.jextract.raylib_h.rlGetVersion;
import static java.lang.foreign.ValueLayout.JAVA_INT;


public class TestMaterials {
    public static void main(String args[]) {
        initWindow(800, 450, "Hello world");
        setTargetFPS(60);

        System.out.println("RLGL TEST: "+rlGetVersion());
        System.out.println(""+JAVA_INT.byteSize());
        var fb =FloatBuffer.allocate(1);
        var bb = ByteBuffer.allocateDirect((int)JAVA_INT.byteSize()).order(ByteOrder.nativeOrder()).asIntBuffer();
        var ba = new byte[4];
        //var bb = ByteBuffer.wrap(ba);

        var ib = createIntBuffer(1);
        loadFileData("heightmap.png",  ib);

        System.out.printf("filesize "+ib.get(0));

        Camera3D camera = new Camera3D(new Vector3(18,16,18),
                new Vector3(),
                new Vector3(0,1,0), 45, 0);

        Image image = loadImage("heightmap.png");
        Texture texture = loadTextureFromImage(image);
        Mesh mesh = genMeshHeightmap(image,new Vector3(16, 8, 16));
        Model model = loadModelFromMesh(mesh);
        var matmap = model.getMaterials().getArrayElement(0).getMaps().getArrayElement(0);
        matmap.setTexture(texture);


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
