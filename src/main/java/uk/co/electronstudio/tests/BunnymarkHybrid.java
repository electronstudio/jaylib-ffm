// 137000
// 140000
// 147000

package uk.co.electronstudio.tests;
import com.raylib.Color;
import uk.co.electronstudio.Util;


import java.lang.foreign.*;

import static com.raylib.Raylib.*;
import static com.raylib.jextract.raylib_h_1.*;


public class BunnymarkHybrid {

    static int MAX_BUNNIES = 500000;

    static class Bunny {
        Vec2 position = new Vec2();
        Vec2 speed = new Vec2();
        Color color = null;//new Color();
    }

    static class Vec2 {
        float x;
        float y;
    }


    public static void main(String[] args) {
        System.out.println("Hello world!");


        try (Arena arena = Arena.ofConfined()) {  // you dont need to do this, it will create auto arena if you dont, but perhaps its faster if manage our own?

            System.out.println(System.getProperty("java.vm.name"));
            System.out.println(System.getProperty("java.vm.vendor"));
            System.out.println(System.getProperty("java.vm.version"));
            // Initialization
            //--------------------------------------------------------------------------------------
            int screenWidth = 1920;
            int screenHeight = 1080;

            initWindow(screenWidth, screenHeight, "raylib [textures] example - bunnymark");

            // Load bunny texture
            var texBunny = loadTexture(arena, Util.extractFileFromResources("wabbit_alpha", ".png"));
            var texBunnySeg = texBunny.memorySegment;
            var white = WHITE.memorySegment;
            var black = BLACK.memorySegment;

            Bunny[] bunnies = new Bunny[MAX_BUNNIES];
            for (int i = 0; i < MAX_BUNNIES; i++) {
                bunnies[i] = new Bunny();
            }

            int bunniesCount = 0;           // Bunnies counter
            var bunniesText = arena.allocateFrom("Bunnies: 0");
            var bunniesTextStr = "";

            setTargetFPS(60);               // Set our game to run at 60 frames-per-second
            //--------------------------------------------------------------------------------------
            float width = texBunny.getWidth();
            float height = texBunny.getHeight();
            // Main game loop
            while (!WindowShouldClose())    // Detect window close button or ESC key
            {
                // Update
                //----------------------------------------------------------------------------------
                if (IsMouseButtonDown(0)) {
                    // Create more bunnies
                    for (int i = 0; i < 200; i++) {
                        if (bunniesCount < MAX_BUNNIES) {
                            bunnies[bunniesCount].position.x = getMousePosition(arena).getX();
                            bunnies[bunniesCount].position.y = getMousePosition(arena).getY();
                            bunnies[bunniesCount].speed.x = (float) (getRandomValue(-250, 250) / 60.0f);
                            bunnies[bunniesCount].speed.y = (float) (getRandomValue(-250, 250) / 60.0f);
                            bunnies[bunniesCount].color = new Color(arena, (byte) getRandomValue(50, 240),
                                    (byte) getRandomValue(80, 240),
                                    (byte) getRandomValue(100, 240), (byte) 255);
                            bunniesCount++;
                            bunniesTextStr = "Bunnies: " + bunniesCount;
                            bunniesText = arena.allocateFrom(bunniesTextStr);
                        }
                    }
                }


                // Update bunnies
                for (int i = 0; i < bunniesCount; i++) {
                    bunnies[i].position.x += bunnies[i].speed.x;
                    bunnies[i].position.y += bunnies[i].speed.y;

                    if (((bunnies[i].position.x + width / 2.0) > screenWidth) ||
                            ((bunnies[i].position.x + width / 2.0) < 0)) {
                        bunnies[i].speed.x *= -1;
                    }
                    if (((bunnies[i].position.y + height / 2.0) > screenHeight) ||
                            ((bunnies[i].position.y + height / 2.0 - 40) < 0)) {
                        bunnies[i].speed.y *= -1;
                    }
                }

                BeginDrawing();
                ClearBackground(WHITE.memorySegment);
                for (int i = 0; i < bunniesCount; i++) {
                    //DrawTexture(texBunny.memorySegment, (int)bunnies[i].position.x, (int)bunnies[i].position.y, bunnies[i].color.memorySegment);
                    drawTexture(texBunny, (int) bunnies[i].position.x, (int) bunnies[i].position.y, bunnies[i].color);
                }
                DrawRectangle(0, 0, screenWidth, 40, BLACK.memorySegment);
                //DrawText(bunniesText, 120, 10, 20, WHITE.memorySegment);
                drawText(bunniesTextStr, 120, 10, 20, WHITE);
                DrawFPS(10, 10);
                EndDrawing();

            }


            unloadTexture(texBunny);

            closeWindow();


        }

    }
}