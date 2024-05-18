//151000

package uk.co.electronstudio.tests;

import com.raylib.jextract.Color;
import com.raylib.jextract.Texture;
import com.raylib.jextract.Vector2;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static com.raylib.jextract.raylib_h.*;

public class BunnymarkJextract {
    static int MAX_BUNNIES    =    500000 ;


    static int MAX_BATCH_ELEMENTS = 8192;

    static class Bunny {
        Vec2 position = new Vec2();
        Vec2 speed = new Vec2();
        MemorySegment color = null;
    }

    static class Vec2{
        float x;
        float y;
    }

    static class Col{
        int r;
        int g;
        int b;
        int a;
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
        try (Arena arena = Arena.ofConfined()) {
            // Allocate a region of off-heap memory to store pointers



            System.out.println(System.getProperty("java.vm.name"));
            System.out.println(System.getProperty("java.vm.vendor"));
            System.out.println(System.getProperty("java.vm.version"));
            // Initialization
            //--------------------------------------------------------------------------------------
            int screenWidth = 1920;
            int screenHeight = 1080;

            InitWindow(screenWidth, screenHeight,  arena.allocateFrom("raylib [textures] example - bunnymark"));

            // Load bunny texture
            var texBunny = LoadTexture(arena, arena.allocateFrom("src/main/resources/wabbit_alpha.png"));

            Bunny[] bunnies = new Bunny[MAX_BUNNIES];
            for (int i=0; i < MAX_BUNNIES; i++){
                bunnies[i] = new Bunny();
            }

            int bunniesCount = 0;           // Bunnies counter
            var bunniesText = arena.allocateFrom("Bunnies: 0");

            float width = Texture.width(texBunny);
            float height = Texture.height(texBunny);

            var black = Color.allocate(arena);
            Color.a(black, (byte)255);
            var white = Color.allocate(arena);
            Color.r(white, (byte) 255);
            Color.g(white, (byte) 255);
            Color.b(white, (byte) 255);
            Color.a(white, (byte) 255);

            SetTargetFPS(60);               // Set our game to run at 60 frames-per-second
            //--------------------------------------------------------------------------------------

            // Main game loop
            while (!WindowShouldClose())    // Detect window close button or ESC key
            {
                // Update
                //----------------------------------------------------------------------------------
                if (IsMouseButtonDown(0))
                {
                    // Create more bunnies
                    for (int i = 0; i < 200; i++)
                    {
                        if (bunniesCount < MAX_BUNNIES)
                        {
                            bunnies[bunniesCount].position.x = Vector2.x(GetMousePosition(arena));
                            bunnies[bunniesCount].position.y = Vector2.y(GetMousePosition(arena));
                            bunnies[bunniesCount].speed.x = (float)(GetRandomValue(-250, 250)/60.0f);
                            bunnies[bunniesCount].speed.y = (float)(GetRandomValue(-250, 250)/60.0f);
                            var color = Color.allocate(arena);
                            Color.r(color, (byte) GetRandomValue(50, 240));
                            Color.g(color, (byte) GetRandomValue(80, 240));
                            Color.b(color, (byte) GetRandomValue(100, 240));
                            Color.a(color, (byte) 255);
                            bunnies[bunniesCount].color = color;

                            bunniesCount++;
                            bunniesText = arena.allocateFrom("Bunnies: "+bunniesCount);

                    //        System.out.println(bunniesCount);
                        }
                    }
                }


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
                BeginDrawing();


                ClearBackground(white );

                for (int i = 0; i < bunniesCount; i++)
                {

                    DrawTexture(texBunny, (int)bunnies[i].position.x, (int)bunnies[i].position.y, bunnies[i].color);
                }

                DrawRectangle(0, 0, screenWidth, 40, black);
                DrawText(bunniesText, 120, 10, 20, white);
                DrawFPS(10, 10);
                EndDrawing();

            }

            // De-Initialization
            //--------------------------------------------------------------------------------------


            UnloadTexture(texBunny);    // Unload bunny texture

            CloseWindow();              // Close window and OpenGL context
            //--------------------------------------------------------------------------------------




        }

    }
}