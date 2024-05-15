package uk.co.electronstudio;

import com.raylib.jextract.Color;
import com.raylib.jextract.Texture;
import com.raylib.jextract.Vector2;

import java.lang.foreign.Arena;

import static com.raylib.jextract.raylib_h.*;

public class MainOld {
    static int MAX_BUNNIES    =    500000 ;


    static int MAX_BATCH_ELEMENTS = 8192;

    static class Bunny {
        Vec2 position = new Vec2();
        Vec2 speed = new Vec2();
        //Color color = new Color();
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
                var texBunny = LoadTexture(arena, arena.allocateFrom("wabbit_alpha.png"));

                Bunny[] bunnies = new Bunny[MAX_BUNNIES];
                for (int i=0; i < MAX_BUNNIES; i++){
                    bunnies[i] = new Bunny();
                }

                int bunniesCount = 0;           // Bunnies counter

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
                        for (int i = 0; i < 100; i++)
                        {
                            if (bunniesCount < MAX_BUNNIES)
                            {
                                bunnies[bunniesCount].position.x = Vector2.x(GetMousePosition(arena));
                                bunnies[bunniesCount].position.y = Vector2.y(GetMousePosition(arena));
                                bunnies[bunniesCount].speed.x = (float)(GetRandomValue(-250, 250)/60.0f);
                                bunnies[bunniesCount].speed.y = (float)(GetRandomValue(-250, 250)/60.0f);
//                                bunnies[bunniesCount].color = new Jaylib.Color(GetRandomValue(50, 240),
//                                        GetRandomValue(80, 240),
//                                        GetRandomValue(100, 240), 255 );
                                bunniesCount++;
                                System.out.println(bunniesCount);
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
                    BeginDrawing();

                    var black = Color.allocate(arena);
                    Color.a(black, (byte)255);
                    var white = Color.allocate(arena);
                    Color.r(white, (byte) 255);
                    Color.g(white, (byte) 255);
                    Color.b(white, (byte) 255);
                    Color.a(white, (byte) 255);
                    ClearBackground(white );

                    for (int i = 0; i < bunniesCount; i++)
                    {
                        // NOTE: When internal batch buffer limit is reached (MAX_BATCH_ELEMENTS),
                        // a draw call is launched and buffer starts being filled again;
                        // before issuing a draw call, updated vertex data from internal CPU buffer is send to GPU...
                        // Process of sending data is costly and it could happen that GPU data has not been completely
                        // processed for drawing while new data is tried to be sent (updating current in-use buffers)
                        // it could generates a stall and consequently a frame drop, limiting the number of drawn bunnies
                        DrawTexture(texBunny, (int)bunnies[i].position.x, (int)bunnies[i].position.y, black);
                    }

//                    DrawRectangle(0, 0, screenWidth, 40, BLACK);
//                    DrawText(TextFormat("bunnies: "+bunniesCount), 120, 10, 20, GREEN);
//                    DrawText(TextFormat("batched draw calls: " + (1+bunniesCount/MAX_BATCH_ELEMENTS)), 320, 10, 20, MAROON);

                    DrawFPS(10, 10);

                    EndDrawing();
                    //----------------------------------------------------------------------------------
                }

                // De-Initialization
                //--------------------------------------------------------------------------------------


                UnloadTexture(texBunny);    // Unload bunny texture

                CloseWindow();              // Close window and OpenGL context
                //--------------------------------------------------------------------------------------




        }

    }
}