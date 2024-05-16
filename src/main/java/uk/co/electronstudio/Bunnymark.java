package uk.co.electronstudio;
import com.raylib.Color;
import com.raylib.jextract.Texture;
import com.raylib.jextract.Vector2;

import java.lang.foreign.*;

import static com.raylib.Raylib.*;




public class Bunnymark {

    static int MAX_BUNNIES    =    500000 ;

    static class Bunny {
        Vec2 position = new Vec2();
        Vec2 speed = new Vec2();
        Color color = new Color();
    }

    static class Vec2{
        float x;
        float y;
    }


    public static void main(String[] args) {
        System.out.println("Hello world!");


        try (Arena arena = Arena.ofConfined()) {

                System.out.println(System.getProperty("java.vm.name"));
                System.out.println(System.getProperty("java.vm.vendor"));
                System.out.println(System.getProperty("java.vm.version"));
                // Initialization
                //--------------------------------------------------------------------------------------
                int screenWidth = 1920;
                int screenHeight = 1080;

                initWindow(screenWidth, screenHeight,  "raylib [textures] example - bunnymark");

                // Load bunny texture
                var texBunny = loadTexture(Util.extractFileFromResources("wabbit_alpha",".png"));

                Bunny[] bunnies = new Bunny[MAX_BUNNIES];
                for (int i=0; i < MAX_BUNNIES; i++){
                    bunnies[i] = new Bunny();
                }

                int bunniesCount = 0;           // Bunnies counter

                setTargetFPS(60);               // Set our game to run at 60 frames-per-second
                //--------------------------------------------------------------------------------------

                // Main game loop
                while (!windowShouldClose())    // Detect window close button or ESC key
                {
                    // Update
                    //----------------------------------------------------------------------------------
                    if (isMouseButtonDown(0))
                    {
                        // Create more bunnies
                        for (int i = 0; i < 100; i++)
                        {
                            if (bunniesCount < MAX_BUNNIES)
                            {
                                bunnies[bunniesCount].position.x = Vector2.x(getMousePosition());
                                bunnies[bunniesCount].position.y = Vector2.y(getMousePosition());
                                bunnies[bunniesCount].speed.x = (float)(getRandomValue(-250, 250)/60.0f);
                                bunnies[bunniesCount].speed.y = (float)(getRandomValue(-250, 250)/60.0f);
                                bunnies[bunniesCount].color = new Color((byte)getRandomValue(50, 240),
                                        (byte)getRandomValue(80, 240),
                                        (byte)getRandomValue(100, 240), (byte)255 );
                                bunniesCount++;
                            }
                        }
                    }

                    float width = Texture.width(texBunny);
                    float height = Texture.height(texBunny);
                    // Update bunnies
                    for (int i = 0; i < bunniesCount; i++)
                    {
                        bunnies[i].position.x += bunnies[i].speed.x;
                        bunnies[i].position.y += bunnies[i].speed.y;

                        if (((bunnies[i].position.x + width/2.0) > screenWidth) ||
                                ((bunnies[i].position.x + width/2.0) < 0)){
                            bunnies[i].speed.x *= -1;
                        }
                        if (((bunnies[i].position.y + height/2.0) > screenHeight) ||
                                ((bunnies[i].position.y + height/2.0 - 40) < 0)) {
                            bunnies[i].speed.y *= -1;
                        }
                    }
                    //----------------------------------------------------------------------------------

                    // Draw
                    //----------------------------------------------------------------------------------
                    beginDrawing();

                    var black = new com.raylib.Color((byte)0,(byte)0,(byte)0,(byte)255);
                    var white = new com.raylib.Color((byte)255,(byte)255,(byte)255,(byte)255);
                    clearBackground(white.memorySegment );

                    for (int i = 0; i < bunniesCount; i++)
                    {
                        drawTexture(texBunny, (int)bunnies[i].position.x, (int)bunnies[i].position.y, bunnies[i].color.memorySegment);
                    }

                    drawRectangle(0, 0, screenWidth, 40, black.memorySegment);
                    drawText("bunnies: "+bunniesCount, 120, 10, 20, white.memorySegment);

                    drawFPS(10, 10);

                    endDrawing();

                }



                unloadTexture(texBunny);

                closeWindow();




        }

    }
}